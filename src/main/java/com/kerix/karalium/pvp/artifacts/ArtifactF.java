package com.kerix.karalium.pvp.artifacts;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ArtifactF {

    public static ItemStack createArtifact(Artifacts artifact) {
        ItemStack i = new ItemStack(Material.ECHO_SHARD);
        ItemMeta itemmeta = i.getItemMeta();
        itemmeta.displayName(Component.text("§4§l*** §d§l§k▒§5§l§k▒§d§l§k▒§4§lDark §d§l§k▒§5§l§k▒§d§l§k▒§4§l Artifact §d§l§k▒§5§l§k▒§d§l§k▒§4§l***"));
        List<Component> lore = new ArrayList<>() {{
            add(Component.text("§4" + artifact.getName()));
            for (String line : artifact.getDescription().split("\n")) {
                add(Component.text("§c    " + line));
            }
            add(Component.text("§4Corrupted"));
        }};
        itemmeta.lore(lore);
        i.setItemMeta(itemmeta);
        return i;
    }

    public static ItemStack combineArtifactWithArmor(Artifacts artifact, ItemStack armor) {
        ItemStack result = armor.clone();
        ItemMeta meta = result.getItemMeta();
        Multimap<Attribute, AttributeModifier> attr = getAttributes(armor);
        List<Component> lore = new ArrayList<>() {{
            add(Component.text("§4" + artifact.getName()));
            for (String line : artifact.getDescription().split("\n")) {
                add(Component.text("§c    " + line));
            }
            add(Component.text("§4Corrupted"));
        }};
        if (artifact.equals(Artifacts.VoidHeartAmulet)) {
            AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
            EquipmentSlot equipmentSlot = getArmorType(armor);
            if (equipmentSlot != null) attr.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.kb-res", .3D, operation, equipmentSlot));
        }
        meta.lore(lore);
        meta.setAttributeModifiers(attr);
        result.setItemMeta(meta);
        return result;
    }

    private static Multimap<Attribute, AttributeModifier> getAttributes(ItemStack armor) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
        AttributeModifier armorModifier = null;
        AttributeModifier toughnessModifier = null;
        EquipmentSlot eq = getArmorType(armor);
        switch (armor.getType()) {
            case NETHERITE_HELMET, NETHERITE_BOOTS -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 3, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 3D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case NETHERITE_CHESTPLATE -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 3D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case NETHERITE_LEGGINGS -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 6, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 3D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case DIAMOND_HELMET, DIAMOND_BOOTS -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 3, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 2D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case DIAMOND_CHESTPLATE -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 2D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case DIAMOND_LEGGINGS -> {
                armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 6, AttributeModifier.Operation.ADD_NUMBER, eq);
                toughnessModifier = new AttributeModifier(UUID.randomUUID(), "generic.tough", 2D, AttributeModifier.Operation.ADD_NUMBER, eq);
            }
            case IRON_HELMET, IRON_BOOTS, LEATHER_LEGGINGS, GOLDEN_HELMET, CHAINMAIL_HELMET ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 2, AttributeModifier.Operation.ADD_NUMBER, eq);
            case IRON_CHESTPLATE ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 6, AttributeModifier.Operation.ADD_NUMBER, eq);
            case IRON_LEGGINGS, GOLDEN_CHESTPLATE, CHAINMAIL_CHESTPLATE ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 5, AttributeModifier.Operation.ADD_NUMBER, eq);
            case GOLDEN_LEGGINGS, LEATHER_CHESTPLATE ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 3, AttributeModifier.Operation.ADD_NUMBER, eq);
            case GOLDEN_BOOTS, LEATHER_BOOTS, LEATHER_HELMET, CHAINMAIL_BOOTS ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 1, AttributeModifier.Operation.ADD_NUMBER, eq);
            case CHAINMAIL_LEGGINGS ->
                    armorModifier = new AttributeModifier(UUID.randomUUID(), "generic.armor", 4, AttributeModifier.Operation.ADD_NUMBER, eq);
            default -> {}
        }
        attributes.put(Attribute.GENERIC_ARMOR, armorModifier);
        if (toughnessModifier != null) attributes.put(Attribute.GENERIC_ARMOR_TOUGHNESS, toughnessModifier);
        return attributes;
    }

    private static EquipmentSlot getArmorType(ItemStack item){
        EquipmentSlot equipmentSlot = null;
        Material material = item.getType();
        if (material.name().contains("chestplate")) equipmentSlot = EquipmentSlot.CHEST;
        else if (material.name().contains("helmet")) equipmentSlot = EquipmentSlot.HEAD;
        else if (material.name().contains("leggings")) equipmentSlot = EquipmentSlot.LEGS;
        else if (material.name().contains("boots")) equipmentSlot = EquipmentSlot.FEET;
        return equipmentSlot;
    }

    public static boolean isArtifact(ItemStack item) {
        return item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                Objects.equals(item.getItemMeta().displayName(), Component.text("§4§l*** §d§l§k▒§5§l§k▒§d§l§k▒§4§lDark §d§l§k▒§5§l§k▒§d§l§k▒§4§l Artifact §d§l§k▒§5§l§k▒§d§l§k▒§4§l***"));
    }
}
