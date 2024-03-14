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
 * Manages the lock state of vaults in the MarketCraft plugin.
 * The lock mechanism prevents simultaneous modification issues and maintains
 * consistency across the vault's data.
 */
public class VaultLockManager {
    private final Map<UUID, Map<String, Set<UUID>>> vaultStatus = new ConcurrentHashMap<>();

    /**
     * Locks a vault for a player. Adds the player's UUID to the set of players who have the vault open.
     *
     * @param vaultOwnerUUID The UUID of the owner of the vault.
     * @param vaultName      The name of the vault to be locked.
     * @param playerUUID     The UUID of the player who is locking the vault.
     */
    public synchronized void lockVault(UUID vaultOwnerUUID, String vaultName, UUID playerUUID) {
        vaultStatus.computeIfAbsent(vaultOwnerUUID, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(vaultName, k -> new HashSet<>())
                .add(playerUUID);
    }

    /**
     * Unlocks a vault for a player. Removes the player's UUID from the set of players who have the vault open.
     * The vault is unlocked only when the set becomes empty, indicating no players have it open.
     *
     * @param vaultOwnerUUID The UUID of the owner of the vault.
     * @param vaultName      The name of the vault to be unlocked.
     * @param playerUUID     The UUID of the player who is unlocking the vault.
     */
    public synchronized void unlockVault(UUID vaultOwnerUUID, String vaultName, UUID playerUUID) {
        Map<String, Set<UUID>> vaultMap = vaultStatus.get(vaultOwnerUUID);
        if (vaultMap != null) {
            Set<UUID> playersWithAccess = vaultMap.get(vaultName);
            if (playersWithAccess != null) {
                playersWithAccess.remove(playerUUID);
                if (playersWithAccess.isEmpty()) {
                    vaultMap.remove(vaultName);
                }
            }
        }
    }

    /**
     * Checks if a vault is locked.
     *
     * @param vaultOwnerUUID The UUID of the owner of the vault.
     * @param vaultName      The name of the vault to be checked.
     * @return true if the vault is locked (i.e., if any players have it open), false otherwise.
     */
    public boolean isLocked(UUID vaultOwnerUUID, String vaultName) {
        Map<String, Set<UUID>> vaultMap = vaultStatus.get(vaultOwnerUUID);
        return vaultMap != null && !vaultMap.getOrDefault(vaultName, new HashSet<>()).isEmpty();
    }
}