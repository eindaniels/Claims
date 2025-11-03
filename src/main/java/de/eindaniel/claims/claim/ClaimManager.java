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

    public void loadAllClaims() {
        if (plugin.claimConfig().getAllClaims() == null) return;
        for (Claim c : plugin.claimConfig().getAllClaims()) {
            for (UUID uuids : plugin.claimConfig().getTrusted(c)) {
                if (uuids == null) return;
                c.addTrusted(uuids);
            }
        }
        claims.addAll(plugin.claimConfig().getAllClaims());
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
        for (Claim c : claims) {
            if (!c.isOwner(uuid)) return;
            removeClaim(c);
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

//    public void tryCreateClaim(Player p, Selection sel, boolean adminMode) {
//        int area = sel.area();
//        int blocksAvailable = getClaimBlocks(p.getUniqueId());
//        Location min = Claim.getMinLocation(sel.first, sel.second);
//        Location max = Claim.getMaxLocation(sel.first, sel.second);
//
//        if (!adminMode && overlapsWithExistingClaim(min, max)) {
//            p.sendMessage("§cDieser Bereich überschneidet sich mit einem bestehenden Claim!");
//            return;
//        }
//        if (adminMode) {
//            Claim c = new Claim(UUID.randomUUID(), p.getUniqueId(), sel.first, sel.second, true);
//            addClaim(c);
//            p.sendMessage("§bAdmin-Claim erstellt.");
//        } else {
//            if (blocksAvailable < area) {
//                p.sendMessage("§cNicht genug Claim-Blöcke.");
//            } else {
//                Claim c = new Claim(UUID.randomUUID() ,p.getUniqueId(), sel.first, sel.second, false);
//                addClaim(c);
//                removeClaimBlocks(p.getUniqueId(), area);
//                p.sendMessage("§aClaim erstellt. Verbrauchte Blöcke: " + area);
//            }
//        }
//    }
}