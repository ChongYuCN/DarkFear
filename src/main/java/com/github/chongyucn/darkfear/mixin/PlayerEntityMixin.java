package com.github.chongyucn.darkfear.mixin;

import com.github.chongyucn.darkfear.DarkFear;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
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

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.getEntityWorld() instanceof ServerWorld world) {
            int lightLevel = world.getLightLevel(this.getBlockPos());

            if (lightLevel <= 6) {
                if(!(((int)(world.getLevelProperties().getTimeOfDay()/ 24000L)+1) <= 2 || this.hasStatusEffect(StatusEffects.NIGHT_VISION) || this.hasVehicle() || this.hasPassengers() || this.isSwimming())){
                    darkTime++;
                    if(darkTime==6*20){
                        if(!world.isClient()){
                            this.sendMessage(Text.translatable("When you are in a dark environment, you will be afraid and hurt!").formatted(Formatting.RED),false);
                        }
                    }
                    if(darkTime==10*20){
                        if(!world.isClient()){
                            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,10*20,1));
                            this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,5*20,1));
                        }
                    }

                    if(darkTime==20*20){
                        if(!world.isClient()){
                            this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS,10*20,1));
                        }
                    }
                    if(darkTime>=30*20){
                        if(!world.isClient()){
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

                    DamageSource source = new DamageSource(
                            world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(DarkFear.DARK_FEAR)
                    );
                    if(darkStill <= 60*20 && darkRecord >= 3){
                        this.damage((ServerWorld) world,source,this.getMaxHealth()*0.9f);
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
                            this.damage((ServerWorld) world,source,1.0f);
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
    }

    @Inject(at = @At("TAIL"), method = "readCustomData")
    public void readCustomDataFromNbt(ReadView view, CallbackInfo ci) {
        darkTime = view.getInt("darkTime",0);
        darkHurtTime = view.getInt("darkHurtTime",0);
        darkRecord = view.getInt("darkRecord",0);
        darkStill = view.getInt("darkStill",0);
    }

    @Inject(at = @At("TAIL"), method = "writeCustomData")
    public void writeCustomDataToNbt(WriteView view, CallbackInfo ci) {
        view.putInt("darkTime", darkTime);
        view.putInt("darkHurtTime", darkHurtTime);
        view.putInt("darkRecord", darkRecord);
        view.putInt("darkStill", darkStill);
    }

    @Shadow
    public abstract void sendMessage(Text message, boolean overlay);
}
