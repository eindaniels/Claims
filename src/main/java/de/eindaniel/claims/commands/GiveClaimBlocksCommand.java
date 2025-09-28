package de.eindaniel.claims.commands;


import de.eindaniel.claims.Claims;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class GiveClaimBlocksCommand extends Command {
    private final Claims plugin;

    public GiveClaimBlocksCommand(Claims plugin) {
        super("giveclaimblocks");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("claims.admin")) { sender.sendMessage("§cKeine Rechte"); return true; }
        if (args.length < 2) { sender.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>Richtige Verwendung <dark_gray>› <#fbecab>/giveclaimblocks <Spieler> <Amount>"))); return true; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { sender.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Spieler ist nicht Online!"))); return true; }
        int amount;
        try { amount = Integer.parseInt(args[1]); } catch (NumberFormatException e) { sender.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Ungültiger Betrag!"))); return true; }
        plugin.claimManager.addClaimBlocks(target.getUniqueId(), amount);
        sender.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#1fff17>Du hast erfolgreich " + amount + " Claim-Blöcke an " + target.getName() + "gesendet.")));
        target.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#1fff17>Du hast " + amount + " Claim-Blöcke erhalten.")));
        return true;
    }
}