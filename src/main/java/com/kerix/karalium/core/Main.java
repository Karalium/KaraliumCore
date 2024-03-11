package com.kerix.karalium.core;

import com.kerix.karalium.commands.ByPassCooldown;
import com.kerix.karalium.commands.EcoCommand;
import com.kerix.karalium.commands.KaraliumAdmin;
import com.kerix.karalium.configfiles.Data;
import com.kerix.karalium.configfiles.GenConfig;
import com.kerix.karalium.configfiles.PVPConfig;
import com.kerix.karalium.economy.EconomyImplementer;
import com.kerix.karalium.economy.VaultHook;
import com.kerix.karalium.gens.GenCore;
import com.kerix.karalium.pvp.abilities.AbilityCore;
import com.kerix.karalium.pvp.artifacts.ArtifactCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kerix.api.MinecraftAPI;
import org.kerix.api.console.startapi.DebugMessageAPI;
import org.kerix.api.console.startapi.StatutMessageList;

import java.util.HashMap;
import java.util.UUID;

/**
 * Main class of the Karalium plugin.
 */
public final class Main extends JavaPlugin {

    // Startup
    private static Main INSTANCE;
    private final MinecraftAPI minecraftAPI = (MinecraftAPI) Bukkit.getPluginManager().getPlugin("MinecraftAPI");

    // Config
    private PVPConfig pvpConfig;
    private GenConfig genConfig;
    private DebugMessageAPI debugMessage;
    private Data dataconfig;

    // Economy
    private VaultHook vaultHook;
    public EconomyImplementer implementer;
    public HashMap<UUID, Double> playerBank = new HashMap<>();

    /**
     * Plugin Enable method.
     */
    @Override
    public void onEnable() {
        initializeClasses();
        initializeCommandAndListeners();

        for (Location loc : genConfig.getAllGeneratorLocations()) {
            GenCore.applyGenerator(loc);
        }

        if (initializeKaraAPI()) {
            debugMessage.StatutPlugin(StatutMessageList.ENABLE, getDescription(), getLogger());
        }

        if (playerBank.isEmpty()) {
            getLogger().config("Hooking to VaultAPI");
            vaultHook.hook();
            getLogger().config("Synchronizing Economy with data.");
            loadEconomy();
        }
    }

    /**
     * Plugin disable method.
     */
    @Override
    public void onDisable() {
        if (initializeKaraAPI()) {
            debugMessage.StatutPlugin(StatutMessageList.DISABLE, getDescription(), getLogger());
        }
        saveDataBank();
        vaultHook.unhook();
    }

    /**
     * Saves player bank data to configuration.
     */
    public void saveDataBank() {
        if (playerBank.isEmpty()) return;
        playerBank.forEach((uuid, balance) -> {
            String path = "Players." + uuid;
            dataconfig.getConfig().set(path + "Balance", balance);
        });
    }

    /**
     * Loads player bank data from configuration.
     */
    public void loadEconomy() {
        YamlConfiguration config = dataconfig.getConfig();
        ConfigurationSection section = config.getConfigurationSection("Players");
        if (section == null) return;
        section.getKeys(false).forEach((uuid) -> {
            UUID id = UUID.fromString(uuid);
            double balance = section.getDouble(uuid + "balance");
            playerBank.put(id, balance);
        });
    }

    /**
     * Tests if an item is an armor piece.
     *
     * @param item Item to verify.
     * @return True if the item is an armor piece, false otherwise.
     */
    public static boolean isArmor(ItemStack item) {
        Material type = item.getType();
        return type == Material.LEATHER_HELMET || type == Material.LEATHER_CHESTPLATE ||
                type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_BOOTS ||
                type == Material.IRON_HELMET || type == Material.IRON_CHESTPLATE ||
                type == Material.IRON_LEGGINGS || type == Material.IRON_BOOTS ||
                type == Material.GOLDEN_HELMET || type == Material.GOLDEN_CHESTPLATE ||
                type == Material.GOLDEN_LEGGINGS || type == Material.GOLDEN_BOOTS ||
                type == Material.DIAMOND_HELMET || type == Material.DIAMOND_CHESTPLATE ||
                type == Material.DIAMOND_LEGGINGS || type == Material.DIAMOND_BOOTS;
    }

    /**
     * Initializes the plugin classes.
     */
    private void initializeClasses() {
        INSTANCE = this;
        pvpConfig = new PVPConfig();
        genConfig = new GenConfig();
        dataconfig = new Data();
        debugMessage = new DebugMessageAPI();
        implementer = new EconomyImplementer();
        vaultHook = new VaultHook();
    }

    /**
     * Initializes commands and listeners.
     */
    private void initializeCommandAndListeners() {
        GenCore genCore = new GenCore();
        AbilityCore abilityCore = new AbilityCore();
        ArtifactCore artifactCore = new ArtifactCore();

        registerEvents(genCore, abilityCore, artifactCore);

        registerCommand("karalium", new KaraliumAdmin());
        registerCommand("eco", new EcoCommand());
        registerCommand("abilitybypass", new ByPassCooldown());
    }

    /**
     * Registers event listeners.
     *
     * @param listeners List of listeners to register.
     */
    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, INSTANCE);
        }
    }

    /**
     * Registers a command.
     *
     * @param commandName Name of the command.
     * @param executor    Command executor.
     */
    public void registerCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            getLogger().warning("Failed to register command '" + commandName + "'. Command not found.");
        }
    }

    /**
     * Initializes KaraAPI.
     *
     * @return True if KaraAPI is present, false otherwise.
     */
    private boolean initializeKaraAPI() {
        if (minecraftAPI != null) {
            return true;
        } else {
            getLogger().severe("No KaraAPI");
            return false;
        }
    }

    // Getters
    public static Main getINSTANCE() {
        return INSTANCE;
    }

    public PVPConfig getPvPConfig() {
        return pvpConfig;
    }

    public GenConfig getGenConfig() {
        return genConfig;
    }

    public Data getDataConfig() {
        return dataconfig;
    }
}
