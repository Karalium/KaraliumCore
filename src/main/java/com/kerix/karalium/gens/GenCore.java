package com.kerix.karalium.gens;

import com.kerix.karalium.configfiles.GenConfig;
import com.kerix.karalium.core.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles generator-related events.
 */
public class GenCore implements Listener {

    private static final Main main = Main.getINSTANCE();
    private static final GenConfig genConfig = main.getGenConfig();

    /**
     * Handles block placement of a generator.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack placedItem = e.getItemInHand();
        if (placedItem.hasItemMeta() && placedItem.getItemMeta().hasDisplayName()) {
            ItemMeta itemMeta = placedItem.getItemMeta();
            if (itemMeta != null) {
                Component displayName = itemMeta.displayName();
                if (displayName != null) {
                    Material material = placedItem.getType();
                    Gens gen = Gens.getGen(material);
                    if (gen != null) {
                        e.getPlayer().sendMessage("§aPlaced " + gen.getName());
                        Location loc = e.getBlock().getLocation();
                        genConfig.addGenerator(loc, gen);
                        applyGenerator(loc);
                    }
                }
            }
        }
    }

    /**
     * Handles the breaking of a generator.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        if (genConfig.hasGenerator(loc)) {
            Gens gen = Gens.getGen(e.getBlock().getType());
            e.getPlayer().sendMessage("§cRemoved " + gen.getName());
            genConfig.removeGenerator(loc);
            e.getPlayer().getInventory().addItem(gen.getGenerator().getItem());
            e.setDropItems(false);
        }
    }

    /**
     * Applies generator functionality at a given location.
     *
     * @param loc Location to apply generator functionality.
     */
    public static void applyGenerator(Location loc) {
        new BukkitRunnable() {
            final Location dropLocation = loc.clone().add(0, 0.5, 0);

            @Override
            public void run() {
                if (loc.getBlock().getType() != Material.AIR && !loc.getNearbyPlayers(60).isEmpty()) {
                    Gens gen = genConfig.getGeneratorAtLocation(loc);
                    if (gen != null) {
                        dropLocation.getWorld().dropItemNaturally(dropLocation, gen.getGenerated().getItem());
                    }
                }
            }
        }.runTaskTimer(main, 1, 20);
    }
}
