package de.eindaniel.claims.claim;

import org.bukkit.Location;

import java.util.*;

public class Claim {
    private UUID id;
    private final UUID owner;
    private final Location min;
    private final Location max;
    private final boolean adminClaim;
    public List<UUID> trusted;

    public Claim(UUID id, UUID owner, Location first, Location second, boolean adminClaim, List<UUID> trusted) {
        this.id = id;
        this.owner = owner;
        this.adminClaim = adminClaim;
        this.min = getMinLocation(first, second);
        this.max = getMaxLocation(first, second);
        this.trusted = trusted;
    }

    public static Location getMinLocation(Location a, Location b) {
        return new Location(
                a.getWorld(),
                Math.min(a.getBlockX(), b.getBlockX()),
                Math.min(a.getBlockY(), b.getBlockY()),
                Math.min(a.getBlockZ(), b.getBlockZ())
        );
    }

    public static Location getMaxLocation(Location a, Location b) {
        return new Location(
                a.getWorld(),
                Math.max(a.getBlockX(), b.getBlockX()),
                Math.max(a.getBlockY(), b.getBlockY()),
                Math.max(a.getBlockZ(), b.getBlockZ())
        );
    }

    public Location getMin() { return min; }
    public Location getMax() { return max; }
    public boolean isOwner(UUID id) { return owner.equals(id); }
    public boolean isAdminClaim() { return adminClaim; }
    public void addTrusted(UUID id) { trusted.add(id); }
    public void removeTrusted(UUID id) { trusted.remove(id); }

    public UUID getId() {
        return id;
    }

    public List<UUID> getTrusted() {
        return trusted;
    }

    public UUID getOwner() {
        return owner;
    }
}