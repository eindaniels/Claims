package de.eindaniel.claims.claim;


import org.bukkit.Location;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class Claim {
    private final UUID owner;
    private final Location min;
    private final Location max;
    private final boolean adminClaim;
    public final Set<UUID> trusted = new HashSet<>();


    public Claim(UUID owner, Location first, Location second, boolean adminClaim) {
        this.owner = owner;
        this.adminClaim = adminClaim;
        int minX = Math.min(first.getBlockX(), second.getBlockX());
        int minZ = Math.min(first.getBlockZ(), second.getBlockZ());
        int maxX = Math.max(first.getBlockX(), second.getBlockX());
        int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());
        this.min = new Location(first.getWorld(), minX, 0, minZ);
        this.max = new Location(first.getWorld(), maxX, 256, maxZ);
    }


    public Location getMin() { return min; }
    public Location getMax() { return max; }
    public boolean isOwner(UUID id) { return owner.equals(id); }
    public boolean isAdminClaim() { return adminClaim; }
    public void addTrusted(UUID id) { trusted.add(id); }
    public void removeTrusted(UUID id) { trusted.remove(id); }
}