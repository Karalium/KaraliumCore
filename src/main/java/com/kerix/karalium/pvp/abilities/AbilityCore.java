package com.kerix.karalium.pvp.abilities;

import com.kerix.karalium.core.Main;
import com.kerix.karalium.pvp.artifacts.Artifacts;
import com.kerix.karalium.pvp.artifacts.rifts.Rift;
import com.kerix.karalium.commands.ByPassCooldown;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.*;

import static com.kerix.karalium.pvp.abilities.Abilities.getAbility;
import static com.kerix.karalium.pvp.artifacts.ArtifactF.createArtifact;

public class AbilityCore implements Listener {

    private final Main main = Main.getINSTANCE();

    private final HashMap<UUID , HashMap<Abilities, Long>> CoolDown = new HashMap<>();

    private FileConfiguration config;

    record AbilityEffect(Abilities ability, Consumer<Player> action) {}


    private final AbilityEffect[] abilities = {
            new AbilityEffect(Abilities.ShadowStep, player -> {
                if (cooldownmanager(player, Abilities.ShadowStep, config.getInt("Abilities.ShadowStep.Cooldown"))) return;

                Location loc = player.getLocation();
                Vector dir = loc.getDirection().normalize().multiply(config.getInt("Abilities.ShadowStep.Distance"));
                Location targetLoc = isWithinBlocks(loc.clone().add(dir)) ? findSafeLocationBeforeBlocks(loc, loc.clone().add(dir)) : loc.clone().add(dir);

                player.teleport(targetLoc);

                Bukkit.getOnlinePlayers().stream()
                        .filter(other -> other.getLocation().distance(targetLoc) <= config.getInt("Abilities.ShadowStep.Radius") && !other.equals(player))
                        .forEach(other -> other.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, config.getInt("Abilities.ShadowStep.Blindness") * 20, 60)));

                player.spawnParticle(Particle.EXPLOSION_HUGE, targetLoc, 4);
            }),
            new AbilityEffect(Abilities.VoidNova, player -> {
                if(cooldownmanager(player, Abilities.VoidNova, config.getInt("Abilities.VoidNova.Cooldown")))return;
                Bukkit.getOnlinePlayers().forEach(other -> {
                    if (other != player && other.getLocation().distance(player.getLocation()) <= config.getInt("Abilities.VoidNova.Radius")) {
                        Vector direction = other.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                        direction = direction.setY(direction.getY()* config.getInt("Abilities.VoidNova.YNerf"));
                        other.setVelocity(direction.multiply(5));
                        other.damage(4, player);
                        other.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                                config.getInt("Abilities.VoidNova.Slowness.Duration") * 20,
                                config.getInt("Abilities.VoidNova.Slowness.Level")-1));
                    }
                });
            }),
            new AbilityEffect(Abilities.AbyssalBarrier, player -> {
                if(cooldownmanager(player, Abilities.AbyssalBarrier, config.getInt("Abilities.AbyssalBarrier.Cooldown")))return;
                player.setInvulnerable(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setInvulnerable(false);
                    }
                }.runTaskLater(main , config.getInt("Abilities.AbyssalBarrier.Duration") * 20L);
            }),
            new AbilityEffect(Abilities.EclipsingStrike, player -> {
                if(cooldownmanager(player, Abilities.EclipsingStrike, config.getInt("Abilities.EclipsingStrike.Cooldown"))) return;
                World world = player.getWorld();
                Location startLocation = player.getEyeLocation().clone();
                Vector direction = startLocation.getDirection().normalize();
                Projectile darkEnergy = world.spawn(startLocation, Arrow.class);
                darkEnergy.setShooter(player);
                darkEnergy.setVelocity(direction.multiply(config.getInt("Abilities.EclipsingStrike.ArrowSpeed")));
                new BukkitRunnable() {
                    private final Set<Entity> hitEntities = new HashSet<>();

                    @Override
                    public void run() {
                        if (!darkEnergy.isValid() || darkEnergy.isOnGround() || darkEnergy.isDead()) {
                            world.createExplosion(darkEnergy.getLocation(), config.getInt("Abilities.EclipsingStrike.ExplosionPower"), false);
                            darkEnergy.getWorld().playSound(darkEnergy.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                            cancel();
                            return;
                        }

                        Location currentLocation = darkEnergy.getLocation();
                        world.spawnParticle(Particle.SCULK_SOUL, currentLocation, 20, 0.1, 0.1, 0.1, 0.05);
                        currentLocation.getNearbyEntities(1, 1, 1).stream()
                                .filter(entity -> !hitEntities.contains(entity))
                                .filter(entity -> entity instanceof LivingEntity && entity != player)
                                .map(entity -> (LivingEntity) entity)
                                .peek(hitEntities::add)
                                .forEach(entity -> {
                                    entity.damage(config.getInt("Abilities.EclipsingStrike.Damage"));
                                    entity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,
                                            config.getInt("Abilities.EclipsingStrike.Darkness")*20, 5));
                                });
                    }
                }.runTaskTimer(main, 0, 1);
            }),
            new AbilityEffect(Abilities.RiftSummon, player -> {
                if(cooldownmanager(player , Abilities.RiftSummon , config.getInt("Abilities.RiftSummon.Cooldown"))) return;
                double X = Math.random() * config.getInt("Abilities.RiftSummon.Radius") * 2 - config.getInt("Abilities.RiftSummon.Radius");
                double Y = Math.random() * config.getInt("Abilities.RiftSummon.YRadius");
                double Z = Math.random() * config.getInt("Abilities.RiftSummon.Radius") * 2 - config.getInt("Abilities.RiftSummon.Radius");
                Location riftLocation = player.getLocation().add(Math.round(X), Math.round(Y), Math.round(Z));


                new Rift(riftLocation , 15 , 1).createRift();
            })

    };

    private boolean isWithinBlocks(Location location) {
        return location.getBlock().getType().isSolid();
    }

    private Location findSafeLocationBeforeBlocks(Location originLoc, Location targetLoc) {
        originLoc.add(0 , 1 , 0);
        while (targetLoc.getBlock().getType().isSolid()) {
            targetLoc.subtract(targetLoc.getDirection().normalize());
        }
        return targetLoc;
    }


    @EventHandler
    public void OnClick(PlayerInteractEvent e){
        if(!e.getAction().isRightClick()) return;
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if(meta == null || !meta.hasDisplayName() || meta.lore() == null) return;
        if(!Objects.equals(item.getItemMeta().displayName(), Component.text("§1- §0§k[§d§l*** §n§4Void Relic §5§l*** §0§k] §1✦"))) return;
        String name = String.valueOf(Objects.requireNonNull(meta.lore()).get(0));
        Abilities ability = getAbility(name);
        config = main.getPvPConfig().getConfig();
        for(AbilityEffect ability1 : abilities)
            if(ability.getName().equals(ability1.ability.getName()))
                ability1.action.accept(p);
    }
    @EventHandler
    public void OnJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        p.getInventory().clear();
        p.getInventory().setItem(0, createRelic(Abilities.ShadowStep));
        p.getInventory().setItem(1, createRelic(Abilities.VoidNova));
        p.getInventory().setItem(2, createRelic(Abilities.AbyssalBarrier));
        p.getInventory().setItem(3 , createRelic(Abilities.EclipsingStrike));
        p.getInventory().addItem(createRelic(Abilities.RiftSummon));
        p.getInventory().addItem(createArtifact(Artifacts.VoidHeartAmulet));
        p.getInventory().addItem(createArtifact(Artifacts.CursedSkull));
        p.getInventory().addItem(createArtifact(Artifacts.ShadowCloakCape));
    }

    public ItemStack createRelic(Abilities ability){
        ItemStack i = new ItemStack(Material.SLIME_BALL);
        ItemMeta itemmeta = i.getItemMeta();
        itemmeta.displayName(Component.text("§1- §0§k[§d§l*** §n§4Void Relic §5§l*** §0§k] §1✦"));

        String[] descriptionLines = ability.getDescription().split("\n");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("§4" + ability.getName() + " §7(Right Click)")
        ));
        for (String line : descriptionLines) {
            lore.add(Component.text("§c    " + line));
        }

        itemmeta.lore(lore);
        i.setItemMeta(itemmeta);
        return i;
    }

    private boolean cooldownmanager(Player player, Abilities ability, long Cooldown) {
        if(ByPassCooldown.getCDBypass().contains(player.getUniqueId())) return false;
        CoolDown.putIfAbsent(player.getUniqueId(), new HashMap<>());

        Map<Abilities, Long> playerCooldowns = CoolDown.get(player.getUniqueId());

        if (playerCooldowns.containsKey(ability)) {
            long abilityCooldownEndTime = playerCooldowns.get(ability);

            if (abilityCooldownEndTime > System.currentTimeMillis()) {
                long remainingCooldownSeconds = (abilityCooldownEndTime - System.currentTimeMillis()) / 1000;
                player.sendMessage("§cYou are on cooldown for: " + remainingCooldownSeconds + " seconds");
                return true;
            }
        }

        long cooldownEndTime = System.currentTimeMillis() + Cooldown * 1000;
        player.sendMessage("§cYou are now on cooldown for the ability: §5" + ability.getName() + " §cFor: " + Cooldown + " seconds");

        playerCooldowns.put(ability, cooldownEndTime);
        return false;
    }
}