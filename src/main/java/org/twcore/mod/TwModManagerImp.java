package org.twcore.mod;

import org.twcore.api.TwModManager;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * {@link TwModManager} 的唯一实现，单例。
 * <p>
 * 内部维护：
 * <ul>
 *   <li>已注册模组映射 (modId → 注册的整数版本)</li>
 *   <li>每个模组的最低要求版本映射 (modId → 最低整数版本)</li>
 * </ul>
 */
public final class TwModManagerImp implements TwModManager {

    // ---------- 单例 ----------
    private static final class Holder {
        static final TwModManagerImp INSTANCE = new TwModManagerImp();
    }

    public static TwModManagerImp getInstance() {
        return Holder.INSTANCE;
    }

    // ---------- 数据 ----------
    private final Map<String, Integer> registeredMods;
    private final Map<String, Integer> minRequiredVersions;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private TwModManagerImp() {
        registeredMods = new LinkedHashMap<>(); // 保留注册顺序
        minRequiredVersions = new HashMap<>();
        initMinRequirements();
    }

    private void initMinRequirements() {
    }

    // ---------- 公共方法实现 ----------
    @Override
    public void register(String modId, int modVersion) {
        Objects.requireNonNull(modId, "modId cannot be null");
        if (modVersion <= 0) {
            throw new IllegalArgumentException("modVersion must be >= 1, got: " + modVersion);
        }

        lock.writeLock().lock();
        try {
            if (registeredMods.containsKey(modId)) {
                throw new IllegalArgumentException(
                        "Mod '" + modId + "' is already registered."
                );
            }

            int required = minRequiredVersions.getOrDefault(modId, 1);
            if (modVersion < required) {
                // 使用可翻译文本抛出异常
                throw TwModManagerException.of(
                        modId, modVersion, required
                );
            }

            registeredMods.put(modId, modVersion);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isRegistered(String modId) {
        lock.readLock().lock();
        try {
            return registeredMods.containsKey(modId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getRegisteredVersion(String modId) {
        lock.readLock().lock();
        try {
            return registeredMods.getOrDefault(modId, -1);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Map<String, Integer> getRegisteredMods() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableMap(new LinkedHashMap<>(registeredMods));
        } finally {
            lock.readLock().unlock();
        }
    }
}