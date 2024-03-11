package com.kerix.karalium.gens;

import com.kerix.karalium.configfiles.GenConfig;
import com.kerix.karalium.core.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Generator {
    DIRT(new ItemStack(Material.COARSE_DIRT) {
        {
            ItemMeta meta = getItemMeta();
            meta.displayName(Component.text("§6Dirt Generator"));
            setItemMeta(meta);
        }
    } , 10),
    STONE(new ItemStack(Material.ANDESITE) {
        {
            ItemMeta meta = getItemMeta();
            meta.displayName(Component.text("§7Stone Generator"));
            setItemMeta(meta);
        }
    } , 20);

    private final ItemStack generator;
    private double buyPrice;

    Generator(ItemStack generator, double buyPrice) {
        this.generator = generator;
        this.buyPrice = buyPrice;
    }

    public ItemStack getItem() {
        return generator;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public static void reloadBuyPrice() {
        GenConfig genConfig = Main.getINSTANCE().getGenConfig();
        YamlConfiguration config = genConfig.getConfig();
        for (Gens gen : Gens.values()) {
            String enumName = genConfig.getKey() + gen.getName().replace(" ", "");
            ConfigurationSection section = config.getConfigurationSection(enumName);
            for (Generator generator : Generator.values()) {
                if (section != null && section.isDouble("BuyPrice")) {
                    generator.buyPrice = section.getDouble("BuyPrice");
                } else {
                    generator.buyPrice += 0;
                }
            }
        }
    }
}
