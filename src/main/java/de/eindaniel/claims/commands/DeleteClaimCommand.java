package de.eindaniel.claims.commands;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class DeleteClaimCommand extends Command {
    private final Claims plugin;
    public DeleteClaimCommand(Claims plugin) {
        super("deleteclaim");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Nur Spieler"); return true; }
        Claim c = plugin.claimManager.getClaimAt(p.getLocation());
        if (c == null) { p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du stehst in keinem Claim!"))); return true; }
        if (!c.isOwner(p.getUniqueId()) && !p.hasPermission("claims.admin")) {
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du hast keine Berechtigung diesen Claim zu löschen.")));
            return true;
        }
        plugin.claimManager.removeClaim(c);
        plugin.claimManager.saveClaimsAsync();
        p.sendMessage("§aClaim wurde gelöscht.");
        return true;
    }
}