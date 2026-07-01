package dev.sprintmc.sprintkb;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final SprintKBPlugin plugin;
    private final ProfileManager profileManager;
    private final DebugManager debugManager;
    private final ConfigManager configManager;

    private final UpdateChecker updateChecker;

    public CommandManager(SprintKBPlugin plugin, ProfileManager profileManager, DebugManager debugManager, ConfigManager configManager, UpdateChecker updateChecker) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        this.debugManager = debugManager;
        this.configManager = configManager;
        this.updateChecker = updateChecker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sprintkb.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "SprintKB Commands:");
            sender.sendMessage(ChatColor.AQUA + "/sprintkb reload " + ChatColor.WHITE + "- Reload the config");
            sender.sendMessage(ChatColor.AQUA + "/sprintkb debug <on/off> " + ChatColor.WHITE + "- Toggle debug mode");
            sender.sendMessage(ChatColor.AQUA + "/sprintkb profile <player> " + ChatColor.WHITE + "- View a player's active profile details");
            sender.sendMessage(ChatColor.AQUA + "/sprintkb setprofile <player> <profile> " + ChatColor.WHITE + "- Set player profile");
            sender.sendMessage(ChatColor.AQUA + "/sprintkb clearprofile <player> " + ChatColor.WHITE + "- Clear player profile");
            sender.sendMessage(ChatColor.AQUA + "/sprintkb setworldprofile <world> <profile> " + ChatColor.WHITE + "- Set world profile");
            sender.sendMessage(ChatColor.AQUA + "/sprintkb update <check|download|status|disable> " + ChatColor.WHITE + "- Updater commands");
            sender.sendMessage(ChatColor.AQUA + "/sprintkb test <player> " + ChatColor.WHITE + "- Apply test KB to player");
            sender.sendMessage(ChatColor.AQUA + "/sprintkb reset <player> " + ChatColor.WHITE + "- Reset player profile to default");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "update":
                if (!sender.hasPermission("sprintkb.update")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to manage updates.");
                    break;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /sprintkb update <check|download|status|disable>");
                    break;
                }
                switch (args[1].toLowerCase()) {
                    case "check":
                        sender.sendMessage(ChatColor.YELLOW + "Checking for updates...");
                        updateChecker.checkForUpdates(false);
                        break;
                    case "download":
                        sender.sendMessage(ChatColor.YELLOW + "Checking and downloading update if available...");
                        updateChecker.checkForUpdates(true);
                        break;
                    case "status":
                        if (UpdateChecker.isUpdateDownloaded()) {
                            sender.sendMessage(ChatColor.GREEN + "SprintKB update " + UpdateChecker.getLatestVersionStr() + " is downloaded! Restart to apply.");
                        } else if (UpdateChecker.isUpdateAvailable()) {
                            sender.sendMessage(ChatColor.YELLOW + "SprintKB update " + UpdateChecker.getLatestVersionStr() + " is available! Use /sprintkb update download.");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "SprintKB is up to date.");
                        }
                        break;
                    case "disable":
                        plugin.getConfig().set("updater.enabled", false);
                        plugin.saveConfig();
                        sender.sendMessage(ChatColor.GREEN + "Updater disabled in config.yml.");
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Unknown update subcommand.");
                        break;
                }
                break;
            case "reload":
                if (!sender.hasPermission("sprintkb.reload")) break;
                configManager.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "SprintKB config reloaded!");
                break;
            case "debug":
                if (!sender.hasPermission("sprintkb.debug")) break;
                if (args.length == 2) {
                    boolean state = args[1].equalsIgnoreCase("on");
                    debugManager.setDebugEnabled(state);
                    sender.sendMessage(ChatColor.GREEN + "SprintKB debug mode set to " + state);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /sprintkb debug <on/off>");
                }
                break;
            case "profile":
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        KnockbackProfile p = profileManager.getProfile(target);
                        sender.sendMessage(ChatColor.AQUA + "Active Profile: " + ChatColor.WHITE + p.getName());
                        sender.sendMessage(ChatColor.AQUA + "Horizontal: " + ChatColor.WHITE + p.getHorizontal());
                        sender.sendMessage(ChatColor.AQUA + "Vertical: " + ChatColor.WHITE + p.getVertical());
                        sender.sendMessage(ChatColor.AQUA + "Air Multiplier: " + ChatColor.WHITE + p.getAirMultiplier());
                        boolean pc = p.getPingCompensationEnabled() != null ? p.getPingCompensationEnabled() : plugin.getConfig().getBoolean("ping-compensation.enabled", true);
                        sender.sendMessage(ChatColor.AQUA + "Ping Compensation: " + ChatColor.WHITE + (pc ? "enabled" : "disabled"));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /sprintkb profile <player>");
                }
                break;
            case "setprofile":
                if (args.length == 3) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        profileManager.setPlayerProfile(target, args[2]);
                        sender.sendMessage(ChatColor.GREEN + "Set profile of " + target.getName() + " to " + args[2]);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /sprintkb setprofile <player> <profile>");
                }
                break;
            case "clearprofile":
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        profileManager.removePlayer(target);
                        sender.sendMessage(ChatColor.GREEN + "Cleared custom profile for " + target.getName());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /sprintkb clearprofile <player>");
                }
                break;
            case "setworldprofile":
                if (args.length == 3) {
                    profileManager.setWorldProfile(args[1], args[2]);
                    sender.sendMessage(ChatColor.GREEN + "Set profile of world " + args[1] + " to " + args[2]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /sprintkb setworldprofile <world> <profile>");
                }
                break;
            case "test":
                if (!sender.hasPermission("sprintkb.test")) break;
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        target.setVelocity(new Vector(0, 0.5, 0));
                        sender.sendMessage(ChatColor.GREEN + "Applied test KB to " + target.getName());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /sprintkb test <player>");
                }
                break;
            case "reset":
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        profileManager.removePlayer(target);
                        sender.sendMessage(ChatColor.GREEN + "Reset profile of " + target.getName());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player not found.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /sprintkb reset <player>");
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "debug", "profile", "setprofile", "clearprofile", "setworldprofile", "test", "reset", "update");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return Arrays.asList("on", "off");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("update")) {
            return Arrays.asList("check", "download", "status", "disable");
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("setprofile") || args[0].equalsIgnoreCase("setworldprofile"))) {
            return new ArrayList<>(configManager.getProfiles().keySet());
        }
        return new ArrayList<>();
    }
}
