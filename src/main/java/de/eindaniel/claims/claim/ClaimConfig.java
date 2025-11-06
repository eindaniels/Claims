package de.eindaniel.claims.claim;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClaimConfig {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    // Regex to find UUIDs in arbitrary strings
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
    );

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

        // Migrate old/invalid trusted entries on load to avoid breaking claims
        try {
            int fixed = migrateTrustedEntries();
            if (fixed > 0) {
                plugin.getLogger().info("ClaimConfig: migrated trusted lists for " + fixed + " claims.");
            }
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Fehler bei der Migration der trusted-Einträge: " + ex.getMessage(), ex);
        }
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

    /**
     * Migration utility: converts legacy/invalid 'trusted' entries into a proper List<String> of UUIDs.
     *
     * - Handles cases where trusted was saved as:
     *   - proper List<String> (fine)
     *   - List containing UUID objects (toString() will be extracted)
     *   - duplicated UUID strings
     *   - single String like "[uuid1, uuid2]" or "uuid1, uuid2"
     *   - weird serialized java objects / markers (we extract all UUID-like substrings)
     *
     * This method modifies the loaded config in memory and saves it if changes were made.
     *
     * @return number of claims whose trusted entry was normalized/changed
     */
    public int migrateTrustedEntries() {
        ConfigurationSection claimsSection = config.getConfigurationSection("claims");
        if (claimsSection == null) return 0;

        int changed = 0;
        for (String id : claimsSection.getKeys(false)) {
            String path = "claims." + id + ".trusted";
            Object raw = config.get(path);
            if (raw == null) continue;

            // Current canonical string-list for comparison (what getStringList would return)
            List<String> currentList = config.getStringList(path);

            // We'll collect UUID strings in insertion order and deduplicate
            LinkedHashSet<String> uuids = new LinkedHashSet<>();

            if (raw instanceof List<?>) {
                for (Object o : (List<?>) raw) {
                    if (o == null) continue;
                    String token = o.toString().trim();
                    if (token.isBlank()) continue;
                    // token might be a UUID itself, or a representation containing UUIDs
                    extractAndAddUUIDs(token, uuids);
                }
            } else if (raw instanceof String) {
                String token = ((String) raw).trim();
                if (!token.isBlank()) {
                    extractAndAddUUIDs(token, uuids);
                }
            } else {
                // unknown object - fall back to toString and try to extract UUIDs
                String token = raw.toString().trim();
                if (!token.isBlank()) {
                    extractAndAddUUIDs(token, uuids);
                }
            }

            List<String> normalized = new ArrayList<>(uuids);

            // If normalized is identical to currentList, no change needed
            if (!normalized.equals(currentList)) {
                // write normalized back to config (as simple List<String>)
                config.set(path, normalized);
                changed++;
            }
        }

        if (changed > 0) {
            saveConfig();
            plugin.getLogger().info("ClaimConfig: normalized 'trusted' lists for " + changed + " claims.");
        }
        return changed;
    }

    // Helper: find all UUID substrings in the input and add them to the set
    private void extractAndAddUUIDs(String input, Set<String> out) {
        // Quick split by commas/semicolons/slashes/spaces to catch simple CSV formats (and also handle brackets/quotes)
        String cleaned = input.replaceAll("[\\[\\]\"]", " ");
        String[] parts = cleaned.split("[,;|/]");

        boolean found = false;
        for (String p : parts) {
            String t = p.trim();
            if (t.isBlank()) continue;
            // If the part is exactly a UUID, add it
            if (isValidUUID(t)) {
                out.add(t);
                found = true;
            } else {
                // Try to extract UUIDs from the part via regex
                Matcher m = UUID_PATTERN.matcher(t);
                while (m.find()) {
                    out.add(m.group());
                    found = true;
                }
            }
        }

        // If splitting didn't find anything, do a final regex sweep on the whole input
        if (!found) {
            Matcher m = UUID_PATTERN.matcher(input);
            while (m.find()) {
                out.add(m.group());
            }
        }
    }

    private boolean isValidUUID(String s) {
        try {
            UUID.fromString(s);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}