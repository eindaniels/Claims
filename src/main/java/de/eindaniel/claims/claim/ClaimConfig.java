package de.eindaniel.claims.claim;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
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
        config.set("claims." + claim.getId() + ".trusted", trusted.stream().map(UUID::toString).toList());
        saveConfig();
    }

    public void removeTrusted(Claim claim, UUID uuid) {
        List<UUID> trusted = claim.getTrusted();
        trusted.remove(uuid);
        config.set("claims." + claim.getId() + ".trusted", trusted);
        saveConfig();
    }

    public List<UUID> getTrusted(Claim claim) {
        List<UUID> trusted = new ArrayList<>();
        for (String stuuid : config.getStringList("claims." + claim.getId() + ".trusted")) {
            if (stuuid == null) return null;
            trusted.add(UUID.fromString(stuuid));
        }
        return trusted;
    }


    public void saveClaim(Claim claim) {
        UUID id = claim.getId();
        UUID owner = claim.getOwner();
        Location min = claim.getMin();
        Location max = claim.getMax();
        boolean adminClaim = claim.isAdminClaim();
        List<UUID> trusted = claim.getTrusted();

        config.set("claims." + id + ".owner", owner.toString());
        config.set("claims." + id + ".min", min);
        config.set("claims." + id + ".max", max);
        config.set("claims." + id + ".adminClaim", adminClaim);
        config.set("claims." + id + ".trusted", trusted.toString());

        saveConfig();
    }

    public void removeClaim(Claim claim) {
        UUID id = claim.getId();
        config.set("claims." + "id", null);
        saveConfig();
    }

    public List<Claim> getAllClaims() {
        List<Claim> claimList = new ArrayList<>();

        ConfigurationSection claimsSection = config.getConfigurationSection("claims");
        if (claimsSection == null) {
            return claimList;
        }

        for (String id : claimsSection.getKeys(false)) {
            UUID claimId;
            try {
                claimId = UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping claim with invalid id: " + id);
                continue;
            }

            String ownerStr = config.getString("claims." + id + ".owner", null);
            UUID owner = null;
            if (ownerStr != null) {
                try {
                    owner = UUID.fromString(ownerStr);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid owner UUID for claim " + id + ": " + ownerStr);
                }
            } else {
                plugin.getLogger().warning("Missing owner for claim " + id + ", setting owner=null");
            }

            Location min = config.getLocation("claims." + id + ".min");
            Location max = config.getLocation("claims." + id + ".max");
            boolean adminClaim = config.getBoolean("claims." + id + ".adminClaim", false);

            List<UUID> trusted = config
                    .getStringList("claims." + id + ".trusted")
                    .stream()
                    .map(s -> {
                        try {
                            return UUID.fromString(s);
                        } catch (IllegalArgumentException ex) {
                            plugin.getLogger().warning("Invalid trusted UUID in claim " + id + ": " + s);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            if (min == null || max == null) {
                plugin.getLogger().warning("Skipping claim " + id + " because min or max location is missing.");
                continue;
            }

            Claim claim = new Claim(claimId, owner, min, max, adminClaim, trusted);
            claimList.add(claim);
        }

        return claimList;
    }
}
