package org.mooner510.backpack.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class InventoryUtils {
    public static final ItemStack glass = GUIUtils.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " ");
    public static final ItemStack close = GUIUtils.createItem(Material.BARRIER, 1, "&c닫기");
    public static final ItemStack next = GUIUtils.createItem(Material.ARROW, 1, "&aNext Page ➡");
    public static final ItemStack last =  GUIUtils.createItem(Material.ARROW, 1, "&eLast Page »");
    public static final ItemStack previous = GUIUtils.createItem(Material.ARROW, 1, "&a⬅ Previous Page");
    public static final ItemStack first = GUIUtils.createItem(Material.ARROW, 1, "&e« First Page");

    public static Inventory build(FileConfiguration yml, int page, int lines) {
        ConfigurationSection section = yml.getConfigurationSection("items." + page);
        if (section == null) throw new NullPointerException("Page is null");

        Set<String> keys = section.getKeys(false);

        int maxPage = (int) Math.ceil(lines / 5d);
        Inventory inventory = Bukkit.createInventory(null, 9 + 9 * getEnderChestPageLine(page, lines), "Backpack (" + page + "/" + maxPage + ")");

        for (int i = 0; i < 9; i++) inventory.setItem(i, glass);
        inventory.setItem(0, close);

        if (page < maxPage) {
            inventory.setItem(7, next);
            inventory.setItem(8, last);
        }

        if (page > 1) {
            inventory.setItem(6, previous);
            inventory.setItem(5, first);
        }

        for (String key : keys) inventory.setItem(Integer.parseInt(key), section.getItemStack(key));
        return inventory;
    }

    public static int getEnderChestPageLine(int page, int lines) {
        if (page == 1) {
            return Math.min(lines, 5);
        } else {
            return Math.min(lines - ((page - 1) * 5), 5);
        }
    }

    public static boolean isClicked(InventoryClickEvent event, Inventory inventory, Player viewer) {
        ItemStack item = event.getCurrentItem();
        return isOwned(event, inventory, viewer) && event.getClickedInventory() != null && item != null && item.getType() != Material.AIR;
    }

    public static boolean isOwned(InventoryEvent event, Inventory inventory, Player viewer) {
        return viewer.getUniqueId().equals(event.getView().getPlayer().getUniqueId()) && !event.getInventory().equals(inventory);
    }
}
