package com.kerix.karalium.pvp.artifacts;

import java.util.Arrays;
import java.util.Optional;

public enum Artifacts {
    CursedSkull("Cursed Skull" , "Grants the wearer lifesteal.\n20% Activation rate / 10% Lifesteal rate"),
    VoidHeartAmulet("VoidHeart Amulet" , "Provides immunity to knockback effects and increases damage dealt while below half health.\n15% More Damage"),
    ShadowCloakCape("ShadowCloak Cape" , "Grants temporary invisibility upon taking damage and enhances movement speed for a short duration.\n25% More speed / Effect Timer : 10 seconds / 5% Activation rate");

    private final String name;
    private final String description;

    Artifacts(String name , String description){
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public static Artifacts getArtifact(String name) {
        Optional<Artifacts> result = Arrays.stream(values())
                .filter(artifacts -> name.contains(artifacts.getName()))
                .findFirst();
        return result.orElse(null);
    }
}
