package com.kerix.karalium.pvp.abilities;

import java.util.Arrays;
import java.util.Optional;

public enum Abilities {

    ShadowStep("Shadow Step" , "Allows the player to teleport a short distance, evading attacks and repositioning strategically. \n Additionally, inflicts temporary blindness on nearby enemies upon reappearing."),
    VoidNova("Void Nova" , "Unleashes a shockwave of dark energy that damages and pushes back enemies within its radius.\n Inflicts 'Void Corruption' debuff on affected enemies, reducing their movement speed and making them vulnerable to further attacks."),
    AbyssalBarrier("Abyssal Barrier" , "Summons a protective shield made of swirling void energy that absorbs incoming damage."),
    EclipsingStrike("Eclipsing Strike", "Shoots forward in a straight line, leaving behind a trail of dark energy that damages enemies. \nTriggers an explosion upon reaching maximum range or colliding with an enemy."),
    RiftSummon("Rift Summon", "Allows players to create a rift at a specific location, granting access to the Abyssal Dimension.");

    private final String name;
    private final String description;

    Abilities(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Abilities getAbility(String name) {
        Optional<Abilities> result = Arrays.stream(values())
                .filter(ability -> name.contains(ability.getName()))
                .findFirst();
        return result.orElse(null);
    }

}
