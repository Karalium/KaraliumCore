package com.kerix.karalium.pvp.artifacts;

import com.kerix.karalium.core.Main;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.kerix.karalium.core.Main.*;
import static com.kerix.karalium.pvp.artifacts.ArtifactF.combineArtifactWithArmor;
import static com.kerix.karalium.pvp.artifacts.ArtifactF.isArtifact;
import static com.kerix.karalium.pvp.artifacts.Artifacts.*;


public class ArtifactCore implements Listener {

    private final Main main = getINSTANCE();

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack tool = inventory.getFirstItem();
        ItemStack artifact = inventory.getSecondItem();
        if (tool != null && artifact != null && isArmor(tool) && isArtifact(artifact) && !tool.getItemMeta().hasLore()) {
            String name = String.valueOf(Objects.requireNonNull(artifact.getItemMeta().lore()).get(0));
            ItemStack result = combineArtifactWithArmor(getArtifact(name), tool);
            event.setResult(result);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() instanceof AnvilInventory anvil && e.getSlotType() == InventoryType.SlotType.RESULT && e.getCurrentItem() != null) {
            ItemStack armor = anvil.getItem(0);
            ItemStack artifact = anvil.getItem(1);
            if (armor != null && armor.getType() != Material.AIR) {
                ItemMeta meta = armor.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.hasLore()) {
                    e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
                    armor.setAmount(armor.getAmount() - 1);
                    if (artifact != null) artifact.setAmount(artifact.getAmount() - 1);
                    e.setCurrentItem(null);
                }
            }
        }
    }

    private List<Artifacts> wearingArtifact(Player player) {
        List<Artifacts> artifacts = new ArrayList<>();
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                ItemMeta meta = armor.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.hasLore()) {
                    artifacts.add(getArtifact(String.valueOf(Objects.requireNonNull(meta.lore()).get(0))));
                }
            }
        }
        return artifacts;
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        record PlayerArtifact(Player player, List<Artifacts> artifacts) {}

        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            PlayerArtifact attackerArtifact = new PlayerArtifact(attacker, wearingArtifact(attacker));
            PlayerArtifact victimArtifact = new PlayerArtifact(victim, wearingArtifact(victim));

            Arrays.asList(attackerArtifact, victimArtifact).forEach(playerArtifact -> playerArtifact.artifacts.forEach(artifact -> applyArtifactEffect(playerArtifact.player, victimArtifact.player, artifact, event)));
        }
    }

    private void applyArtifactEffect(Player player, @Nullable Player affectedPlayer, Artifacts artifact, EntityDamageByEntityEvent event) {
        assert affectedPlayer != null;
        affectedPlayer.displayName();
        switch (artifact) {
            case VoidHeartAmulet:
                if (player.getHealth() <= Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getDefaultValue() / 2) {
                    event.setDamage(event.getDamage() * 1.15);
                }
                break;
            case CursedSkull:
                if (Math.random() <= 0.2) {
                    player.sendMessage("§5§lCursed Skull has been activated");
                    double newHealth = player.getHealth() + event.getDamage() * 0.1;
                    player.setHealth(Math.min(newHealth, 20));
                }
                break;
            case ShadowCloakCape:
                if (Math.random() <= 0.05) {
                    player.sendMessage("§5§lYou are now under the effect of the Shadow Cloak Cape");
                    PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false);
                    player.addPotionEffect(invisibility);
                    AttributeInstance speed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    if (speed != null) {
                        speed.setBaseValue(speed.getBaseValue() * 1.25);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                speed.setBaseValue(speed.getBaseValue() * 0.8);
                                player.sendMessage("§5§lYou are no longer under the effect of the Shadow Cloak Cape");
                            }
                        }.runTaskLater(main, 100);
                    }
                }
                break;
            default:
                break;
        }
    }
}