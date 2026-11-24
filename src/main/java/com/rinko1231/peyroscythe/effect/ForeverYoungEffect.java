package com.rinko1231.peyroscythe.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;

public class ForeverYoungEffect extends MagicMobEffect {

    public ForeverYoungEffect() {

        super(MobEffectCategory.BENEFICIAL, 0);

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {

        return duration % 60 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        if (entity instanceof AgeableMob ageableMob) {ageableMob.setBaby(true); return;}
        if (entity instanceof Zombie zombie) {zombie.setBaby(true);return;}
        if (entity instanceof Piglin piglin) {piglin.setBaby(true);return;}
        if (entity instanceof Zoglin zoglin) {zoglin.setBaby(true);return;}


    }



}
