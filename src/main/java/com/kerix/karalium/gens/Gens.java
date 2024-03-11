package com.kerix.karalium.gens;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Optional;

public enum Gens {
    DIRT("Dirt Generator", Generator.DIRT , Generated.DIRT),
    STONE("Stone Generator", Generator.STONE , Generated.STONE);


    private final String name;

    private final Generator generator;
    private final Generated generated;

    Gens(String name , Generator generator , Generated generated){
        this.name = name;
        this.generator = generator;
        this.generated = generated;
    }

    public String getName() {
        return name;
    }

    public Generator getGenerator() {
        return generator;
    }

    public Generated getGenerated() {
        return generated;
    }

    public static Gens getGen(Material material) {
        Optional<Gens> result = Arrays.stream(values())
                .filter(gen -> gen.getGenerator().getItem().getType() == material)
                .findFirst();
        return result.orElse(null);
    }
}
