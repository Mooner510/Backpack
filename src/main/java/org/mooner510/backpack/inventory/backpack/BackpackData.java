package org.mooner510.backpack.inventory.backpack;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.mooner510.backpack.Backpack;
import org.mooner510.backpack.inventory.InventoryUtils;
import org.mooner510.backpack.inventory.backpackupgrade.BackpackUpgradeGUI;
import org.mooner510.backpack.inventory.backpackupgrade.BackpackUpgradeSettingGUI;

import java.io.File;
import java.io.IOException;
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
        viewer.playSound(viewer, Sound.BLOCK_CHEST_OPEN, 1, 0.75f);
        viewer.openInventory(inventory);
        registerBackpackEvent(viewer, inventory, owner, page);
    }

    public static boolean upgradeBackpack(Player player) {
        if(!BackpackUpgradeSettingGUI.canUpgrade(player)) return false;
        UUID uuid = player.getUniqueId();
        BackpackData data = backpackData.get(uuid);
        if(data == null) return false;
        Bukkit.getScheduler().runTaskAsynchronously(Backpack.getInstance(), () -> {
            FileConfiguration yml = loadConfig(mainPath + "/data", uuid + ".yml");
            yml.set("lines", data.lines + 1);
            try {
                yml.save(new File(mainPath + "/data", uuid + ".yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return true;
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
