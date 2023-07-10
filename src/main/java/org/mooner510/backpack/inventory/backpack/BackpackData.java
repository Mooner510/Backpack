package org.mooner510.backpack.inventory.backpack;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.mooner510.backpack.Backpack;
import org.mooner510.backpack.inventory.InventoryUtils;

import java.util.HashMap;
import java.util.UUID;

import static org.mooner510.backpack.Backpack.mainPath;
import static org.mooner510.backpack.MoonerUtils.loadConfig;

public class BackpackData {
    private static final HashMap<UUID, BackpackData> backpackData = new HashMap<>();

    public static void loadBackpack(Player player) {
        backpackData.computeIfAbsent(player.getUniqueId(), BackpackData::new);
    }

    public static void openBackpack(Player viewer, UUID owner, int page) {
        BackpackData data = backpackData.get(owner);
        if (data == null) return;
        Inventory inventory = data.getInventory(page);
        viewer.openInventory(inventory);
        registerBackpackEvent(viewer, inventory, owner, page);
    }

    private static void registerBackpackEvent(Player viewer, Inventory inventory, UUID owner, int page) {
        Bukkit.getPluginManager().registerEvents(new BackpackEvent(viewer, inventory, owner, page), Backpack.getInstance());
    }

    public static int getBackpackMaxPages(UUID owner) {
        BackpackData data = backpackData.get(owner);
        if(data == null) return 0;
        return getBackpackMaxPages(data);
    }

    public static int getBackpackMaxPages(BackpackData data) {
        return getMaxPages(data.lines);
    }

    private static int getMaxPages(int lines) {
        return (int) Math.ceil(lines / 5d);
    }

    private HashMap<Integer, Inventory> inventories;

    private int lines;

    private final UUID uuid;

    private BackpackData(UUID uuid) {
        this.uuid = uuid;
        reload();
    }

    public void reload() {
        inventories = new HashMap<>();
        Bukkit.getScheduler().runTaskAsynchronously(Backpack.getInstance(), () -> {
            FileConfiguration yml = loadConfig(mainPath + "/data", uuid + ".yml");
            lines = yml.getInt("lines", 1);
            ConfigurationSection items = yml.getConfigurationSection("items");
            if (items != null) {
                int maxLines = getMaxPages(lines);
                for (int i = 0; i < maxLines; i++) {
                    inventories.put(i + 1, InventoryUtils.build(yml, i, lines));
                }
            }
        });
    }

    public Inventory getInventory(int page) {
        Inventory inv = inventories.get(page);
        if (inv == null) throw new NullPointerException("Unknown inventory page: " + page);
        return inv;
    }
}
