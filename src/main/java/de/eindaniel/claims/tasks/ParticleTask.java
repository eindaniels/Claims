package de.eindaniel.claims.tasks;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class ParticleTask extends BukkitRunnable {
    private final Claims plugin;
    public ParticleTask(Claims plugin) { this.plugin = plugin; }


    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ItemStack hand = p.getInventory().getItemInMainHand();
            if (hand != null && hand.getType() == Material.GOLDEN_SHOVEL) {
                Claim c = plugin.claimManager.getClaimAt(p.getLocation());
                if (c != null) {
                    Location min = c.getMin();
                    Location max = c.getMax();
                    World w = min.getWorld();
                    if (w == null) continue;
                    int y = p.getLocation().getBlockY();
                    for (int x = min.getBlockX(); x <= max.getBlockX(); x += Math.max(1, (max.getBlockX()-min.getBlockX())/20 + 1)) {
                        p.spawnParticle(Particle.HAPPY_VILLAGER, new Location(w, x, y, min.getBlockZ()), 1);
                        p.spawnParticle(Particle.HAPPY_VILLAGER, new Location(w, x, y, max.getBlockZ()), 1);
                    }
                    for (int z = min.getBlockZ(); z <= max.getBlockZ(); z += Math.max(1, (max.getBlockZ()-min.getBlockZ())/20 + 1)) {
                        p.spawnParticle(Particle.HAPPY_VILLAGER, new Location(w, min.getBlockX(), y, z), 1);
                        p.spawnParticle(Particle.HAPPY_VILLAGER, new Location(w, max.getBlockX(), y, z), 1);
                    }
                }
            }
        }
    }
}