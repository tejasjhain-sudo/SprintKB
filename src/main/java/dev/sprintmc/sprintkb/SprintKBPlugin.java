package dev.sprintmc.sprintkb;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SprintKBPlugin extends JavaPlugin {
    
    private static SprintKBPlugin instance;
    private ConfigManager configManager;
    private ProfileManager profileManager;
    private PingTracker pingTracker;
    private ComboTracker comboTracker;
    private LagMonitor lagMonitor;
    private KnockbackEngine knockbackEngine;
    private DebugManager debugManager;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        UpdateChecker updateChecker = new UpdateChecker(this);
        
        profileManager = new ProfileManager(configManager);
        pingTracker = new PingTracker(this, configManager);
        comboTracker = new ComboTracker();
        lagMonitor = new LagMonitor(this, configManager);
        debugManager = new DebugManager();
        
        knockbackEngine = new KnockbackEngine(this, profileManager, pingTracker, comboTracker, lagMonitor, configManager, debugManager);
        
        getServer().getPluginManager().registerEvents(new KnockbackListener(this, knockbackEngine, comboTracker, configManager), this);
        getServer().getPluginManager().registerEvents(pingTracker, this);
        
        getCommand("sprintkb").setExecutor(new CommandManager(this, profileManager, debugManager, configManager, updateChecker));
        getCommand("sprintkb").setTabCompleter(new CommandManager(this, profileManager, debugManager, configManager, updateChecker));
        
        getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
                if (event.getPlayer().hasPermission("sprintkb.admin")) {
                    if (UpdateChecker.isUpdateDownloaded()) {
                        event.getPlayer().sendMessage(ChatColor.GREEN + "SprintKB update downloaded. Restart your server to apply it.");
                    } else if (UpdateChecker.isUpdateAvailable()) {
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "SprintKB update available: current " + getDescription().getVersion() + ", latest " + UpdateChecker.getLatestVersionStr());
                    }
                }
            }
        }, this);
        
        printStartupBanner();
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.AQUA + "SprintKB is shutting down. Saving data and cleaning up...");
        if (pingTracker != null) pingTracker.cleanup();
        if (comboTracker != null) comboTracker.cleanup();
        getLogger().info(ChatColor.AQUA + "SprintKB disabled successfully.");
    }

    private void printStartupBanner() {
        String updateStatus = UpdateChecker.isUpdateDownloaded() ? "Downloaded" : (UpdateChecker.isUpdateAvailable() ? "Available" : "Up to date");
        StartupBanner.printBanner(this, updateStatus);
    }

    public static SprintKBPlugin getInstance() {
        return instance;
    }
}
