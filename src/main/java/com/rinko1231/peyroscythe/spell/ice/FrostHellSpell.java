package com.rinko1231.peyroscythe.spell.ice;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.ice.IceQuakeAoe;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;

import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;


import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class FrostHellSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation("peyroscythe", "frost_hell");
    private final DefaultConfig defaultConfig;

    public FrostHellSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMaxLevel(5)
                .setCooldownSeconds(20.0)
                .setAllowCrafting(false)
                .build();
        this.manaCostPerLevel = 30;
        this.baseSpellPower = 9;
        this.spellPowerPerLevel = 2;
        this.castTime = 10;
        this.baseManaCost = 60;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", this.getDamageText(spellLevel, caster)),
                Component.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.recast_count", this.getRecastCount(spellLevel, caster))
        );
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.frostHellAllowLooting.get();
    }

    @Override
    public boolean canBeInterrupted(Player player) {
        return false;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.ICE_CAST.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.ICE_BLOCK_IMPACT.get());
    }

    public int getRecastCount(int spellLevel, LivingEntity entity) {
        return spellLevel+2;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!playerMagicData.getPlayerCooldowns().isOnCooldown(this)
                && !playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(
                    new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity),
                            80, castSource, null),
                    playerMagicData
            );
        }

        float radius = this.getRadius(spellLevel, entity);
        float range = 1.7F;

        Vec3 hitLocation = Utils.moveToRelativeGroundLevel(
                level,
                Utils.raycastForBlock(level, entity.getEyePosition(),
                                entity.getEyePosition().add(entity.getForward().multiply(range, 0.0F, range)), Fluid.NONE)
                        .getLocation(), 3);

        IceQuakeAoe aoe = new IceQuakeAoe(level, radius);
        aoe.setOwner(entity);
        aoe.setDamage(this.getDamage(spellLevel, entity));
        aoe.moveTo(hitLocation);
        level.addFreshEntity(aoe);

        CameraShakeManager.addCameraShake(new CameraShakeData(10 + (int)radius, hitLocation, radius * 2.0F + 5.0F));

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return this.getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity,entity.getMobType());
    }
    @Override
    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker).setFreezeTicks(120);
    }

    private float getRadius(int spellLevel, LivingEntity entity) {
        return spellLevel+ PeyroScytheConfig.frostHellBasicRadius.get().floatValue();
    }

    private String getDamageText(int spellLevel, LivingEntity entity) {
        if (entity != null) {
            float weaponDamage = Utils.getWeaponDamage(entity,entity.getMobType());
            String plus = weaponDamage > 0.0F
                    ? String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1))
                    : "";
            String damage = Utils.stringTruncation(this.getDamage(spellLevel, entity), 1);
            return damage + plus;
        } else {
            return "" + this.getSpellPower(spellLevel, entity);
        }
    }


    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.OVERHEAD_MELEE_SWING_ANIMATION;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return AnimationHolder.pass();
    }

    public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
        float range = this.getRadius(spellLevel, mob) * 1.1F;
        return Utils.raycastForBlock(mob.level(), mob.position(), mob.position().subtract(0.0F, 0.5F, 0.0F), Fluid.NONE)
                .getType() == Type.MISS
                || target.distanceToSqr(mob) > (range * range);
    }
}
