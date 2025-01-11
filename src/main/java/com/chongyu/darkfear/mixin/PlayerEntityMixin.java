package com.chongyu.darkfear.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract void tick();

    @Unique
    public int darkTime;
    @Unique
    public int darkHurtTime;
    @Unique
    public int darkRecord;
    @Unique
    public int darkStill;
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        World world = this.getWorld();
        int lightLevel = world.getLightLevel(this.getBlockPos());

        if (lightLevel <= 6) {
            if(!(((int)(this.getWorld().getLevelProperties().getTimeOfDay()/ 24000L)+1) <= 2 || this.hasStatusEffect(StatusEffects.NIGHT_VISION) || this.hasVehicle() || this.hasPassengers() || this.isSwimming())){
                darkTime++;
                if(darkTime==6*20){
                    if(!this.getWorld().isClient){
                        this.sendMessage(Text.translatable("When you are in a dark environment, you will be afraid and hurt!").formatted(Formatting.RED));
                    }
                }
                if(darkTime==10*20){
                    if(!this.getWorld().isClient){
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,10*20,1));
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,5*20,1));
                    }
                }

                if(darkTime==20*20){
                    if(!this.getWorld().isClient){
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,10*20,1));
                    }
                }
                if(darkTime>=30*20){
                    if(!this.getWorld().isClient){
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,10*20,2));
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,10*20,1));
                        darkTime=8*20+10;
                    }
                }

                if(darkTime == 6*20){
                    darkRecord += 1;
                }

                if(darkRecord > 0 ){
                    darkStill++;
                }

                if(darkStill <= 60*20 && darkRecord >= 3){
                    this.damage(this.getDamageSources().generic(),this.getMaxHealth()*0.9f);
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,10*20,2));
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,30*20,2));
                    darkStill=0;
                    darkRecord=0;
                }else if(darkStill > 60*20 && darkRecord >= 3){
                    darkStill=0;
                    darkRecord=0;
                }

                if(darkTime>=6*20){
                    darkHurtTime++;
                    if(darkHurtTime>=2*20){
                        this.damage(this.getDamageSources().generic(),1.0f);
                        darkHurtTime=0;
                    }
                }
            }else {
                darkTime=0;
            }
        }else {
            darkTime=0;
        }
    }

    @Inject(at = @At("RETURN"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        super.readCustomDataFromNbt(nbt);
        darkTime = nbt.getInt("darkTime");
        darkHurtTime = nbt.getInt("darkHurtTime");
        darkRecord = nbt.getInt("darkRecord");
        darkStill = nbt.getInt("darkStill");
    }

    @Inject(at = @At("RETURN"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("darkTime", darkTime);
        nbt.putInt("darkHurtTime", darkHurtTime);
        nbt.putInt("darkRecord", darkRecord);
        nbt.putInt("darkStill", darkStill);
    }
}
