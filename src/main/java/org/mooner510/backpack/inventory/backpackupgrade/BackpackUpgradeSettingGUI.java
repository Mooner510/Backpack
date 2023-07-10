package org.mooner510.backpack.inventory.backpackupgrade;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mooner510.backpack.Backpack;
import org.mooner510.backpack.inventory.InventoryUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.mooner510.backpack.Backpack.mainPath;
import static org.mooner510.backpack.MoonerUtils.loadConfig;

public class BackpackUpgradeSettingGUI implements Listener {
    private static Set<ItemStack> items;

    public static boolean canUpgrade(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (ItemStack item : items) {
            if(!inventory.containsAtLeast(item, item.getAmount())) return false;
        }
        return true;
    }

    private final Player player;
    private final Inventory inventory;

    public BackpackUpgradeSettingGUI(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, 1, "업그레이드 재료 설정");
        Bukkit.getScheduler().runTaskAsynchronously(Backpack.getInstance(), () -> {
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
    public void onClose(InventoryCloseEvent event) {
        if (InventoryUtils.isNotOwned(event, inventory, player)) return;

        Bukkit.getScheduler().runTaskAsynchronously(Backpack.getInstance(), () -> {
            YamlConfiguration config = new YamlConfiguration();
            HashSet<ItemStack> set = new HashSet<>();
            for (int i = 0; i < 9; i++) {
                ItemStack item = inventory.getItem(i);
                config.set("upgrade", item);
                if(item != null) set.add(item.clone());
            }
            items = set;
            try {
                config.save(new File(mainPath, "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        HandlerList.unregisterAll(this);
    }
}
