package com.kerix.karalium.configfiles;

import com.kerix.karalium.core.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.kerix.api.configapi.ConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Manages configuration related to PvP abilities.
 */
public class PVPConfig extends ConfigManager {

    private final YamlConfiguration config;

    /**
     * Initializes the PVPConfig configuration manager.
     */
    public PVPConfig() {
        super(Main.getINSTANCE(), "pvpconfig", "Pvp");
        Main main = Main.getINSTANCE();
        initFile(main);
        config = createFile(main);
    }

    /**
     * Initializes the base configuration.
     */
    @Override
    public void initBaseConfig() {
        config.set(getKey() + "Abilities.ShadowStep.Cooldown", 10);
        config.set(getKey() + "Abilities.ShadowStep.Distance", 5);
        config.set(getKey() + "Abilities.ShadowStep.Blindness", 9);
        config.set(getKey() + "Abilities.ShadowStep.Radius", 5);
        config.set(getKey() + "Abilities.VoidNova.Cooldown", 30);
        config.set(getKey() + "Abilities.VoidNova.RepelStrength", 5);
        config.set(getKey() + "Abilities.VoidNova.YNerf", 0.75);
        config.set(getKey() + "Abilities.VoidNova.Slowness.Level", 5);
        config.set(getKey() + "Abilities.VoidNova.Slowness.Duration", 180);
        config.set(getKey() + "Abilities.VoidNova.Radius", 10);
        config.set(getKey() + "Abilities.VoidNova.Damage", 4);
        config.set(getKey() + "Abilities.AbyssalBarrier.Cooldown", 45);
        config.set(getKey() + "Abilities.AbyssalBarrier.Duration", 8);
        config.set(getKey() + "Abilities.EclipsingStrike.Cooldown", 50);
        config.set(getKey() + "Abilities.EclipsingStrike.Damage", 6);
        config.set(getKey() + "Abilities.EclipsingStrike.ArrowSpeed", 16);
        config.set(getKey() + "Abilities.EclipsingStrike.ExplosionPower", 4);
        config.set(getKey() + "Abilities.EclipsingStrike.Darkness", 9);
        config.set(getKey() + "Abilities.RiftSummon.Cooldown", 240);
        config.set(getKey() + "Abilities.RiftSummon.Radius", 30);
        config.set(getKey() + "Abilities.RiftSummon.YRadius", 10);
        config.set("Rift.BuffY", 4.5);
        config.set("Rift.NerfSpeed", 16);
        config.set("Rift.TPRadius", 4);
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
     * Compares two YAML configurations and lists the differences.
     *
     * @param config1 The first YAML configuration.
     * @param config2 The second YAML configuration.
     * @return A list of differences between the configurations.
     */
    public static List<String> compareFiles(YamlConfiguration config1, YamlConfiguration config2) {
        List<String> differences = new ArrayList<>();
        Set<String> commonKeys = config1.getKeys(true);
        commonKeys.retainAll(config2.getKeys(true));
        differences.add("§dChanges are:");
        for (String key : commonKeys) {
            Object value1 = config1.get(key);
            Object value2 = config2.get(key);
            assert value1 != null;
            if (!value1.equals(value2)) {
                if (value1 instanceof ConfigurationSection section1 && value2 instanceof ConfigurationSection section2) {
                    if (!section1.getKeys(true).equals(section2.getKeys(true))) {
                        differences.add("§c- " + key + ": " + value1 + "§a + " + key + ": " + value2);
                    }
                } else {
                    differences.add("§c- " + key + ": " + value1 + "§a + " + key + ": " + value2);
                }
            }
        }
        if (differences.size() <= 1) differences.set(0, "§cNo changes have been made.");
        return differences;
    }
}
