package de.eindaniel.claims.commands;

import de.eindaniel.claims.Claims;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Admin command to trigger trusted-list migration on demand.
 * Usage: /converttrusted  (requires claims.admin)
 */
public class ConvertTrustedCommand extends Command {
    private final Claims plugin;

    public ConvertTrustedCommand(Claims plugin) {
        super("converttrusted");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Nur Spieler"); return true; }
        if (!p.hasPermission("claims.admin")) { p.sendMessage("§cKeine Rechte"); return true; }

        int changed = plugin.claimConfig().migrateTrustedEntries();
        p.sendMessage(MiniMessage.miniMessage().deserialize("<#1fff17>Migration abgeschlossen. Geänderte Claims: " + changed));
        plugin.getLogger().info("converttrusted executed by " + p.getName() + " (" + p.getUniqueId() + "), changed claims: " + changed);
        return true;
    }
}