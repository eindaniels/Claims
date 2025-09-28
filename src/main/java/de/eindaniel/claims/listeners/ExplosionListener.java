package de.eindaniel.claims.listeners;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.block.Block;


public class ExplosionListener implements Listener {
    private final Claims plugin;
    public ExplosionListener(Claims plugin) { this.plugin = plugin; }


    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        e.blockList().removeIf(block -> {
            Claim c = plugin.claimManager.getClaimAt(block.getLocation());
            return c != null; // alle Blöcke in Claims werden geschützt
        });
    }
}