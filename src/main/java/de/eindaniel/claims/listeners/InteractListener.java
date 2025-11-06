package de.eindaniel.claims.listeners;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import io.papermc.paper.event.player.PlayerOpenSignEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


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
                type == Material.HOPPER || type.toString().contains("SHULKER_BOX")) {


            if (c.isAdminClaim() && !p.hasPermission("claims.admin")) {
                e.setCancelled(true);
                p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier nicht interagieren.")));
                return;
            }
            if (c.isOwner(p.getUniqueId()) && !c.getTrusted().contains(p.getUniqueId()) && !p.hasPermission("claims.admin")) {
                e.setCancelled(true);
                p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht.")));
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (event.getEntity() instanceof Player) return;
        Claim c = plugin.claimManager.getClaimAt(event.getEntity().getLocation());
        if (c == null) return;

        Player damager = (Player) event.getDamager();

        if (c.isOwner(damager.getUniqueId()) || c.getTrusted().contains(damager.getUniqueId()) || damager.hasPermission("claims.admin")) return;

        if (c.isAdminClaim() && !damager.hasPermission("claims.admin")) {
            event.setCancelled(true);
            damager.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier nicht interagieren.")));
            return;
        }

        event.setCancelled(true);
        damager.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du hast keine Rechte in diesem Claim!")));
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Claim c  = plugin.claimManager.getClaimAt(event.getPlayer().getLocation());

        if (c == null) return;

        if (event.getRightClicked().getType() == EntityType.VILLAGER) {
            if (c.isAdminClaim() && !event.getPlayer().hasPermission("claims.admin")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier nicht interagieren.")));
                return;
            }
            if (c.isOwner(event.getPlayer().getUniqueId()) && !c.getTrusted().contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("claims.admin")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht.")));
            }
        }
    }

    @EventHandler
    public void onSignOpen(PlayerOpenSignEvent event) {
        Claim c = plugin.claimManager.getClaimAt(event.getPlayer().getLocation());

        if (c == null) return;

        if (c.isAdminClaim() && !event.getPlayer().hasPermission("claims.admin")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier nicht interagieren.")));
            return;
        }
        if (c.isOwner(event.getPlayer().getUniqueId()) && !c.getTrusted().contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("claims.admin")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Claim gehört dir nicht.")));
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();
        ItemStack bucket = e.getItemStack();
        if (bucket == null ||
                (bucket.getType() != Material.LAVA_BUCKET && bucket.getType() != Material.WATER_BUCKET)) return; // Lava ODER Wasser!

        Block target = e.getBlockClicked().getRelative(e.getBlockFace());
        Claim c = plugin.claimManager.getClaimAt(target.getLocation());
        if (c == null) return;

        if (c.isAdminClaim() && !player.hasPermission("claims.admin")) {
            e.setCancelled(true);
            player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier keine Flüssigkeiten platzieren!")));
            return;
        }
        if (c.isOwner(player.getUniqueId()) && !c.getTrusted().contains(player.getUniqueId()) && !player.hasPermission("claims.admin")) {
            e.setCancelled(true);
            player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du darfst hier keine Flüssigkeiten platzieren!")));
        }
    }
}