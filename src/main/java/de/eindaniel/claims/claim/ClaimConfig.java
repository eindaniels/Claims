package de.eindaniel.claims.claim;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class ClaimConfig {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    public ClaimConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "claims.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("claims.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTrusted(Claim claim, UUID uuid) {
        List<UUID> trusted = claim.getTrusted();
        trusted.add(uuid);
        config.set("claims." + claim.getId() + ".trusted", trusted);
        saveConfig();
    }

    public void removeTrusted(Claim claim, UUID uuid) {
        List<UUID> trusted = claim.getTrusted();
        trusted.remove(uuid);
        config.set("claims." + claim.getId() + ".trusted", trusted);
        saveConfig();
    }


    public void saveClaim(Claim claim) {
        UUID id = claim.getId();
        UUID owner = claim.getOwner();
        Location min = claim.getMin();
        Location max = claim.getMax();
        boolean adminClaim = claim.isAdminClaim();
        List<UUID> trusted = claim.getTrusted();

        config.set("claims." + id + ".owner", owner);
        config.set("claims." + id + ".min", min);
        config.set("claims." + id + ".max", max);
        config.set("claims." + id + ".adminClaim", adminClaim);
        config.set("claims." + id + ".trusted", trusted);

        saveConfig();
    }

    public void removeClaim(Claim claim) {
        UUID id = claim.getId();
        config.set("claims." + "id", null);
        saveConfig();
    }

    public List<Claim> getAllClaims() {
        List<Claim> claimList = new ArrayList<>();
        for (String id : config.getConfigurationSection("claims").getKeys(false)) {
            UUID owner = (UUID) config.get("claims." + id + ".owner");
            Location min = config.getLocation("claims." + id + ".min");
            Location max = config.getLocation("claims." + id + ".max");
            boolean adminClaim = config.getBoolean("claims." + id + ".adminClaim");
            List<UUID> trusted = config
                    .getStringList("claims." + id + ".trusted")
                    .stream()
                    .map(s -> {
                        try {
                            return UUID.fromString(s);
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            Claim claim = new Claim(UUID.fromString(id), owner, min, max, adminClaim, trusted);
            claimList.add(claim);
        }
        return claimList;
    }
}
