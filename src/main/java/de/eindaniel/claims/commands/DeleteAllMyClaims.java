package de.eindaniel.claims.commands;

import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeleteAllMyClaims extends Command {
    private Claims plugin;

    public DeleteAllMyClaims(Claims plugin) {
        super("deleteallmyclaims");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Nur Spieler"); return true; }
        plugin.claimManager.deleteAllPlayerClaims(p.getUniqueId());
        p.sendMessage("§aClaims wurden gelöscht.");
        return true;
    }
}
