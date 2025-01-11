package com.chongyu.darkfear.mixin;

import com.chongyu.darkfear.GetDarkTime;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {
    @Final
    @Shadow private final List<DamageRecord> recentDamage = Lists.newArrayList();
    @Mutable
    @Final
    @Shadow private final LivingEntity entity;

    public DamageTrackerMixin(LivingEntity entity) {
        this.entity = entity;
    }

    @Inject(method = "getDeathMessage", at = @At("HEAD"), cancellable = true)
    public void getDeathMessage(CallbackInfoReturnable<Text> cir) throws IllegalAccessException {
        DamageRecord damageRecord2 = this.recentDamage.get(this.recentDamage.size() - 1);
        if(this.entity instanceof PlayerEntity player){
            if(damageRecord2.damageSource().getName().equals("generic")){
                if(GetDarkTime.isInDark(player)){
                    cir.setReturnValue(player.getDisplayName().copy().append(Text.translatable(Formatting.RED+" ").append(Text.translatable("death.attack.dark_fear").formatted(Formatting.RED))));
                }
            }
        }
    }
}
