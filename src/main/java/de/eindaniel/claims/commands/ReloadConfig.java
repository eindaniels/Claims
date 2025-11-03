package de.eindaniel.claims.commands;

import de.eindaniel.claims.Claims;
import de.eindaniel.claims.util.PlayerData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadConfig extends Command {
    private Claims plugin;

    public ReloadConfig(Claims plugin) {
        super("reloadclaims");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return false;
        if (!player.hasPermission("claims.admin")) return false;
        plugin.claimConfig().loadConfig();
        plugin.claimManager.loadAllClaims();
        player.sendMessage(MiniMessage.miniMessage().deserialize("<#1fff17>Hoffentlich wurde die Claim Config reloaded."));
        return false;
    }
}
