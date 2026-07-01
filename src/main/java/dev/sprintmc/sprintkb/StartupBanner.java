package dev.sprintmc.sprintkb;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class StartupBanner {

    public static void printBanner(SprintKBPlugin plugin, String updateStatus) {
        if (!plugin.getConfig().getBoolean("startup-banner.enabled", true)) return;

        String version = plugin.getDescription().getVersion();
        String platform = plugin.getServer().getName();
        String javaVersion = System.getProperty("java.version");

        String[] banner = {
            "███████╗██████╗ ██████╗ ██╗███╗   ██╗████████╗██╗  ██╗██████╗ ",
            "██╔════╝██╔══██╗██╔══██╗██║████╗  ██║╚══██╔══╝██║ ██╔╝██╔══██╗",
            "███████╗██████╔╝██████╔╝██║██╔██╗ ██║   ██║   █████╔╝ ██████╔╝",
            "╚════██║██╔═══╝ ██╔══██╗██║██║╚██╗██║   ██║   ██╔═██╗ ██╔══██╗",
            "███████║██║     ██║  ██║██║██║ ╚████║   ██║   ██║  ██╗██████╔╝",
            "╚══════╝╚═╝     ╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝   ╚═╝  ╚═╝╚═════╝ ",
            " ",
            "──────────────[ SprintKB ]──────────────",
            "Plugin Information:",
            " -> Authors: SprintMC Development / Tejas",
            " -> Version: " + version,
            " -> Support: 1.20.4 - 1.21.x",
            " -> Platform: " + platform,
            " -> Java: " + javaVersion,
            " -> Updater: " + updateStatus,
            " -> Team: SprintMC Development",
            "────────────────────────────────────────"
        };

        for (String line : banner) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + line);
        }
    }
}
