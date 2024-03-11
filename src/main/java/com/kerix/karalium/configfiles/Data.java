package com.kerix.karalium.configfiles;

import com.kerix.karalium.core.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.kerix.api.configapi.ConfigManager;

/**
 * Manages data related to players.
 */
public class Data extends ConfigManager {
    private final YamlConfiguration config;

    /**
     * Initializes the Data configuration manager.
     */
    public Data() {
        super(Main.getINSTANCE(), "data", "Players");
        Main main = Main.getINSTANCE();
        config = createFile(main);
    }

    /**
     * Retrieves the configuration.
     *
     * @return The YAML configuration.
     */
    public YamlConfiguration getConfig() {
        return config;
    }

    /**
     * Checks if a player is registered in the data.
     *
     * @param player The player to check.
     * @return true if the player is registered; otherwise false.
     */
    public boolean isRegistered(@NotNull Player player) {
        return config.contains("Players." + player.getUniqueId());
    }
}