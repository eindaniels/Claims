package de.eindaniel.claims.commands;

import de.eindaniel.claims.Claims;
import de.eindaniel.claims.claim.Claim;
import de.eindaniel.claims.claim.ClaimManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClaimInfoCommand extends Command {
    private Claims plugin;

    public ClaimInfoCommand(Claims plugin) {
        super("claiminfo");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) return false;
        Claim c = plugin.claimManager.getClaimAt(player.getLocation());
        if (c == null) {
            player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<#ff1717>Du stehst in keinem Claim!")));
            return false;
        }
        player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>Inhaber <dark_gray>→ <#fbecab>" + Bukkit.getOfflinePlayer(c.getOwner()).getName())));
        player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>UUID (Claim) <dark_gray>→ <#fbecab>" + Bukkit.getOfflinePlayer(c.getId()))));
        player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>Admin Claim <dark_gray>→ <#fbecab>" + c.isAdminClaim())));
        player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>Min <dark_gray>→ <#fbecab>" + c.getMin())));
        player.sendMessage(Claims.getPrefix().append(MiniMessage.miniMessage().deserialize("<gray>Max <dark_gray>→ <#fbecab>" + c.getMax())));
        return true;
    }
}
