package de.eindaniel.claims.listeners;

import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class InteractListener implements Listener {
    private final Claims plugin;
    public InteractListener(Claims plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        Material type = b.getType();

        Claim c = plugin.claimManager.getClaimAt(b.getLocation());
        if (c == null) return;

        if (type == Material.ENDER_CHEST) return;

        // Liste der interaktiven Blöcke (wie vorher)
        if (c.isAdminClaim() && !p.hasPermission("claims.admin")) {
            e.setCancelled(true);
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier nicht interagieren.")));
            return;
        }
        if (!c.isOwner(p.getUniqueId()) && !c.getTrusted().contains(p.getUniqueId()) && !p.hasPermission("claims.admin")) {
            e.setCancelled(true);
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht.")));

        }
    }
}