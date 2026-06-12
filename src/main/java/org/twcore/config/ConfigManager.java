package org.twcore.config;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.twcore.TWCore;
import org.twcore.api.TwModManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ConfigManager {
    private static final Logger LOGGER = TWCore.LOGGER;
    private static final Path BASE_DIR = Path.of("config", "tenacious-wonder");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 已注册的配置条目（配置元信息）
    private static final Map<ModConfigKey, ConfigEntry<?>> entries = new LinkedHashMap<>();
    // 运行时数据缓存
    private static final Map<ModConfigKey, Object> dataCache = new ConcurrentHashMap<>();
    // 待处理的影响器池，key 为目标配置标识，value 为影响器列表
    private static final Map<ModConfigKey, List<ConfigInfluencer>> pendingInfluencers = new HashMap<>();

    private ConfigManager() {}

    // ----- 注册阶段 -----

    public static synchronized void registerConfig(String modId, ConfigType<?> type) {
        ModConfigKey key = new ModConfigKey(modId, type.name());
        if (entries.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate config registration: " + key);
        }
        entries.put(key, new ConfigEntry<>(type));
    }

    public static synchronized void addInfluencer(String targetModId, String configName, ConfigInfluencer influencer) {
        ModConfigKey key = new ModConfigKey(targetModId, configName);
        // 无论目标是否存在，一律存入待处理池
        pendingInfluencers.computeIfAbsent(key, k -> new ArrayList<>()).add(influencer);
    }

    // ----- 统一加载阶段 -----

    public static synchronized void loadAll() {
        if (entries.isEmpty()) return;
        try {
            Files.createDirectories(BASE_DIR);
        } catch (IOException e) {
            LOGGER.error("Failed to create base config directory", e);
            return;
        }

        for (Map.Entry<ModConfigKey, ConfigEntry<?>> mapEntry : entries.entrySet()) {
            ModConfigKey key = mapEntry.getKey();
            ConfigEntry<?> entry = mapEntry.getValue();
            loadSingleConfig(key, entry);
        }

        // 处理完毕，释放影响器池
        pendingInfluencers.clear();
    }

    private static <T> void loadSingleConfig(ModConfigKey key, ConfigEntry<T> entry) {
        ConfigType<T> type = entry.type;
        Path file = BASE_DIR.resolve(key.modId()).resolve(key.configName() + ".json");

        // 1. 从待处理池取出当前配置的所有影响器（可能为空）
        List<ConfigInfluencer> rawInfluencers = pendingInfluencers.getOrDefault(key, Collections.emptyList());

        // 2. 筛选有效影响器：来源模组必须已在 TwModManager 中注册
        List<ConfigInfluencer> validInfluencers = rawInfluencers.stream()
                .filter(inf -> TwModManager.IMPL.isRegistered(inf.sourceModId()))
                .toList();

        // 3. 计算有效版本号
        int sourceVersion = TwModManager.IMPL.getRegisteredVersion(key.modId());
        int influencerVersionSum = validInfluencers.stream()
                .mapToInt(ConfigInfluencer::sourceModVersion)
                .sum();
        int effectiveVersion = sourceVersion + influencerVersionSum;

        // 4. 生成最终默认值
        T finalDefault;
        try {
            finalDefault = type.defaultFactory().apply(validInfluencers);
        } catch (Exception e) {
            LOGGER.error("Default factory for config '{}' threw exception, falling back to factory with empty list", key, e);
            try {
                finalDefault = type.defaultFactory().apply(Collections.emptyList());
            } catch (Exception ex) {
                LOGGER.error("Critical: default factory for '{}' failed even with empty list", key, ex);
                return;
            }
        }

        // 5. 尝试从文件加载
        T data = null;
        if (Files.exists(file)) {
            try {
                String content = Files.readString(file, StandardCharsets.UTF_8);
                JsonElement json = JsonParser.parseString(content);
                if (!json.isJsonObject()) {
                    throw new IllegalStateException("Config file is not a JSON object");
                }
                JsonObject obj = json.getAsJsonObject();
                int fileVersion = obj.has("version") ? obj.get("version").getAsInt() : 0;
                obj.remove("version");

                if (fileVersion == effectiveVersion) {
                    DataResult<T> result = type.codec().parse(JsonOps.INSTANCE, obj);
                    Optional<T> parsed = result.resultOrPartial(error -> {
                        LOGGER.error("Failed to parse config '{}': {}", key, error);
                    });
                    if (parsed.isPresent()) {
                        data = parsed.get();
                    }
                } else if (fileVersion < effectiveVersion && type.migrator() != null) {
                    LOGGER.info("Migrating config '{}' from version {} to {}", key, fileVersion, effectiveVersion);
                    DataResult<T> migrated = type.migrator().migrate(obj, fileVersion);
                    Optional<T> result = migrated.resultOrPartial(error ->
                            LOGGER.error("Migration failed for '{}': {}", key, error));
                    if (result.isPresent()) {
                        data = result.get();
                    }
                } else {
                    LOGGER.info("Config '{}' version mismatch (file: {}, current: {}), using default", key, fileVersion, effectiveVersion);
                }
            } catch (Exception e) {
                LOGGER.error("Error reading/parsing config file '{}', will override with default", file, e);
            }
        }

        // 6. 若加载失败，使用最终默认值覆写
        if (data == null) {
            data = finalDefault;
        }

        // 7. 存入缓存并保存文件
        dataCache.put(key, data);
        saveConfig(key, data, effectiveVersion);
    }

    private static <T> void saveConfig(ModConfigKey key, T data, int version) {
        Path file = BASE_DIR.resolve(key.modId()).resolve(key.configName() + ".json");
        try {
            Files.createDirectories(file.getParent());
            ConfigEntry<?> entry = entries.get(key);
            if (entry == null) return;
            @SuppressWarnings("unchecked")
            Codec<T> codec = (Codec<T>) entry.type.codec();

            JsonElement json = codec.encodeStart(JsonOps.INSTANCE, data)
                    .getOrThrow(false, s -> LOGGER.error("Failed to encode config '{}': {}", key, s));
            JsonObject obj = ensureJsonObject(json);
            obj.addProperty("version", version);
            Files.writeString(file, GSON.toJson(obj), StandardCharsets.UTF_8);
            LOGGER.debug("Config '{}' saved (version {})", key, version);
        } catch (Exception e) {
            LOGGER.error("Failed to save config '{}'", key, e);
        }
    }

    // ----- 查询接口 -----

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T get(String modId, String configName) {
        return (T) dataCache.get(new ModConfigKey(modId, configName));
    }

    public static <T> void update(String modId, String configName, Function<T, T> updater) {
        ModConfigKey key = new ModConfigKey(modId, configName);
        T old = get(modId, configName);
        if (old == null) {
            throw new IllegalStateException("Config not loaded: " + key);
        }
        T newData = updater.apply(old);
        dataCache.put(key, newData);
        int sourceVersion = TwModManager.IMPL.getRegisteredVersion(modId);
        int influencerSum = pendingInfluencers.getOrDefault(key, Collections.emptyList()).stream()
                .filter(inf -> TwModManager.IMPL.isRegistered(inf.sourceModId()))
                .mapToInt(ConfigInfluencer::sourceModVersion)
                .sum();
        saveConfig(key, newData, sourceVersion + influencerSum);
    }

    private static JsonObject ensureJsonObject(JsonElement json) {
        if (json.isJsonObject()) {
            return json.getAsJsonObject();
        } else {
            JsonObject wrapper = new JsonObject();
            wrapper.add("value", json);
            return wrapper;
        }
    }

    private record ModConfigKey(String modId, String configName) {}

    private record ConfigEntry<T>(ConfigType<T> type) {}
}