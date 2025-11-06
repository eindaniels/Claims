package de.eindaniel.claims.claim;

import de.eindaniel.claims.Claims;
import org.bukkit.Location;

import java.util.*;

public class ClaimManager {
    private final Claims plugin;
    private final List<Claim> claims = new ArrayList<>();
    private final Map<UUID, Integer> claimBlocks = new HashMap<>();

    public ClaimManager(Claims plugin) { this.plugin = plugin; }

    public void addClaim(Claim c) {
        plugin.claimConfig().saveClaim(c);
        claims.add(c);
    }
    public void removeClaim(Claim c) {
        plugin.claimConfig().removeClaim(c);
        claims.remove(c);
    }

    public void clearClaims() {
        claims.clear();
    }

    public void loadAllClaims() {
        clearClaims();
        List<Claim> loaded = plugin.claimConfig().getAllClaims();
        if (loaded == null) return;
        claims.addAll(loaded);
    }

    public Claim getClaimAt(Location loc) {
        for (Claim c : claims) {
            if (loc.getWorld() != c.getMin().getWorld()) continue;
            if (loc.getBlockX() >= c.getMin().getBlockX() && loc.getBlockX() <= c.getMax().getBlockX() &&
                    loc.getBlockZ() >= c.getMin().getBlockZ() && loc.getBlockZ() <= c.getMax().getBlockZ()) {
                return c;
            }
        }
        return null;
    }

    public boolean overlapsWithExistingClaim(Location min, Location max) {
        for (Claim c : claims) {
            if (!min.getWorld().equals(c.getMin().getWorld())) continue;
            if (rectsOverlap(min, max, c.getMin(), c.getMax())) {
                return true;
            }
        }
        return false;
    }

    public void deleteAllPlayerClaims(UUID uuid) {
        for (Claim c : new ArrayList<>(claims)) {
            if (c.isOwner(uuid)) {
                removeClaim(c);
            }
        }
    }

    private boolean rectsOverlap(Location aMin, Location aMax, Location bMin, Location bMax) {
        return aMin.getBlockX() <= bMax.getBlockX() && aMax.getBlockX() >= bMin.getBlockX() &&
                aMin.getBlockZ() <= bMax.getBlockZ() && aMax.getBlockZ() >= bMin.getBlockZ();
    }

    public int getClaimBlocks(UUID id) {
        return plugin.playerData().getClaimBlocks(id);
    }
    public void addClaimBlocks(UUID id, int amt) {
        int current = getClaimBlocks(id);
        plugin.playerData().setClaimBlocks(id, current + amt);
    }
    public void removeClaimBlocks(UUID id, int amt) {
        int current = getClaimBlocks(id);
        plugin.playerData().setClaimBlocks(id, current - amt);
    }
}