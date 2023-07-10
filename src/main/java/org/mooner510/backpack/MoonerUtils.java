package org.mooner510.backpack;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class MoonerUtils {
    public static String chat(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static FileConfiguration loadConfig(String path, String file) {
        if (new File(path).mkdirs()) {
            Backpack.getInstance().getLogger().config("The plugin directory was created successfully.");
        }
        File f = new File(path, file);
        if (!f.exists()) {
            try {
                if (f.createNewFile()) {
                    Backpack.getInstance().getLogger().config("Config in directory " + path + " named by " + file + " was created successfully.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert stream != null;
        return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }
}
