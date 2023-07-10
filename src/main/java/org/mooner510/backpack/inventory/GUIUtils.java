package org.mooner510.backpack.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static org.mooner510.backpack.MoonerUtils.chat;

public final class GUIUtils {
    public static ItemStack createItem(Material m, int amount, String name, String... lore) {
        ItemStack i = new ItemStack(m, amount);
        ItemMeta im = i.getItemMeta();
        if (im != null) {
            im.setDisplayName(chat("&f" + name));
            im.setLore(Arrays.stream(lore).map(s -> chat("&7" + s)).toList());
            i.setItemMeta(im);
        }
        return i;
    }
}
