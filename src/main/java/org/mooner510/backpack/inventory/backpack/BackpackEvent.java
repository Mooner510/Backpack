package org.mooner510.backpack.inventory.backpack;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.mooner510.backpack.inventory.InventoryUtils;

import java.util.UUID;

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

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!InventoryUtils.isClicked(event, inventory, viewer)) return;

        int slot = event.getSlot();
        if (slot < 9) {
            event.setCancelled(true);
            switch (slot) {
                case 0 -> viewer.closeInventory();
                case 5 -> BackpackData.openBackpack(viewer, owner, 1);
                case 6 -> BackpackData.openBackpack(viewer, owner, page - 1);
                case 7 -> BackpackData.openBackpack(viewer, owner, page + 1);
                case 8 -> BackpackData.openBackpack(viewer, owner, BackpackData.getBackpackMaxPages(owner));
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if(!InventoryUtils.isOwned(event, inventory, viewer)) return;
        HandlerList.unregisterAll(this);
    }
}
