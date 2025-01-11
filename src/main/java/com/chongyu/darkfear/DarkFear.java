package com.chongyu.darkfear;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Identifier;

public class DarkFear implements ModInitializer {

    RegistryKey<DamageType> DARK_FEAR = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dark_fear"));
    DamageSource lava = new DamageSource(DARK_FEAR);
    @Override
    public void onInitialize() {
        Registries.bootstrap();
    }
}
