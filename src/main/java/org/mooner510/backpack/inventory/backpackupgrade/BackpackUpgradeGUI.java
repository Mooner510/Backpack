package org.mooner510.backpack.inventory.backpackupgrade;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mooner510.backpack.Backpack;
import org.mooner510.backpack.inventory.GUIUtils;
import org.mooner510.backpack.inventory.InventoryUtils;
import org.mooner510.backpack.inventory.backpack.BackpackData;

import static org.mooner510.backpack.Backpack.mainPath;
import static org.mooner510.backpack.MoonerUtils.chat;
import static org.mooner510.backpack.MoonerUtils.loadConfig;

public class BackpackUpgradeGUI implements Listener {
    private final Player player;
    private final Inventory inventory;

    public BackpackUpgradeGUI(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, 5, "창고 업그레이드");
        Bukkit.getScheduler().runTaskAsynchronously(Backpack.getInstance(), () -> {
            final ItemStack glass1 = GUIUtils.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "다음 재료가 필요합니다");
            for (int i = 0; i < 9; i++) inventory.setItem(i, glass1);
            inventory.setItem(29, GUIUtils.createItem(Material.LIME_DYE, 1, "&a강화하기"));
            inventory.setItem(33, GUIUtils.createItem(Material.GRAY_DYE, 1, "&c취소"));
            FileConfiguration yml = loadConfig(mainPath, "config.yml");
            ConfigurationSection section = yml.getConfigurationSection("upgrade");
            if (section == null) return;
            int index = 0;
            for (String key : section.getKeys(false)) inventory.setItem(index, section.getItemStack(key));
            Bukkit.getScheduler().runTask(Backpack.getInstance(), () -> {
                player.openInventory(inventory);
                Bukkit.getPluginManager().registerEvents(this, Backpack.getInstance());
            });
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (InventoryUtils.isNotClicked(event, inventory, player)) return;

        event.setCancelled(true);
        int slot = event.getSlot();
        switch (slot) {
            case 29 -> {
                if (BackpackData.upgradeBackpack(player)) {
                    player.sendMessage(chat("&a창고가 업그레이드 되었습니다."));
                    player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 0.8f);
                } else {
                    player.sendMessage(chat("&c업그레이드에 필요한 재화가 부족합니다."));
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                }
                player.closeInventory();
            }
            case 33 -> player.closeInventory();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (InventoryUtils.isNotOwned(event, inventory, player)) return;
        HandlerList.unregisterAll(this);
    }
}
