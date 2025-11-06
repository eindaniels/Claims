package de.eindaniel.claims.commands;

import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TransferClaimCommand extends Command {
    private final Claims plugin;

    public TransferClaimCommand(Claims plugin) {
        super("transferclaim");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Nur Spieler");
            return true;
        }

        Claim c = plugin.claimManager.getClaimAt(p.getLocation());
        if (c == null) {
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du stehst in keinem Claim!")));
            return true;
        }

        if (!c.isOwner(p.getUniqueId()) && !p.hasPermission("claims.admin")) {
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du hast keine Berechtigung, diesen Claim zu übertragen.")));
            return true;
        }

        if (args.length < 1) {
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>Verwendung <dark_gray>› <#fbecab>/transferclaim <Spieler>")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Spieler konnte nicht gefunden werden.")));
            return true;
        }
        UUID targetId = target.getUniqueId();
        if (targetId == null) {
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Ungültige UUID für Spieler.")));
            return true;
        }

        if (targetId.equals(c.getOwner())) {
            p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Dieser Spieler ist bereits der Besitzer dieses Claims.")));
            return true;
        }

        Claim newClaim = new Claim(c.getId(), targetId, c.getMin(), c.getMax(), c.isAdminClaim(), c.getTrusted());

        plugin.claimManager.removeClaim(c);
        plugin.claimManager.addClaim(newClaim);

        String targetName = (target.getName() == null) ? targetId.toString() : target.getName();
        p.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#1fff17>Claim erfolgreich an " + targetName + " übertragen.")));

        Player online = Bukkit.getPlayer(targetId);
        if (online != null && online.isOnline()) {
            online.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#1fff17>Dir wurde ein Claim übertragen von <gray>" + p.getName())));
        }

        plugin.getLogger().info("Claim " + c.getId() + " transferred from " + p.getUniqueId() + " to " + targetId);
        return true;
    }
}