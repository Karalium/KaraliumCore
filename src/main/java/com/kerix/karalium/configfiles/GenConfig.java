package com.kerix.karalium.configfiles;

import com.kerix.karalium.core.Main;
import com.kerix.karalium.gens.Generated;
import com.kerix.karalium.gens.Generator;
import com.kerix.karalium.gens.Gens;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.kerix.api.configapi.ConfigManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages configuration related to generators.
 */
public class GenConfig extends ConfigManager {

    private final YamlConfiguration config;

    /**
     * Initializes the GenConfig configuration manager.
     */
    public GenConfig() {
        super(Main.getINSTANCE(), "genconfig", "Gens");
        Main main = Main.getINSTANCE();
        config = createFile(main);
    }

    /**
     * Initializes the base configuration.
     */
    @Override
    public void initBaseConfig() {
        config.set(getKey() + ".Delay", 1);
        for (Gens gen : Gens.values()) {
            config.set(getKey() + gen.getName().replace(" ", "") + ".SellValue", gen.getGenerated().getSellValue());
            config.set(getKey() + gen.getName().replace(" ", "") + ".BuyPrice", gen.getGenerator().getBuyPrice());
        }
    }

    public void reloadGenerators(){
        saveConfig();
        Generated.reloadSellValues();
        Generator.reloadBuyPrice();
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
     * Adds a generator to the configuration.
     *
     * @param loc The location of the generator.
     * @param gen The type of generator.
     */
    public void addGenerator(@NotNull Location loc, @NotNull Gens gen) {
        String location = String.format("world.%s.x%d.z%d.y%d",
                loc.getWorld().getName(),
                (int) loc.getX(),
                (int) loc.getZ(),
                (int) loc.getY());
        setYamlConfig(getKey() + location, gen.getGenerator().getItem().getType().name());
    }

    /**
     * Removes a generator from the configuration.
     *
     * @param loc The location of the generator to remove.
     */
    public void removeGenerator(@NotNull Location loc) {
        String location = String.format("world.%s.x%d.z%d.y%d",
                loc.getWorld().getName(),
                (int) loc.getX(),
                (int) loc.getZ(),
                (int) loc.getY());
        setYamlConfig(getKey() + ".List." + location, null);
    }

    /**
     * Checks if a generator exists at the specified location.
     *
     * @param loc The location to check.
     * @return true if a generator exists at the location; otherwise false.
     */
    public boolean hasGenerator(@NotNull Location loc) {
        String location = String.format("world.%s.x%d.z%d.y%d",
                loc.getWorld().getName(),
                (int) loc.getX(),
                (int) loc.getZ(),
                (int) loc.getY());
        return getYamlSectionString(getKey() + ".List." + location) != null;
    }

    /**
     * Retrieves the generator type at the specified location.
     *
     * @param loc The location to check.
     * @return The generator type if found; otherwise null.
     */
    public Gens getGeneratorAtLocation(@NotNull Location loc) {
        String location = String.format("%s.x%d.z%d.y%d",
                loc.getWorld().getName(),
                (int) loc.getX(),
                (int) loc.getZ(),
                (int) loc.getY());
        String generatorName = getYamlSectionString(getKey() + ".List." + location);
        if (generatorName != null) {
            return Gens.getGen(Material.valueOf(generatorName));
        }
        return null;
    }

    /**
     * Retrieves all generator locations.
     *
     * @return A list of all generator locations.
     */
    public List<Location> getAllGeneratorLocations() {
        List<Location> locations = new ArrayList<>();
        getYamlConfigurationSectionList(getKey() + ".List.").stream()
                .filter(key -> key.split("\\.").length == 4)
                .forEach(key -> {
                    String[] parts = key.split("\\.");
                    World world = Bukkit.getWorld(parts[1]);
                    if (world != null) {
                        int x = Integer.parseInt(parts[2].substring(1));
                        int z = Integer.parseInt(parts[3].substring(1));
                        ConfigurationSection ySection = getYamlSection().getConfigurationSection(key);
                        if (ySection != null) {
                            ySection.getKeys(false).forEach(yKey -> {
                                int y = Integer.parseInt(yKey.substring(1));
                                locations.add(new Location(world, x, y, z));
                            });
                        }
                    }
                });
        return locations;
    }
}