package org.mooner510.backpack;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner510.backpack.inventory.backpack.BackpackData;
import org.mooner510.backpack.inventory.backpackupgrade.BackpackUpgradeSettingGUI;

import java.io.File;

import static org.mooner510.backpack.MoonerUtils.chat;

public final class Backpack extends JavaPlugin implements Listener {
    public static final String mainPath = "/Backpack";

    private static Backpack backpack;

    public static Backpack getInstance() {
        return backpack;
    }

    @Override
    public void onEnable() {
        backpack = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info(chat("Backpack Plugin Enabled!"));
        new File("/Backpack/data").mkdirs();
    }

    @Override
    public void onDisable() {
        getLogger().info(chat("Backpack Plugin Enabled!"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "backpack" -> {
                if (sender instanceof Player p) {
                    BackpackData.openBackpack(p, p.getUniqueId(), 1);
                }
            }
            case "backpackupgrade" -> {
                if(sender instanceof Player p) {
                    new BackpackUpgradeSettingGUI(p);
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BackpackData.loadBackpack(event.getPlayer());
    }
}
