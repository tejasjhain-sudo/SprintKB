package dev.sprintmc.sprintkb;

import org.bukkit.Bukkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateDownloadTask implements Runnable {
    
    private final SprintKBPlugin plugin;
    private final String downloadUrl;
    private final String latestVersion;
    
    public UpdateDownloadTask(SprintKBPlugin plugin, String downloadUrl, String latestVersion) {
        this.plugin = plugin;
        this.downloadUrl = downloadUrl;
        this.latestVersion = latestVersion;
    }
    
    @Override
    public void run() {
        try {
            plugin.getLogger().info("Starting download of SprintKB update: " + latestVersion);
            
            File updateFolder = new File(plugin.getDataFolder().getParentFile(), "update");
            if (!updateFolder.exists()) {
                updateFolder.mkdirs();
            }
            
            File tempFile = new File(updateFolder, "SprintKB.jar.download");
            if (tempFile.exists()) tempFile.delete();
            
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "SprintKB-Updater");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            
            String token = plugin.getConfig().getString("updater.github-token", "");
            if (token != null && !token.isEmpty()) {
                conn.setRequestProperty("Authorization", "token " + token);
                conn.setRequestProperty("Accept", "application/octet-stream"); // To download private release assets
            }
            
            if (conn.getResponseCode() != 200) {
                plugin.getLogger().warning("Failed to download update. Server responded with: " + conn.getResponseCode());
                return;
            }
            
            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            
            out.close();
            in.close();
            
            // Rename to final jar
            File finalFile = new File(updateFolder, "SprintKB.jar");
            if (finalFile.exists()) finalFile.delete();
            tempFile.renameTo(finalFile);
            
            plugin.getLogger().info("New update downloaded successfully! Restart required.");
            UpdateChecker.setUpdateDownloaded(latestVersion);
            
            // Notify admins
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.broadcast("SprintKB update downloaded. Restart your server to apply it.", "sprintkb.admin");
            });
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error downloading update: " + e.getMessage());
        }
    }
}
