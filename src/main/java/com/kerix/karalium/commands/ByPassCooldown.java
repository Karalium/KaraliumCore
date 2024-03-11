package com.kerix.karalium.commands;

import com.kerix.karalium.core.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles bypassing cooldowns for abilities.
 */
public class ByPassCooldown implements CommandExecutor {

    // List to store players who are bypassing cooldowns
    private static final List<UUID> CDBypass = new ArrayList<>();

    /**
     * Executes the command to toggle cooldown bypass for players.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("abilitybypass") && sender instanceof Player player) {
            if (!CDBypass.contains(player.getUniqueId())) {
                player.sendMessage("§aYou are now bypassing Ability CoolDowns");
                CDBypass.add(player.getUniqueId());
            } else {
                player.sendMessage("§cYou are no longer bypassing Ability CoolDowns");
                CDBypass.remove(player.getUniqueId());
            }
        } else {
            Main.getINSTANCE().getLogger().info("Only a player can execute this command");
        }
        return false;
    }

    /**
     * Retrieves the list of players bypassing cooldowns.
     */
    public static List<UUID> getCDBypass() {
        return CDBypass;
    }
}