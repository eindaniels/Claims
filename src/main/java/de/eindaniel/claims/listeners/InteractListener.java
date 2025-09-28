package de.eindaniel.claims.listeners;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;


public class InteractListener implements Listener {
    private final Claims plugin;
    public InteractListener(Claims plugin) { this.plugin = plugin; }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        Block b = e.getClickedBlock();
        Material type = b.getType();
        Player p = e.getPlayer();
        Claim c = plugin.claimManager.getClaimAt(b.getLocation());
        if (c == null) return;


// Buttons, Levers, Pressure Plates etc. dürfen IMMER benutzt werden
        if (type.toString().contains("BUTTON") || type == Material.LEVER || type.toString().contains("PRESSURE_PLATE")) {
            return;
        }


// Interaktive Blöcke wie Kisten, Türen, Fässer, Öfen blockieren
        if (type == Material.CHEST || type == Material.BARREL || type == Material.FURNACE ||
                type == Material.BLAST_FURNACE || type == Material.SMOKER ||
                type == Material.DISPENSER || type == Material.DROPPER ||
                type == Material.HOPPER || type.toString().contains("DOOR") ||
                type.toString().contains("SHULKER_BOX") ||
                type.toString().contains("TRAPDOOR")) {


            if (c.isAdminClaim() && !p.hasPermission("claims.admin")) {
                e.setCancelled(true);
                p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier nicht interagieren.")));
                return;
            }
            if (!c.isOwner(p.getUniqueId()) && !c.trusted.contains(p.getUniqueId()) && !p.hasPermission("claims.admin")) {
                e.setCancelled(true);
                p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht.")));
            }
        }
    }
}