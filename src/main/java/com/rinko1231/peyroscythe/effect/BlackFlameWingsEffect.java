package com.rinko1231.peyroscythe.effect;

import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import com.rinko1231.peyroscythe.init.PeyroParticleRegistry;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;
import static io.redspace.ironsspellbooks.registries.MobEffectRegistry.ANGEL_WINGS;
import static java.lang.Math.min;

public class BlackFlameWingsEffect extends MagicMobEffect implements ISyncedMobEffect {


    private static final int manaLoss = 1;
    public BlackFlameWingsEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
        this.addAttributeModifier(AttributeRegistry.ELDRITCH_SPELL_POWER,
                ResourceLocation.fromNamespaceAndPath(MODID,"b_wings_eld_power"),
                0.15F,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(AttributeRegistry.SPELL_RESIST,
                ResourceLocation.fromNamespaceAndPath(MODID,"b_spell_resist"),
                0.15,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS,
                ResourceLocation.fromNamespaceAndPath(MODID,"b_wings_armor_toughness"),
                0.3,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(MODID,"b_wings_move_speed"),
                0.3,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity player, int amplifier) {
        if(player.hasEffect(ANGEL_WINGS))
            player.removeEffect(ANGEL_WINGS);
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
        if(player instanceof ServerPlayer sp) {
            MagicData entityMagicData = MagicData.getPlayerMagicData(player);
            if(entityMagicData.getMana()>manaLoss)
            {entityMagicData.addMana(-manaLoss);
                PacketDistributor.sendToPlayer(sp, new SyncManaPacket(entityMagicData));
            }
            else return false;
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
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

}
