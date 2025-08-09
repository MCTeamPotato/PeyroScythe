package com.rinko1231.peyroscythe.spell;

import com.rinko1231.peyroscythe.init.SoundEvents;
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
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;

import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;


import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class RaiseHellSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation("peyroscythe", "raise_hell");
    private final DefaultConfig defaultConfig;

    public RaiseHellSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
                .setMaxLevel(5)
                .setCooldownSeconds(25.0)
                .setAllowCrafting(false)
                .build();
        this.manaCostPerLevel = 45;
        this.baseSpellPower = 15;
        this.spellPowerPerLevel = 0;
        this.castTime = 16;
        this.baseManaCost = 90;
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
        return false;
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
        return Optional.of(SoundEvents.RAISE_HELL_PREPARE.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.FIRE_ERUPTION_SLAM.get());
    }

    public int getRecastCount(int spellLevel, LivingEntity entity) {
        return spellLevel;
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

        FireEruptionAoe aoe = new FireEruptionAoe(level, radius);
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

    private float getRadius(int spellLevel, LivingEntity entity) {
        return 8.0F;
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

    public static void ambientParticles(LivingEntity entity, SyncedSpellData spellData) {
        Vec3 vec3 = entity.getBoundingBox().getCenter();
        for (int i = 0; i < 2; ++i) {
            Vec3 pos = vec3.add(Utils.getRandomVec3(entity.getBbHeight() * 2.0F));
            Vec3 motion = vec3.subtract(pos).scale(0.1F);
            entity.level().addParticle(ParticleHelper.EMBERS, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        }
    }

    public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
        float range = this.getRadius(spellLevel, mob) * 1.1F;
        return Utils.raycastForBlock(mob.level(), mob.position(), mob.position().subtract(0.0F, 0.5F, 0.0F), Fluid.NONE)
                .getType() == Type.MISS
                || target.distanceToSqr(mob) > (range * range);
    }
}
