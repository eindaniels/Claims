package de.eindaniel.claims.listeners;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ClaimCreationListener implements Listener {

    private final Claims plugin;
    private final Map<UUID, Location> firstCorner = new HashMap<>();

    public ClaimCreationListener(Claims plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return; // nur Mainhand
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (item == null || item.getType() != Material.GOLDEN_SHOVEL) return;
        if (e.getClickedBlock() == null) return;

        Block clicked = e.getClickedBlock();
        Location loc = clicked.getLocation();

        if (!firstCorner.containsKey(p.getUniqueId())) {
            // Erste Ecke setzen
            firstCorner.put(p.getUniqueId(), loc);
            Component message = MiniMessage.miniMessage().deserialize("<#1fff17>Erste Ecke gesetzt <dark_gray>› <#ff3c26>" + loc.getBlockX() + "<gray>, <#40ff26>" + loc.getBlockY() + "<gray>, <#2667ff>" + loc.getBlockZ());
            p.sendMessage(Claims.getPrefix().append(message));
            e.setCancelled(true);
        } else {
            // Zweite Ecke setzen
            Location first = firstCorner.remove(p.getUniqueId());
            Location second = loc;

            int minX = Math.min(first.getBlockX(), second.getBlockX());
            int minY = Math.min(first.getBlockY(), second.getBlockY());
            int minZ = Math.min(first.getBlockZ(), second.getBlockZ());

            int maxX = Math.max(first.getBlockX(), second.getBlockX());
            int maxY = Math.max(first.getBlockY(), second.getBlockY());
            int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());

            Location min = new Location(first.getWorld(), minX, minY, minZ);
            Location max = new Location(first.getWorld(), maxX, maxY, maxZ);

            int area = (maxX - minX + 1) * (maxZ - minZ + 1);
            int available = plugin.claimManager.getClaimBlocks(p.getUniqueId());

            if (available < area) {
                Component message = MiniMessage.miniMessage().deserialize("<#ff1717>Nicht genug Claimblöcke! <#fbecab>" + area + " Blöcke <gray>werden benötigt, du hast <#fbecab>" + available + " Blöcke<gray>.");
                p.sendMessage(Claims.getPrefix().append(message));
                e.setCancelled(true);
                return;
            }

            // Claim erstellen (adminClaim = false)
            Claim claim = new Claim(p.getUniqueId(), min, max, false);
            plugin.claimManager.addClaim(claim);
            plugin.claimManager.removeClaimBlocks(p.getUniqueId(), area);

            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#1fff17>Dein Claim wurde erfolgreich erstellt.")));
            showParticles(p, claim);
            e.setCancelled(true);
        }
    }

    private void showParticles(Player p, Claim claim) {
        World w = p.getWorld();
        int minX = claim.getMin().getBlockX();
        int minZ = claim.getMin().getBlockZ();
        int maxX = claim.getMax().getBlockX();
        int maxZ = claim.getMax().getBlockZ();
        int y = claim.getMin().getBlockY() + 1;

        for (int x = minX; x <= maxX; x++) {
            spawnParticle(w, new Location(w, x + 0.5, y, minZ + 0.5));
            spawnParticle(w, new Location(w, x + 0.5, y, maxZ + 0.5));
        }
        for (int z = minZ; z <= maxZ; z++) {
            spawnParticle(w, new Location(w, minX + 0.5, y, z + 0.5));
            spawnParticle(w, new Location(w, maxX + 0.5, y, z + 0.5));
        }
    }

    private void spawnParticle(World w, Location loc) {
        w.spawnParticle(Particle.HAPPY_VILLAGER, loc, 1, 0, 0, 0, 0);
    }
}
