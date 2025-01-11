package com.chongyu.darkfear.mixin;

import com.chongyu.darkfear.damagesource.MyDamagesouce;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSources.class)
public class DamageSourcesMixin implements MyDamagesouce {
    private DamageSource darkFear;

    @Mutable
    @Final
    @Shadow
    public final Registry<DamageType> registry;

    RegistryKey<DamageType> DARK_FEAR = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("dark_fear"));

    public DamageSourcesMixin(DamageSource darkFear, Registry<DamageType> registry) {
        this.darkFear = darkFear;
        this.registry = registry;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void DamageSourcesMixin(DynamicRegistryManager registryManager, CallbackInfo ci) {
        this.darkFear = this.create(DARK_FEAR);
    }

    @Shadow
    public final DamageSource create(RegistryKey<DamageType> key) {
        return new DamageSource(this.registry.entryOf(key));
    }

    @Override
    public DamageSource getDarkfear() {
        return null;
    }


}
