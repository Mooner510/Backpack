package org.mooner510.backpack.inventory.backpack;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.mooner510.backpack.Backpack;
import org.mooner510.backpack.inventory.InventoryUtils;
import org.mooner510.backpack.inventory.backpackupgrade.BackpackUpgradeGUI;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.mooner510.backpack.Backpack.mainPath;
import static org.mooner510.backpack.MoonerUtils.loadConfig;

public final class BackpackEvent implements Listener {
    private final Inventory inventory;
    private final Player viewer;
    private final UUID owner;
    private final int page;

    public BackpackEvent(Player viewer, Inventory inventory, UUID owner, int page) {
        this.inventory = inventory;
        this.viewer = viewer;
        this.owner = owner;
        this.page = page;
    }

    private boolean closeSound = true;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (InventoryUtils.isNotClicked(event, inventory, viewer)) return;
        if (InventoryUtils.isNotShownInventoryClicked(event, inventory)) return;

        int slot = event.getSlot();
        if (slot < 9) {
            event.setCancelled(true);
            switch (slot) {
                case 0 -> {
                    viewer.closeInventory();
                }
                case 2 -> new BackpackUpgradeGUI(viewer);
                case 5 -> {
                    closeSound = false;
                    BackpackData.openBackpack(viewer, owner, 1);
                }
                case 6 -> {
                    closeSound = false;
                    BackpackData.openBackpack(viewer, owner, page - 1);
                }
                case 7 -> {
                    closeSound = false;
                    BackpackData.openBackpack(viewer, owner, page + 1);
                }
                case 8 -> {
                    closeSound = false;
                    BackpackData.openBackpack(viewer, owner, BackpackData.getBackpackMaxPages(owner));
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (InventoryUtils.isNotOwned(event, inventory, viewer)) return;

        if (closeSound) viewer.playSound(viewer, Sound.BLOCK_CHEST_CLOSE, 1, 0.75f);
        Bukkit.getScheduler().runTaskAsynchronously(Backpack.getInstance(), () -> {
            FileConfiguration yml = loadConfig(mainPath + "/data", viewer.getUniqueId() + ".yml");
            int size = inventory.getSize();
            for (int i = 0; i < size; i++) {
                yml.set("items." + page + "." + i, inventory.getItem(i));
            }
            try {
                yml.save(new File(mainPath + "/data", viewer.getUniqueId() + ".yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        HandlerList.unregisterAll(this);
    }
}
