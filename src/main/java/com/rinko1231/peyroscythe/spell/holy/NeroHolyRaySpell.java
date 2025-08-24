package com.rinko1231.peyroscythe.spell.holy;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MyMessages;
import com.rinko1231.peyroscythe.network.spell.ClientboundBloodSiphonReverseParticles;
import com.rinko1231.peyroscythe.spellentity.holy.NeroHolyRayVisualEntity;
import com.rinko1231.peyroscythe.utils.MyUtils;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class NeroHolyRaySpell extends AbstractSpell {
    private final ResourceLocation spellId = PeyroScythe.id("nero_holy_ray");
    private final DefaultConfig defaultConfig;

    public NeroHolyRaySpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.EPIC)
                .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
                .setMaxLevel(5)
                .setCooldownSeconds((double) 12.0F).build();
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 20;
        this.spellPowerPerLevel = 5;
        this.castTime = 0;
        this.baseManaCost = 40;
    }

    public static float getRange(int spellLevel) {
        return PeyroScytheConfig.holyRayRange.get().floatValue();
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.holyRayAllowLooting.get();
    }

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation((double) this.getRayDamage(spellLevel, caster), 2)}), Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation((double) getRange(spellLevel), 1)}));
    }

    public CastType getCastType() {
        return CastType.INSTANT;
    }

    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of((SoundEvent) SoundRegistry.LIGHTNING_LANCE_CAST.get());
    }

    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_CONTINUOUS_CAST_ONE_HANDED;
    }
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.FINISH_ANIMATION;
    }

    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of((SoundEvent) SoundRegistry.LIGHTNING_LANCE_CAST.get());
    }

    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        List<EntityHitResult> allHits = MyUtils.raycastAllEntities(level, caster, getRange(spellLevel), true, 0.15F);
        level.addFreshEntity(new NeroHolyRayVisualEntity(level, caster.getEyePosition(), MyUtils.firstBlockHitOrMax(caster,getRange(spellLevel)), caster));

        // 对路径上所有实体逐个处理
        for (EntityHitResult ehr : allHits) {
            // 你的命中逻辑……
            if (ehr.getType() == HitResult.Type.ENTITY) {
                Entity target = ((EntityHitResult) ehr).getEntity();
                if (target instanceof LivingEntity livingEntity && DamageSources.applyDamage(target, this.getRayDamage(spellLevel, caster, livingEntity) + getExtraDamage(livingEntity,0.05f + 0.03f * spellLevel,0.8f), this.getDamageSource(caster))) {
                    MyMessages.sendToPlayersTrackingEntity(new ClientboundBloodSiphonReverseParticles(target.position().add((double) 0.0F, (double) (target.getBbHeight() / 2.0F), (double) 0.0F), caster.position().add((double) 0.0F, (double) (caster.getBbHeight() / 2.0F), (double) 0.0F)), caster, true);
                    //Messages.sendToPlayersTrackingEntity(new ClientboundBloodSiphonParticles(target.position().add((double) 0.0F, (double) (target.getBbHeight() / 2.0F), (double) 0.0F), entity.position().add((double) 0.0F, (double) (entity.getBbHeight() / 2.0F), (double) 0.0F)), entity, true);
                }
            }
        }

        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker);
    }

    private float getRayDamage(int spellLevel, LivingEntity caster, LivingEntity target) {
        if (target.getMobType() == MobType.UNDEAD)
        return this.getSpellPower(spellLevel, caster) * 0.3F * (1.0F+ PeyroScytheConfig.holyRayExtraRatioDamageToUndead.get().floatValue());
        else
            return this.getSpellPower(spellLevel, caster) * 0.3F;
    }
    private float getRayDamage(int spellLevel, LivingEntity caster) {
        return this.getSpellPower(spellLevel, caster) * 0.3F;
    }

    public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
        return mob.distanceToSqr(target) > (double) (getRange(spellLevel) * getRange(spellLevel)) * 1.2;
    }
    public float getExtraDamage(LivingEntity target, float maxPercent, float threshold) {
        float RealMaxPercent = Math.min(maxPercent, PeyroScytheConfig.holyRayExtraMaxHealthDamageRatioCap.get().floatValue());
        float maxHealth = target.getMaxHealth();
        float currentHealth = target.getHealth();
        float lostPercent = (maxHealth - currentHealth) / maxHealth;

        if (lostPercent < threshold) {
            return (lostPercent / threshold) * (maxHealth * RealMaxPercent);
        }

        return Math.min (maxHealth * RealMaxPercent, PeyroScytheConfig.holyRayExtraMaxHealthDamageCap.get().floatValue());
    }
}
