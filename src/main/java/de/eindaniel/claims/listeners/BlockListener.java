package de.eindaniel.claims.listeners;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;


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
        if (c.isOwner(p.getUniqueId()) && !c.getTrusted().contains(p.getUniqueId()) && !p.hasPermission("claims.admin")) {
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
        if (c.isOwner(p.getUniqueId()) && !c.getTrusted().contains(p.getUniqueId()) && !p.hasPermission("claims.admin")) {
            e.setCancelled(true);
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht!")));
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();
        ItemStack bucket = e.getItemStack();
        if (bucket == null || bucket.getType() != Material.LAVA_BUCKET) return;

        Block target = e.getBlockClicked().getRelative(e.getBlockFace());
        Claim c = plugin.claimManager.getClaimAt(target.getLocation());
        if (c == null) return;

        if (c.isAdminClaim() && !player.hasPermission("claims.admin")) {
            e.setCancelled(true);
            player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier keine Lava platzieren!")));
            return;
        }
        if (c.isOwner(player.getUniqueId()) && !c.getTrusted().contains(player.getUniqueId()) && !player.hasPermission("claims.admin")) {
            e.setCancelled(true);
            player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht!")));
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        Block from = e.getBlock();
        Block to = e.getToBlock();
        Claim fromClaim = plugin.claimManager.getClaimAt(from.getLocation());
        Claim toClaim = plugin.claimManager.getClaimAt(to.getLocation());
        if (toClaim != null && fromClaim != toClaim) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFarmlandTrample(EntityChangeBlockEvent e) {
        if (e.getBlock().getType() != Material.FARMLAND) return;
        if (!(e.getEntity() instanceof Player player)) return;

        Claim c = plugin.claimManager.getClaimAt(e.getBlock().getLocation());
        if (c == null) return;

        if (c.isAdminClaim() && !player.hasPermission("claims.admin")) {
            e.setCancelled(true);
            player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier keine Felder zertrampeln!")));
            return;
        }
        if (c.isOwner(player.getUniqueId()) && !c.getTrusted().contains(player.getUniqueId()) && !player.hasPermission("claims.admin")) {
            e.setCancelled(true);
            player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht!")));
        }
    }

}