package de.eindaniel.claims;


import de.eindaniel.claims.claim.*;
import de.eindaniel.claims.commands.*;
import de.eindaniel.claims.listeners.BlockListener;
import de.eindaniel.claims.listeners.ClaimCreationListener;
import de.eindaniel.claims.listeners.ExplosionListener;
import de.eindaniel.claims.listeners.InteractListener;
import de.eindaniel.claims.tasks.ParticleTask;
import de.eindaniel.claims.util.PlayerData;
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
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;


public class Claims extends JavaPlugin implements Listener {
    public ClaimManager claimManager;
    public Map<UUID, Selection> selections = new HashMap<>();
    public Set<UUID> adminClaimMode = new HashSet<>();
    private File claimsFile;
    private ClaimConfig claimConfig;
    private ParticleTask particleTask;
    private PlayerData playerData;

    public ClaimConfig claimConfig() { return claimConfig; }
    public PlayerData playerData() { return playerData; }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);

        playerData = new PlayerData(this);
        claimConfig = new ClaimConfig(this);

        // --- Backup der claims.yml vor Migration ---
        try {
            File dataFolder = getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();
            File claimsYml = new File(dataFolder, "claims.yml");
            if (claimsYml.exists()) {
                String backupName = "claims_backup_" + System.currentTimeMillis() + ".yml";
                File backup = new File(dataFolder, backupName);
                Files.copy(claimsYml.toPath(), backup.toPath());
                getLogger().info("Backup der claims.yml erstellt: " + backup.getName());
            }
        } catch (IOException ex) {
            getLogger().log(Level.WARNING, "Fehler beim Erstellen des claims.yml Backups: " + ex.getMessage(), ex);
        }

        // --- Migration trusted-Einträge (normalisiert Listen, entfernt Duplikate) ---
        try {
            int migrated = claimConfig.migrateTrustedEntries();
            if (migrated > 0) {
                getLogger().info("Claim trusted-Migration: " + migrated + " claims wurden normalisiert.");
            } else {
                getLogger().info("Claim trusted-Migration: keine Änderungen nötig.");
            }
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Fehler bei trusted-Migration: " + ex.getMessage(), ex);
        }

        claimManager = new ClaimManager(this);
        claimManager.loadAllClaims();

        // Commands
        CommandMap commandMap = Bukkit.getCommandMap();
        commandMap.register("claims", new GiveClaimBlocksCommand(this));
        commandMap.register("claims", new TrustCommand(this));
        commandMap.register("claims", new UntrustCommand(this));
        commandMap.register("claims", new TrustListCommand(this));
        commandMap.register("claims", new DeleteClaimCommand(this));
        commandMap.register("claims", new AdminClaimCommand(this));
        commandMap.register("claims", new ClaimInfoCommand(this));
        commandMap.register("claims", new DeleteAllMyClaims(this));
        commandMap.register("claims", new ReloadConfig(this));
        commandMap.register("claims", new ConvertTrustedCommand(this));
        commandMap.register("claims", new TransferClaimCommand(this));

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