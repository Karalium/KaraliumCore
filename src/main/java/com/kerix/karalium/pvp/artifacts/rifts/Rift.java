package com.kerix.karalium.pvp.artifacts.rifts;

import com.kerix.karalium.core.Main;
import com.kerix.karalium.configfiles.PVPConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static com.kerix.karalium.core.Main.getINSTANCE;

public class Rift {

    private final Location center;
    private final double radius;
    private final double strength;
    private static final List<Rift> rifts = new ArrayList<>();

    public Rift(Location center, double radius, double strength) {
        this.center = center;
        this.radius = radius;
        this.strength = strength;
    }

    public void applyGravitationalPull() {
        center.getWorld().spawnParticle(Particle.SCULK_SOUL, center, 20, 0.1, 0.1, 0.1, 0.05);
        Collection<Player> nearbyPlayers = center.getNearbyPlayers(radius);
        PVPConfig core = Main.getINSTANCE().getPvPConfig();
        YamlConfiguration config = core.getConfig();
        if (!nearbyPlayers.isEmpty()) {
            for (Player player : nearbyPlayers) {
                Location loc = player.getLocation();
                Vector playerLocation = loc.toVector();
                Vector riftLocation = center.toVector();
                Vector direction = riftLocation.clone().subtract(playerLocation).normalize();
                direction.setY(direction.getY()*config.getInt("Rift.BuffY"));
                if (isValidVector(direction)) {
                    Vector force = direction.multiply(strength/config.getInt("Rift.NerfSpeed"));
                    player.setVelocity(player.getVelocity().add(force));
                    if (player.getLocation().distance(center) <= config.getInt("Rift.TPRadius")) {
                        player.sendMessage("Teleporting to dimension");
                        Location playerloc = player.getLocation();
                        playerloc.setWorld(Bukkit.getWorld("world_the_end"));
                        player.teleport(playerloc);
                    }
                }
            }
        }
    }

    private boolean isValidVector(Vector vector) {
        return Double.isFinite(vector.getX()) && Double.isFinite(vector.getY()) && Double.isFinite(vector.getZ());
    }

    public void createRift() {
        rifts.add(this);
        applyGravitationalPull(this);
        new BukkitRunnable(){
            @Override
            public void run() {
                remove();
            }
        }.runTaskLater(getINSTANCE() , 2400);
    }

    public static List<Rift> getRifts() {
        return rifts;
    }

    public void remove() {
        rifts.remove(this);
    }

    private void applyGravitationalPull(Rift rift) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!getRifts().contains(rift)) cancel();
                rift.applyGravitationalPull();
            }
        }.runTaskTimer(getINSTANCE(), 10, 0);
    }
}