package org.twcore.blockpile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.twcore.TWCore;
import org.twcore.api.blockpile.CubeBlockPile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方块堆数据的持久化存储
 */
public class CubeBlockPilePersistentState extends SavedData {
    private static final Logger LOGGER = TWCore.LOGGER;
    private static final String PERSISTENT_ID = "cubeBlockPiles";
    public static final Factory<CubeBlockPilePersistentState> TYPE =
            new Factory<>(CubeBlockPilePersistentState::new, CubeBlockPilePersistentState::fromNbt, DataFixTypes.LEVEL);

    /**
     * 临时存储的方块堆数据
     * @see CubeBlockPileManager
     */
    private final Map<ResourceLocation, Map<BlockPos, CubeBlockPileData>> worldData = new ConcurrentHashMap<>();

    public CubeBlockPilePersistentState() {
        super();
    }

    /**
     * 方块堆数据的序列化表示
     */
    public record CubeBlockPileData(BlockPos masterPos, String baseBlockId, BlockPos start, int width, int height,
                                    int depth) {
        public @NotNull CompoundTag toNbt() {
                CompoundTag nbt = new CompoundTag();
                nbt.put("masterPos", NbtUtils.writeBlockPos(masterPos));
                nbt.putString("baseBlockId", baseBlockId);
                nbt.put("start", NbtUtils.writeBlockPos(start));
                nbt.putInt("width", width);
                nbt.putInt("height", height);
                nbt.putInt("depth", depth);
                return nbt;
            }

        public static @NotNull CubeBlockPilePersistentState.CubeBlockPileData fromNbt(@NotNull CompoundTag nbt) {
            BlockPos masterPos = NbtUtils.readBlockPos(nbt, "masterPos").orElseThrow();
            String baseBlockId = nbt.getString("baseBlockId");
            BlockPos start = NbtUtils.readBlockPos(nbt, "start").orElseThrow();
            int width = nbt.getInt("width");
            int height = nbt.getInt("height");
            int depth = nbt.getInt("depth");
            return new CubeBlockPileData(masterPos, baseBlockId, start, width, height, depth);
        }
    }

    /**
     * 添加方块堆数据
     */
    public void addCubeBlockPile(@NotNull Level world, @NotNull CubeBlockPile cubeBlockPile) {
        ResourceLocation worldId = world.dimension().location();
        Map<BlockPos, CubeBlockPileData> worldMap = worldData
                .computeIfAbsent(worldId, k -> new ConcurrentHashMap<>());

        CubeBlockPileData data = new CubeBlockPileData(
                cubeBlockPile.getMasterPos(),
                BuiltInRegistries.BLOCK.getResourceKey(cubeBlockPile.getBaseBlock()).orElseThrow().location().toString(),
                cubeBlockPile.getRange().getStart(),
                cubeBlockPile.getRange().getWidth(),
                cubeBlockPile.getRange().getHeight(),
                cubeBlockPile.getRange().getDepth()
        );

        worldMap.put(cubeBlockPile.getMasterPos(), data);
        setDirty();
    }

    /**
     * 移除方块堆数据
     */
    public void removeCubeBlockPile(@NotNull Level world, BlockPos masterPos) {
        ResourceLocation worldId = world.dimension().location();
        Map<BlockPos, CubeBlockPileData> worldMap = worldData.get(worldId);
        if (worldMap != null) {
            worldMap.remove(masterPos);
            setDirty();
        }
    }

    /**
     * 获取世界中所有的方块堆数据
     */
    public Collection<CubeBlockPileData> getCubeBlockPilesForWorld(@NotNull Level world) {
        ResourceLocation worldId = world.dimension().location();
        Map<BlockPos, CubeBlockPileData> worldMap = worldData.get(worldId);
        return worldMap != null ? worldMap.values() : Collections.emptyList();
    }

    /**
     * 清除世界中的所有方块堆数据
     */
    public void clearWorldData(@NotNull Level world) {
        ResourceLocation worldId = world.dimension().location();
        worldData.remove(worldId);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        ListTag worldsList = new ListTag();

        for (Map.Entry<ResourceLocation, Map<BlockPos, CubeBlockPileData>> worldEntry : worldData.entrySet()) {
            CompoundTag worldNbt = new CompoundTag();
            worldNbt.putString("worldId", worldEntry.getKey().toString());

            ListTag cubeBlockPilesList = new ListTag();
            for (CubeBlockPileData data : worldEntry.getValue().values()) {
                cubeBlockPilesList.add(data.toNbt());
            }

            worldNbt.put("cubeBlockPiles", cubeBlockPilesList);
            worldsList.add(worldNbt);
        }

        nbt.put("worlds", worldsList);

        return nbt;
    }

    /**
     * 从NBT读取数据
     */
    public static @NotNull CubeBlockPilePersistentState fromNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        CubeBlockPilePersistentState state = new CubeBlockPilePersistentState();

        ListTag worldsList = nbt.getList("worlds", Tag.TAG_COMPOUND);
        for (Tag worldElement : worldsList) {
            CompoundTag worldNbt = (CompoundTag) worldElement;
            ResourceLocation worldId = ResourceLocation.parse(worldNbt.getString("worldId"));

            Map<BlockPos, CubeBlockPileData> worldMap = new ConcurrentHashMap<>();
            ListTag cubeBlockPilesList = worldNbt.getList("cubeBlockPiles", Tag.TAG_COMPOUND);

            for (Tag blockElement : cubeBlockPilesList) {
                CompoundTag blockNbt = (CompoundTag) blockElement;
                try {
                    CubeBlockPileData data = CubeBlockPileData.fromNbt(blockNbt);
                    worldMap.put(data.masterPos, data);
                } catch (Exception e) {
                    LOGGER.error("Failed to load CubeBlockPile data from NBT: {}", e.getMessage());
                }
            }

            state.worldData.put(worldId, worldMap);
        }

        return state;
    }

    /**
     * 获取或创建持久化状态
     */
    public static CubeBlockPilePersistentState getOrCreate(ServerLevel world) {
        DimensionDataStorage persistentStateManager = world.getDataStorage();
        return persistentStateManager.computeIfAbsent(TYPE, PERSISTENT_ID);
    }

    /**
     * 保存到文件（手动备份）
     */
    public void saveToFile(MinecraftServer server) {
        try {
            File worldDir = server.getWorldPath(LevelResource.ROOT).toFile();
            File backupFile = new File(worldDir, "cubeBlockPileblocks_backup.dat");

            CompoundTag nbt = this.save(new CompoundTag(), server.registryAccess());
            NbtIo.write(nbt, backupFile.toPath());
        } catch (IOException e) {
            LOGGER.error("Failed to backup CubeBlockPile data: {}", e.getMessage());
        }
    }
}