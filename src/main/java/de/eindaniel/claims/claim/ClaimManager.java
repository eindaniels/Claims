package de.eindaniel.claims.claim;

import de.eindaniel.claims.Claims;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.util.*;

public class ClaimManager {
    private final Claims plugin;
    private final List<Claim> claims = new ArrayList<>();
    private final Map<UUID, Integer> claimBlocks = new HashMap<>();

    public ClaimManager(Claims plugin) { this.plugin = plugin; }

    public void loadFromConfig(YamlConfiguration cfg) { /* TODO: laden */ }
    public void saveToConfig(YamlConfiguration cfg) { /* TODO: speichern */ }
    public void saveClaimsAsync() { /* TODO: async speichern */ }

    public void addClaim(Claim c) { claims.add(c); }
    public void removeClaim(Claim c) { claims.remove(c); }

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

    private boolean rectsOverlap(Location aMin, Location aMax, Location bMin, Location bMax) {
        return aMin.getBlockX() <= bMax.getBlockX() && aMax.getBlockX() >= bMin.getBlockX() &&
                aMin.getBlockZ() <= bMax.getBlockZ() && aMax.getBlockZ() >= bMin.getBlockZ();
    }

    public int getClaimBlocks(UUID id) { return claimBlocks.getOrDefault(id, 0); }
    public void addClaimBlocks(UUID id, int amt) { claimBlocks.put(id, getClaimBlocks(id)+amt); }
    public void removeClaimBlocks(UUID id, int amt) { claimBlocks.put(id, Math.max(0, getClaimBlocks(id)-amt)); }

    public void tryCreateClaim(Player p, Selection sel, boolean adminMode) {
        int area = sel.area();
        int blocksAvailable = getClaimBlocks(p.getUniqueId());
        Location min = Claim.getMinLocation(sel.first, sel.second);
        Location max = Claim.getMaxLocation(sel.first, sel.second);

        if (!adminMode && overlapsWithExistingClaim(min, max)) {
            p.sendMessage("§cDieser Bereich überschneidet sich mit einem bestehenden Claim!");
            return;
        }
        if (adminMode) {
            Claim c = new Claim(p.getUniqueId(), sel.first, sel.second, true);
            addClaim(c);
            p.sendMessage("§bAdmin-Claim erstellt.");
        } else {
            if (blocksAvailable < area) {
                p.sendMessage("§cNicht genug Claim-Blöcke.");
            } else {
                Claim c = new Claim(p.getUniqueId(), sel.first, sel.second, false);
                addClaim(c);
                removeClaimBlocks(p.getUniqueId(), area);
                p.sendMessage("§aClaim erstellt. Verbrauchte Blöcke: " + area);
            }
        }
    }
}