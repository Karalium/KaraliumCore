package com.kerix.karalium.gens;

import com.kerix.karalium.configfiles.GenConfig;
import com.kerix.karalium.core.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public enum Generated {
    DIRT(new ItemStack(Material.BROWN_DYE){
        {
            ItemMeta meta = getItemMeta();
            meta.displayName(Component.text("§6Dirt Essence"));
            setItemMeta(meta);
        }
    } , 1),
    STONE(new ItemStack(Material.GRAY_DYE){
        {
            ItemMeta meta = getItemMeta();
            meta.displayName(Component.text("§6Stone Essence"));
            setItemMeta(meta);
        }
    } , 2);


    private final ItemStack generated;
    private double sellValue;

    Generated(ItemStack generated, double sellValue) {
        this.generated = generated;
        this.sellValue = sellValue;
    }

    public ItemStack getItem() {
        return generated;
    }

    public double getSellValue() {
        return sellValue;
    }

    public static void reloadSellValues() {
        GenConfig genConfig = Main.getINSTANCE().getGenConfig();
        YamlConfiguration config = genConfig.getConfig();
        for (Gens gen : Gens.values()) {
            String enumName = genConfig.getKey() + gen.getName().replace(" ", "");
            ConfigurationSection section = config.getConfigurationSection(enumName);
            for (Generated generated : Generated.values()) {
                if (section != null && section.isDouble("SellValue")) {
                    generated.sellValue = section.getDouble("SellValue");
                } else {
                    generated.sellValue += 0;
                }
            }
        }
    }

}
