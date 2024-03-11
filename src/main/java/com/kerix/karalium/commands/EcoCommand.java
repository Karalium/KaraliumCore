package com.kerix.karalium.commands;

import com.kerix.karalium.core.Main;
import com.kerix.karalium.economy.EconomyImplementer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * Handles economy-related commands and tab completion.
 */
public class EcoCommand implements CommandExecutor, TabCompleter {
    private final EconomyImplementer eco;

    /**
     * Initializes the EcoCommand with the economy implementer instance.
     */
    public EcoCommand() {
        this.eco = Main.getINSTANCE().implementer;
    }

    /**
     * Executes the economy command.
     *
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("eco")) {
            if (args.length >= 1) {
                Player target = null;
                if (args.length >= 2) {
                    String playerName = args[1];
                    target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        sender.sendMessage("§cPlayer not found or has never played before.");
                        return false;
                    }
                } else {
                    if (sender instanceof Player) {
                        target = (Player) sender;
                    }
                }
                double money;
                String command = args.length >= 2 ? args[0] : "bal";
                switch (command) {
                    case "give":
                        if (args.length < 3) {
                            return ecoHelp(sender);
                        }
                        money = parseDouble(args[2]);
                        eco.depositPlayer(target.getName(), money);
                        sender.sendMessage("§aSuccessfully gave " + money + eco.getName() + " to " + target.getName());
                        break;
                    case "bal":
                        if (target != null) {
                            sender.sendMessage("§aPlayer money is: " + eco.getBalance(target.getName()) + eco.getName() + " " + eco.getName());
                        } else {
                            sender.sendMessage("§cYou must specify a player.");
                            return false;
                        }
                        break;
                    case "take":
                        if (args.length < 3) {
                            return ecoHelp(sender);
                        }
                        money = parseDouble(args[2]);
                        eco.withdrawPlayer(target.getName(), money);
                        sender.sendMessage("§aSuccessfully removed " + money + eco.getName() + " from " + target.getName());
                        break;
                    default:
                        return ecoHelp(sender);
                }
            } else {
                ecoHelp(sender);
            }
        }
        if (cmd.getName().equalsIgnoreCase("bal")) {
            Player target;
            if (args.length == 0) {
                if (sender instanceof Player) {
                    target = (Player) sender;
                    sender.sendMessage("§aPlayer money is: " + eco.getBalance(target.getName()) + eco.getName());
                } else {
                    sender.sendMessage("§cYou must specify a player.");
                }
            } else {
                target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    sender.sendMessage("§aPlayer money is: " + eco.getBalance(target.getName()) + eco.getName());
                }
            }
        }
        return false;
    }

    /**
     * Displays economy-related command help.
     */
    private boolean ecoHelp(@NotNull CommandSender sender) {
        sender.sendMessage("§5Economy help:");
        sender.sendMessage("§5         /eco give <player> <amount>: §aGives money to a player.");
        sender.sendMessage("§5         /eco take <player> <amount>: §aRemoves money from a player.");
        sender.sendMessage("§5         /eco bal <player>: §aDisplays a player's balance.");
        return false;
    }

    /**
     * Provides tab completion for economy commands.
     *
     * @return A list of tab completions.
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("eco")) {
            if (args.length == 1) {
                completions.add("give");
                completions.add("bal");
                completions.add("take");
            } else if (args.length == 2) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
        }
        if (command.getName().equalsIgnoreCase("bal")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}
