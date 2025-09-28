package de.eindaniel.claims.listeners;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;

import java.nio.charset.MalformedInputException;


public class BlockListener implements Listener {
    private final Claims plugin;
    public BlockListener(Claims plugin) { this.plugin = plugin; }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Claim c = plugin.claimManager.getClaimAt(e.getBlock().getLocation());
        if (c == null) return;
        if (c.isAdminClaim() && !p.hasPermission("claims.admin")) {
            e.setCancelled(true);
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier nicht abbauen!")));
            return;
        }
        if (!c.isOwner(p.getUniqueId()) && !c.trusted.contains(p.getUniqueId()) && !p.hasPermission("claims.admin")) {
            e.setCancelled(true);
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht!")));
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Claim c = plugin.claimManager.getClaimAt(e.getBlock().getLocation());
        if (c == null) return;
        if (c.isAdminClaim() && !p.hasPermission("claims.admin")) {
            e.setCancelled(true);
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier nicht bauen!")));
            return;
        }
        if (!c.isOwner(p.getUniqueId()) && !c.trusted.contains(p.getUniqueId()) && !p.hasPermission("claims.admin")) {
            e.setCancelled(true);
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht!")));
        }
    }
}