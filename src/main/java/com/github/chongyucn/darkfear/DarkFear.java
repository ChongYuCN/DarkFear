package com.github.chongyucn.darkfear;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class DarkFear implements ModInitializer {

    public static final RegistryKey<DamageType> DARK_FEAR = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("darkfear","dark_fear"));
    @Override
    public void onInitialize() {

    }

}
