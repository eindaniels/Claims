package de.eindaniel.claims.claim;


import org.bukkit.Location;


public class Selection {
    public Location first;
    public Location second;


    public int area() {
        if (first == null || second == null) return 0;
        int dx = Math.abs(first.getBlockX() - second.getBlockX()) + 1;
        int dz = Math.abs(first.getBlockZ() - second.getBlockZ()) + 1;
        return dx * dz;
    }
}