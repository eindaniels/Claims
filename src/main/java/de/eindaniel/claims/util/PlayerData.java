package de.eindaniel.claims.util;

import de.eindaniel.claims.Claims;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerData {
    private final Claims plugin;
    private final File playerDataFolder;

    // TODO Migrate from quests.yml to own players data Files.

    public PlayerData(Claims plugin) {
        this.plugin = plugin;
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public File getPlayerDataFolder() {
        return playerDataFolder;
    }

    public File getPlayerFile(UUID uuid) {
        return new File(playerDataFolder, uuid.toString() + ".yml");
    }

    private void saveConfig(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClaimBlocks(UUID uuid, int blocks) {
        File playerFile = getPlayerFile(uuid);
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set("claimblocks", blocks);
        saveConfig(config, playerFile);
    }

    public int getClaimBlocks(UUID uuid) {
        File playerFile = getPlayerFile(uuid);
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getInt("claimblocks");
    }
}
