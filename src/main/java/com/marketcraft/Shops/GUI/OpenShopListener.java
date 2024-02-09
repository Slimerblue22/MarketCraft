package com.marketcraft.Shops.GUI;

import com.marketcraft.MarketCraft;
import com.marketcraft.Shops.ShopTransaction;
import com.marketcraft.Vaults.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class OpenShopListener implements Listener {
    private final ShopTransaction shopTransaction;
    private final MarketCraft marketCraft;
    private static final int CONFIRM_SLOT = 16;
    private static final int CANCEL_SLOT = 22;
    private static final int OWNER_HEAD_SLOT = 4;

    public OpenShopListener(PlayerVaultManager playerVaultManager, MarketCraft marketCraft) {
        this.shopTransaction = new ShopTransaction(playerVaultManager);
        this.marketCraft = marketCraft;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(Component.text("Shop"))) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getRawSlot();
            if (clickedSlot == CONFIRM_SLOT) { // Buy button slot
                // Retrieve the shop owner's UUID from the inventory
                // This is used within the shop transaction logic to add and remove items from the shop owner's vault
                ItemStack ownerIdentifier = event.getInventory().getItem(OWNER_HEAD_SLOT);
                ItemMeta meta = Objects.requireNonNull(ownerIdentifier).getItemMeta();
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(marketCraft, "shopOwnerUUID");
                String uuidString = dataContainer.get(key, PersistentDataType.STRING);
                UUID shopOwnerUUID = UUID.fromString(Objects.requireNonNull(uuidString));
                // Sending the necessary information to the transaction handler
                shopTransaction.processTransaction(player, event.getInventory(), shopOwnerUUID);
            } else if (clickedSlot == CANCEL_SLOT) { // Close button slot
                player.closeInventory();
            }
        }
    }
}