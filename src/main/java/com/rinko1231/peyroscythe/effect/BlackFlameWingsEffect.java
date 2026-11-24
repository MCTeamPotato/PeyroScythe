package com.rinko1231.peyroscythe.effect;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import com.rinko1231.peyroscythe.init.PeyroParticleRegistry;
import com.rinko1231.peyroscythe.network.data.PeyroMagicData;
import com.rinko1231.peyroscythe.network.data.PeyroSyncedSpellData;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.redspace.ironsspellbooks.registries.MobEffectRegistry.ANGEL_WINGS;
import static java.lang.Math.min;

public class BlackFlameWingsEffect extends MagicMobEffect {


    private static final int manaLoss = 1;
    public BlackFlameWingsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
        this.addAttributeModifier(AttributeRegistry.ELDRITCH_SPELL_POWER.get(),
                "a7eb46a1-40e8-beaf-2a39-b9de6e553344",
                0.15,
                AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(AttributeRegistry.SPELL_RESIST.get(),
                "6ad162ff-7839-c914-cddd-9dbb798d7ce4",
                0.15,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS,
                "9786b360-0589-7365-a782-d40544989983",
                0.3,
                AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                "bb4c9ecd-0167-ec36-aa11-b34ca337bff8",
                0.3,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity player, int amplifier) {
        if(player.hasEffect(ANGEL_WINGS.get()))
            player.removeEffect(ANGEL_WINGS.get());
        this.ambientParticles(player);
        List<Entity> list = player.level().getEntities(player, player.getBoundingBox().inflate((double)0.25F, (double)0.5F, (double)0.25F));
        if (!list.isEmpty()) {
            for(Entity targetEntity : list) {
                if (targetEntity instanceof LivingEntity livingTarget) {
                    DamageSources.applyDamage(targetEntity, min(livingTarget.getMaxHealth()*0.01f,1), ((AbstractSpell) NewSpellRegistry.DEATH_SMOKE_SPELL.get()).getDamageSource(player));
                    targetEntity.invulnerableTime = 20;
                }
            }
        }
        player.fallDistance = 0.0F;
        if(player instanceof ServerPlayer) {
            MagicData entityMagicData = MagicData.getPlayerMagicData(player);
            if(entityMagicData.getMana()>manaLoss)
               entityMagicData.addMana(-manaLoss);
            else player.removeEffect(this);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 10 == 0; // 每tick调用
    }


    public void ambientParticles(LivingEntity entity) {
        Vec3 backwards = entity.getForward().scale(0.003).reverse().add(0.0, 0.1, 0.0); // 往上偏移 0.1，使粒子在身体上方而非眼睛正前方
        RandomSource random = entity.getRandom();

        for (int i = 0; i < 5; ++i) {
            // 在身体周围随机分布
            Vec3 motion = new Vec3(
                    (random.nextFloat() * 2.0 - 1.0),
                    (random.nextFloat() * 1.0 - 0.5), // y 方向偏小
                    (random.nextFloat() * 2.0 - 1.0)
            );
            motion = motion.scale(0.04).add(backwards);

            // 粒子生成位置偏离中心，围绕身体
            double px = entity.getX() + (random.nextDouble() - 0.5) * 0.6;
            double py = entity.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.8; // 在腰部到肩膀之间
            double pz = entity.getZ() + (random.nextDouble() - 0.5) * 0.6;

            entity.level().addParticle(
                    PeyroParticleRegistry.BLACK_FLAME.get(),
                    px, py, pz,
                    motion.x, motion.y, motion.z
            );
        }
    }


    public void addAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {

          /*  if (pLivingEntity.getAttribute(AttributeRegistry.ELDRITCH_SPELL_POWER.get()) != null) {
                pLivingEntity.getAttribute(AttributeRegistry.ELDRITCH_SPELL_POWER.get()).addTransientModifier(new AttributeModifier(
                        UUID.fromString("a7eb46a1-40e8-beaf-2a39-b9de6e553344"),
                        "black_flame_eldritch_power_up",
                        0.15,
                        AttributeModifier.Operation.MULTIPLY_BASE
                ));
            }
            if (pLivingEntity.getAttribute(AttributeRegistry.SPELL_RESIST.get()) != null) {
                pLivingEntity.getAttribute(AttributeRegistry.SPELL_RESIST.get()).addTransientModifier(new AttributeModifier(
                        UUID.fromString("6ad162ff-7839-c914-cddd-9dbb798d7ce4"),
                        "black_flame_spell_resist_up",
                        0.20,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }

            if (pLivingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS) != null) {
                pLivingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS).addTransientModifier(new AttributeModifier(
                        UUID.fromString("9786b360-0589-7365-a782-d40544989983"),
                        "black_flame_armor_toughness_up",
                        0.3,
                        AttributeModifier.Operation.MULTIPLY_BASE
                ));
            }

            if (pLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                pLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(
                        UUID.fromString("bb4c9ecd-0167-ec36-aa11-b34ca337bff8"),
                        "black_flame_move_speed_up",
                        0.3,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }*/


        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        PeyroMagicData.getPlayerData(pLivingEntity).getSyncedData().addEffect(PeyroSyncedSpellData.BLACK_FLAME_WINGS);

    }

    public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
           /* if (pLivingEntity.getAttribute(AttributeRegistry.ELDRITCH_SPELL_POWER.get()) != null
             && pLivingEntity.getAttributes().hasModifier(AttributeRegistry.ELDRITCH_SPELL_POWER.get(),UUID.fromString("a7eb46a1-40e8-beaf-2a39-b9de6e553344"))) {
                pLivingEntity.getAttribute(AttributeRegistry.ELDRITCH_SPELL_POWER.get())
                        .removeModifier(UUID.fromString("a7eb46a1-40e8-beaf-2a39-b9de6e553344"));
            }

            if (pLivingEntity.getAttribute(AttributeRegistry.SPELL_RESIST.get()) != null
                    && pLivingEntity.getAttributes().hasModifier(AttributeRegistry.SPELL_RESIST.get(),UUID.fromString("6ad162ff-7839-c914-cddd-9dbb798d7ce4"))) {
                pLivingEntity.getAttribute(AttributeRegistry.SPELL_RESIST.get())
                        .removeModifier(UUID.fromString("6ad162ff-7839-c914-cddd-9dbb798d7ce4"));
            }

            if (pLivingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS) != null
                    && pLivingEntity.getAttributes().hasModifier(Attributes.ARMOR_TOUGHNESS,UUID.fromString("9786b360-0589-7365-a782-d40544989983"))) {
                pLivingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS)
                        .removeModifier(UUID.fromString("9786b360-0589-7365-a782-d40544989983"));
            }

            if (pLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED) != null
                    && pLivingEntity.getAttributes().hasModifier(Attributes.MOVEMENT_SPEED,UUID.fromString("bb4c9ecd-0167-ec36-aa11-b34ca337bff8"))) {
                pLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED)
                        .removeModifier(UUID.fromString("bb4c9ecd-0167-ec36-aa11-b34ca337bff8"));
            }*/

        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        PeyroMagicData.getPlayerData(pLivingEntity).getSyncedData().removeEffect(PeyroSyncedSpellData.BLACK_FLAME_WINGS);
    }
}
