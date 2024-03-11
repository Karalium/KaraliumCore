package com.kerix.karalium.commands;

import com.kerix.karalium.configfiles.GenConfig;
import com.kerix.karalium.core.Main;
import com.kerix.karalium.configfiles.PVPConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.kerix.karalium.configfiles.PVPConfig.compareFiles;

/**
 * Handles administrative commands for Karalium.
 */
public class KaraliumAdmin implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        Main main = Main.getINSTANCE();
        PVPConfig pvpconfig = null;
        GenConfig genConfig = null;

        if (cmd.getName().equalsIgnoreCase("karalium")) {
            if (args.length > 1) {
                if (args[1].equals("gen")) {
                    genConfig = main.getGenConfig();
                } else {
                    pvpconfig = main.getPvPConfig();
                }
                switch (args[0].toLowerCase()) {
                    case "reload":
                        if (pvpconfig != null) {
                            YamlConfiguration config1 = pvpconfig.getConfig();
                            if (sender.hasPermission("karaliumcore.karalium.reload")) {
                                sender.sendMessage("§dConfig reloaded");
                                pvpconfig.loadConfig();
                                File file2 = pvpconfig.getFile();
                                YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file2);
                                compareFiles(config1, config2).forEach(sender::sendMessage);
                            }
                        }
                        if (genConfig != null) {
                            if (sender.hasPermission("karaliumcore.karalium.reload")) {
                                sender.sendMessage("§dConfig reloaded");
                                genConfig.reloadGenerators();
                            }
                        }
                        break;
                    case "reset":
                        if (pvpconfig != null) {
                            if (sender.hasPermission("karaliumcore.karalium.reset")) {
                                sender.sendMessage("§dConfig reset");
                                pvpconfig.initBaseConfig();
                                pvpconfig.saveConfig();
                            }
                        }
                        if (genConfig != null) {
                            if (sender.hasPermission("karaliumcore.karalium.reset")) {
                                sender.sendMessage("§dConfig reset");
                                genConfig.initBaseConfig();
                                genConfig.saveConfig();
                            }
                        }
                        break;
                    default:
                        sendHelpMessage(sender);
                        break;
                }
            } else sendHelpMessage(sender);
        }
        return false;
    }

    /**
     * Sends help message for Karalium administrative commands.
     */
    private void sendHelpMessage(@NotNull CommandSender sender) {
        sender.sendMessage("§5Karalium Help:");
        sender.sendMessage("§5         reload: §aReloads the server config ");
        sender.sendMessage("§5         help: §aShows this message");
        sender.sendMessage("§5         reset: §aResets the config");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            completions.add("reload");
            completions.add("reset");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("reset")) {
                completions.add("pvpconfig");
                completions.add("genconfig");
            }
        }
        return completions;
    }
}
