package com.rinko1231.peyroscythe.spell.holy;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.holy.MundusEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

@AutoSpellConfig
public class MundusSpell extends AbstractSpell {
    private final ResourceLocation spellId = PeyroScythe.id("mundus");
    private final DefaultConfig defaultConfig;

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.radius_chunk",
                new Object[]{Utils.stringTruncation((double)this.getSearchRadius(spellLevel), 1)}));
    }

    public MundusSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
                .setMaxLevel(3)
                .setCooldownSeconds((double)280.0F)
                .setAllowCrafting(false)
                .build();
        this.manaCostPerLevel = 25;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 100;
        this.baseManaCost = 75;
    }

    public CastType getCastType() {
        return CastType.LONG;
    }

    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.mundusAllowLooting.get();
    }

    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of((SoundEvent) SoundRegistry.BLACK_HOLE_CHARGE.get());
    }

    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of((SoundEvent)SoundRegistry.BLACK_HOLE_CAST.get());
    }

    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float radius = this.getRadius(spellLevel, entity);
        HitResult raycast = Utils.raycastForEntity(level, entity, 16.0F + radius * 1.5F, true);
        Vec3 center = raycast.getLocation();
        if (raycast instanceof BlockHitResult blockHitResult) {
            if (blockHitResult.getDirection().getAxis().isHorizontal()) {
                center = center.subtract((double)0.0F, (double)radius, (double)0.0F);
            } else if (blockHitResult.getDirection() == Direction.DOWN) {
                center = center.subtract((double)0.0F, (double)(radius * 2.0F), (double)0.0F);
            }
        }

        level.playSound((Player)null, center.x, center.y, center.z, (SoundEvent)SoundRegistry.BLACK_HOLE_CAST.get(), SoundSource.AMBIENT, 4.0F, 1.0F);
        MundusEntity mundusEntity = new MundusEntity(level, entity);
        mundusEntity.setRadius(radius);
        mundusEntity.moveTo(center);
        mundusEntity.setSpellLevel(spellLevel);
        level.addFreshEntity(mundusEntity);
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }


    private float getRadius(int spellLevel, LivingEntity entity) {
        return 2.0f ;
    }

    private float getSearchRadius(int spellLevel) {
        return min(32 + 32 * spellLevel, 160);
    }

    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_ANIMATION;
    }

    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.FINISH_ANIMATION;
    }

    public boolean stopSoundOnCancel() {
        return true;
    }
}
