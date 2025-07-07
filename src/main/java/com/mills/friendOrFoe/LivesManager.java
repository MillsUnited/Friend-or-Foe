package com.mills.friendOrFoe;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class LivesManager {

    private final File file;
    private FileConfiguration config;

    public LivesManager(File dataFolder) {
        file = new File(dataFolder, "lives.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int loadPlayerLives(Player player) {
        String playerUUID = player.getUniqueId().toString();

        if (!config.contains("players." + playerUUID)) {
            setPlayerLives(player, 10);
            return 10;
        }

        return config.getInt("players." + playerUUID + ".lives", 10);
    }

    public void savePlayerLives(Player player) {
        String playerUUID = player.getUniqueId().toString();
        config.set("players." + playerUUID + ".lives", loadPlayerLives(player));
        saveConfig();
    }

    public void setPlayerLives(Player player, int lives) {
        String playerUUID = player.getUniqueId().toString();
        config.set("players." + playerUUID + ".lives", lives);
        saveConfig();
    }

    public int getPlayerLives(Player player) {
        String playerUUID = player.getUniqueId().toString();
        return config.getInt("players." + playerUUID + ".lives", 10);
    }

}
