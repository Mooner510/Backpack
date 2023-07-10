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

    public static FileConfiguration loadConfig(String Path, String File) {
        new File(Path).mkdirs();
        File f = new File(Path, File);
        if(!f.exists()) {
            try {
                f.createNewFile();
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
