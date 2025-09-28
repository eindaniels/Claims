package de.eindaniel.claims.commands;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class TrustListCommand extends Command {
    private final Claims plugin;
    public TrustListCommand(Claims plugin) {
        super("trustlist");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Nur Spieler"); return true; }
        Claim c = plugin.claimManager.getClaimAt(p.getLocation());
        if (c == null) { p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#1fff17>Du stehst in keinem Claim!"))); return true; }
        p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>Diese Spieler sind hier getrusted:")));
        if (c.trusted.isEmpty()) { p.sendMessage("§7Keine"); return true; }
        for (UUID u : c.trusted) {
            OfflinePlayer off = Bukkit.getOfflinePlayer(u);
            // TODO Cytooxien Magic
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>" + (off.getName() == null ? u.toString() : off.getName()))));
        }
        return true;
    }
}