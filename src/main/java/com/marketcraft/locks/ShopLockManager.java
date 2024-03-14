/*
 * Marketcraft is licensed under the MIT License.
 *
 * Please view the full license here for more information:
 * https://github.com/Slimerblue22/MarketCraft/blob/main/LICENSE
 *
 * Copyright (c) 2024 Slimerblue22
 */

package com.marketcraft.locks;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the lock state of shops in the MarketCraft plugin.
 * The lock mechanism prevents simultaneous modification issues and maintains
 * consistency across the shop's data.
 */
public class ShopLockManager {
    private final Map<UUID, Map<String, Set<UUID>>> shopStatus = new ConcurrentHashMap<>();

    /**
     * Locks a shop for a player. Adds the player's UUID to the set of players who have the shop open.
     *
     * @param shopOwnerUUID The UUID of the owner of the shop.
     * @param shopName      The name of the shop to be locked.
     * @param playerUUID    The UUID of the player who is locking the shop.
     */
    public synchronized void lockShop(UUID shopOwnerUUID, String shopName, UUID playerUUID) {
        shopStatus.computeIfAbsent(shopOwnerUUID, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(shopName, k -> new HashSet<>())
                .add(playerUUID);
    }

    /**
     * Unlocks a shop for a player. Removes the player's UUID from the set of players who have the shop open.
     * The shop is unlocked only when the set becomes empty, indicating no players have it open.
     *
     * @param shopOwnerUUID The UUID of the owner of the shop.
     * @param shopName      The name of the shop to be unlocked.
     * @param playerUUID    The UUID of the player who is unlocking the shop.
     */
    public synchronized void unlockShop(UUID shopOwnerUUID, String shopName, UUID playerUUID) {
        Map<String, Set<UUID>> shopMap = shopStatus.get(shopOwnerUUID);
        if (shopMap != null) {
            Set<UUID> playersWithAccess = shopMap.get(shopName);
            if (playersWithAccess != null) {
                playersWithAccess.remove(playerUUID);
                if (playersWithAccess.isEmpty()) {
                    shopMap.remove(shopName);
                }
            }
        }
    }

    /**
     * Checks if a shop is locked.
     *
     * @param shopOwnerUUID The UUID of the owner of the shop.
     * @param shopName      The name of the shop to be checked.
     * @return true if the shop is locked (i.e., if any players have it open), false otherwise.
     */
    public boolean isLocked(UUID shopOwnerUUID, String shopName) {
        Map<String, Set<UUID>> shopMap = shopStatus.get(shopOwnerUUID);
        return shopMap != null && !shopMap.getOrDefault(shopName, new HashSet<>()).isEmpty();
    }
}