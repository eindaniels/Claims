package de.eindaniel.claims.commands;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class AdminClaimCommand extends Command {
    private final Claims plugin;
    public AdminClaimCommand(Claims plugin) {
        super("adminclaim");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Nur Spieler"); return true; }
        if (!p.hasPermission("claims.admin")) { p.sendMessage("§cKeine Rechte"); return true; }
        UUID id = p.getUniqueId();
        if (plugin.getAdminClaimMode().contains(id)) {
            plugin.getAdminClaimMode().remove(id);
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#1fff17>Admin-Claim Modus deaktiviert.")));
        } else {
            plugin.getAdminClaimMode().add(id);
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#1fff17>Admin-Claim Modus aktiviert.")));
        }
        return true;
    }
}