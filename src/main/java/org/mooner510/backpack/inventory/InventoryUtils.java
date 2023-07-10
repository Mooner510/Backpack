package org.mooner510.backpack.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class InventoryUtils {
    private static final ItemStack glass = GUIUtils.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " ");

    public static Inventory build(FileConfiguration yml, int page, int lines) {
        ConfigurationSection section = yml.getConfigurationSection("items." + page);
        if (section == null) throw new NullPointerException("Page is null");

        Set<String> keys = section.getKeys(false);

        int maxPage = (int) Math.ceil(lines / 5d);
        Inventory inventory = Bukkit.createInventory(null, 9 + 9 * getEnderChestPageLine(page, lines), "Backpack (" + page + "/" + maxPage + ")");


        if (page < maxPage) {
            inventory.setItem(7, GUIUtils.createItem(Material.ARROW, 1, "&aNext Page ➡"));
            inventory.setItem(8, GUIUtils.createItem(Material.ARROW, 1, "&eLast Page »"));
        }

        if (page > 1) {
            inventory.setItem(6, GUIUtils.createItem(Material.ARROW, 1, "&a⬅ Previous Page"));
            inventory.setItem(5, GUIUtils.createItem(Material.ARROW, 1, "&e« First Page"));
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
        Inventory inv = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();
        return viewer.getUniqueId().equals(event.getWhoClicked().getUniqueId()) && inv != null && !inv.equals(inventory) && item != null && item.getType() != Material.AIR;
    }
}
