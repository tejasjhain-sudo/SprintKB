package dev.sprintmc.sprintkb;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateChecker {
    
    private final SprintKBPlugin plugin;
    private final GitHubReleaseClient githubClient;
    
    private static boolean updateAvailable = false;
    private static boolean updateDownloaded = false;
    private static String latestVersionStr = null;
    
    public UpdateChecker(SprintKBPlugin plugin) {
        this.plugin = plugin;
        this.githubClient = new GitHubReleaseClient(plugin);
        
        if (plugin.getConfig().getBoolean("updater.enabled", true) && plugin.getConfig().getBoolean("updater.check-on-startup", true)) {
            checkForUpdates(true);
            startPeriodicCheck();
        }
    }
    
    public void checkForUpdates(boolean autoDownload) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String[] releaseInfo = githubClient.fetchLatestReleaseInfo();
            if (releaseInfo != null) {
                String latestTag = releaseInfo[0];
                String downloadUrl = releaseInfo[1];
                String currentVersion = plugin.getDescription().getVersion();
                
                if (VersionComparator.isNewer(currentVersion, latestTag)) {
                    updateAvailable = true;
                    latestVersionStr = latestTag;
                    plugin.getLogger().info("A new version of SprintKB is available! Current: " + currentVersion + ", Latest: " + latestTag);
                    
                    if (autoDownload && plugin.getConfig().getBoolean("updater.auto-download", true)) {
                        new UpdateDownloadTask(plugin, downloadUrl, latestTag).run(); // Run synchronously within this async task
                    }
                } else {
                    plugin.getLogger().info("SprintKB is up to date (Version " + currentVersion + ")");
                }
            }
        });
    }
    
    private void startPeriodicCheck() {
        int intervalMinutes = plugin.getConfig().getInt("updater.check-interval-minutes", 360);
        long ticks = intervalMinutes * 60 * 20L;
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!updateDownloaded) {
                    checkForUpdates(true);
                }
            }
        }.runTaskTimerAsynchronously(plugin, ticks, ticks);
    }
    
    public static boolean isUpdateAvailable() { return updateAvailable; }
    public static boolean isUpdateDownloaded() { return updateDownloaded; }
    public static String getLatestVersionStr() { return latestVersionStr; }
    public static void setUpdateDownloaded(String version) {
        updateDownloaded = true;
        latestVersionStr = version;
    }
}
