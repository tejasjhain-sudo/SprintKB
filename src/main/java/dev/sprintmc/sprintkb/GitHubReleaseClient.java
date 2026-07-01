package dev.sprintmc.sprintkb;

import org.bukkit.Bukkit;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GitHubReleaseClient {
    
    private final SprintKBPlugin plugin;
    
    public GitHubReleaseClient(SprintKBPlugin plugin) {
        this.plugin = plugin;
    }
    
    public String[] fetchLatestReleaseInfo() {
        String owner = plugin.getConfig().getString("updater.owner", "");
        String repo = plugin.getConfig().getString("updater.repo", "");
        String token = plugin.getConfig().getString("updater.github-token", "");
        
        if (owner.isEmpty() || repo.isEmpty() || owner.equals("YOUR_GITHUB_USERNAME")) return null;
        
        try {
            URL url = new URL("https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setRequestProperty("User-Agent", "SprintKB-Updater");
            
            if (token != null && !token.isEmpty()) {
                conn.setRequestProperty("Authorization", "token " + token);
            }
            
            if (conn.getResponseCode() != 200) {
                plugin.getLogger().warning("Failed to check for updates. GitHub API responded with: " + conn.getResponseCode());
                return null;
            }
            
            Scanner scanner = new Scanner(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            
            String json = response.toString();
            
            // Extremely basic JSON parsing for version and asset URL (do not use in prod without a real parser like GSON)
            String tagName = extractJsonField(json, "tag_name");
            String downloadUrl = extractJsonAssetUrl(json, plugin.getConfig().getString("updater.asset-name-contains", "SprintKB"));
            
            if (tagName != null && downloadUrl != null) {
                return new String[]{tagName, downloadUrl};
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking GitHub for updates: " + e.getMessage());
        }
        
        return null;
    }
    
    private String extractJsonField(String json, String field) {
        String search = "\"" + field + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
    
    private String extractJsonAssetUrl(String json, String assetNameContains) {
        // Find the browser_download_url containing the asset name
        int start = 0;
        while ((start = json.indexOf("\"browser_download_url\":\"", start)) != -1) {
            start += "\"browser_download_url\":\"".length();
            int end = json.indexOf("\"", start);
            String url = json.substring(start, end);
            if (url.contains(assetNameContains) && url.endsWith(".jar")) {
                return url;
            }
        }
        return null;
    }
}
