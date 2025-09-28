package de.eindaniel.claims.commands;


import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class TrustCommand extends Command {
    private final Claims plugin;
    public TrustCommand(Claims plugin) {
        super("trust");
        this.plugin = plugin;
    }


    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Nur Spieler"); return true; }
        Claim c = plugin.claimManager.getClaimAt(p.getLocation());
        if (c == null) { p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du stehst in keinem Claim!"))); return true; }
        if (!c.isOwner(p.getUniqueId()) && !p.hasPermission("claims.admin")) { p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du hast nicht die Berechtigung diesen Befehl auszuführen."))); return true; }
        if (args.length < 1) { p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>Richtige Verwendung <dark_gray>› <#fbecab>/trust <Spieler>"))); return true; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Spieler ist nicht Online."))); return true; }
        c.addTrusted(target.getUniqueId());
        plugin.claimManager.saveClaimsAsync();
        p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#1fff17>" + target.getName() + " wurde erfolgreich hinzugefügt.")));
        return true;
    }
}