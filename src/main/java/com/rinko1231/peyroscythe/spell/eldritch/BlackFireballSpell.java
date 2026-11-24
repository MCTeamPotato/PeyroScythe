package com.rinko1231.peyroscythe.spell.eldritch;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.eldritch.BlackFireball;
import com.rinko1231.peyroscythe.utils.MyUtils;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.registries.SoundRegistry;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;

@AutoSpellConfig
public class BlackFireballSpell extends AbstractSpell {
    private final ResourceLocation spellId = PeyroScythe.id("black_fireball");
    private final DefaultConfig defaultConfig;

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage",
                new Object[]{Utils.stringTruncation((double)this.getDamage(spellLevel, caster), 2)}),
                Component.translatable("ui.irons_spellbooks.radius",
                new Object[]{Utils.stringTruncation((double)this.getRadius(spellLevel, caster), 2)})
        );
    }

    public BlackFireballSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.RARE)
                .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
                .setMaxLevel(5)
                .setCooldownSeconds((double)40.0F)
                .build();
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 1;
        this.baseManaCost = 80;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.blackFireballAllowLooting.get();
    }

    public CastType getCastType() {
        return CastType.LONG;
    }

    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of((SoundEvent) SoundRegistry.FIREBALL_START.get());
    }

    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of((SoundEvent)SoundRegistry.FIRE_BOMB_CAST.get());
    }
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_CONTINUOUS_CAST_ONE_HANDED;
    }
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.FINISH_ANIMATION;
    }

    public int getCastTime(int spellLevel) {
        return max(0, this.castTime + 20 * (spellLevel - 5));
    }
   public int getRecastCount(int spellLevel, LivingEntity entity) {
        return MyUtils.maxCap(7,spellLevel+1);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!playerMagicData.getPlayerCooldowns().isOnCooldown(this)
                && !playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(
                    new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity),
                            100, castSource, null),
                    playerMagicData
            );
        }
        Vec3 origin = entity.getEyePosition();
        BlackFireball fireball = new BlackFireball(level, entity);
        fireball.setDamage(this.getDamage(spellLevel, entity));
        fireball.setSpellLevel(spellLevel);
        fireball.setExplosionRadius((float)this.getRadius(spellLevel, entity));
        fireball.setPos(origin.add(entity.getForward()).subtract((double)0.0F, (double)(fireball.getBbHeight() / 2.0F), (double)0.0F));
        fireball.shoot(entity.getLookAngle());
        level.addFreshEntity(fireball);
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    public float getDamage(int spellLevel, LivingEntity caster) {
        return 4.0F + 6.0F * this.getSpellPower(spellLevel, caster);
    }

    public float getRadius(int spellLevel, LivingEntity caster) {
        return 1 + (int)this.getSpellPower(spellLevel, caster) *0.7f;
    }
}
