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

    // Speichere trusted immer als Liste von UUID-Strings
    public void addTrusted(Claim claim, UUID uuid) {
        if (claim == null || uuid == null) return;
        // claim bereits angepasst (claim.addTrusted) — sichere Speicherung:
        List<UUID> trusted = claim.getTrusted();
        List<String> trustedStrings = (trusted == null) ? Collections.emptyList() : trusted.stream().map(UUID::toString).toList();
        config.set("claims." + claim.getId() + ".trusted", trustedStrings);
        saveConfig();
    }

    public void removeTrusted(Claim claim, UUID uuid) {
        if (claim == null) return;
        List<UUID> trusted = claim.getTrusted();
        List<String> trustedStrings = (trusted == null) ? Collections.emptyList() : trusted.stream().map(UUID::toString).toList();
        config.set("claims." + claim.getId() + ".trusted", trustedStrings);
        saveConfig();
    }

    public List<UUID> getTrusted(Claim claim) {
        List<UUID> trusted = new ArrayList<>();
        if (claim == null) return trusted;
        for (String stuuid : config.getStringList("claims." + claim.getId() + ".trusted")) {
            if (stuuid == null || stuuid.isBlank()) continue;
            try {
                trusted.add(UUID.fromString(stuuid));
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("Invalid trusted UUID in claim " + claim.getId() + ": " + stuuid);
            }
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

        config.set("claims." + id + ".owner", owner == null ? null : owner.toString());
        config.set("claims." + id + ".min", min);
        config.set("claims." + id + ".max", max);
        config.set("claims." + id + ".adminClaim", adminClaim);
        config.set("claims." + id + ".trusted", (trusted == null) ? Collections.emptyList() : trusted.stream().map(UUID::toString).toList());

        saveConfig();
    }

    public void removeClaim(Claim claim) {
        UUID id = claim.getId();
        // Entferne den claims.<id>-Knoten
        config.set("claims." + id.toString(), null);
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