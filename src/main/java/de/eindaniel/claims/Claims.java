package de.eindaniel.claims;


import de.eindaniel.claims.claim.*;
import de.eindaniel.claims.commands.*;
import de.eindaniel.claims.listeners.BlockListener;
import de.eindaniel.claims.listeners.ClaimCreationListener;
import de.eindaniel.claims.listeners.ExplosionListener;
import de.eindaniel.claims.listeners.InteractListener;
import de.eindaniel.claims.tasks.ParticleTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;


public class Claims extends JavaPlugin implements Listener {
    public ClaimManager claimManager;
    public Map<UUID, Selection> selections = new HashMap<>();
    public Set<UUID> adminClaimMode = new HashSet<>();
    private File claimsFile;
    private YamlConfiguration claimConfig;
    private ParticleTask particleTask;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);


        claimManager = new ClaimManager(this);
        this.claimsFile = new File(getDataFolder(), "claims.yml");
        this.claimConfig = YamlConfiguration.loadConfiguration(claimsFile);
        claimManager.loadFromConfig(claimConfig);


// Commands
        CommandMap commandMap = Bukkit.getCommandMap();
        commandMap.register("claims", new GiveClaimBlocksCommand(this));
        commandMap.register("claims", new TrustCommand(this));
        commandMap.register("claims", new UntrustCommand(this));
        commandMap.register("claims", new TrustListCommand(this));
        commandMap.register("claims", new DeleteClaimCommand(this));
        commandMap.register("claims", new AdminClaimCommand(this));

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new ClaimCreationListener(this), this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(this), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);

        particleTask = new ParticleTask(this);
        particleTask.runTaskTimer(this, 0L, 20L);


        getLogger().info("Claims Plugin aktiviert");
    }


    @Override
    public void onDisable() {
        claimManager.saveToConfig(claimConfig);
        try {
            claimConfig.save(claimsFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Fehler beim Speichern der claims.yml", e);
        }
    }

    public static Component getPrefix() {
        return MiniMessage.miniMessage().deserialize("<dark_gray>[<#ffdd00>Claims<dark_gray>] <reset>");
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        selections.remove(e.getPlayer().getUniqueId());
        adminClaimMode.remove(e.getPlayer().getUniqueId());
    }

    public Set<UUID> getAdminClaimMode() {
        return adminClaimMode;
    }
}