package com.rinko1231.peyroscythe.spell.eldritch;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.eldritch.AbyssMudEntity;
import com.rinko1231.peyroscythe.utils.abyssmud.AbyssMudLink;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.eldritch.AbstractEldritchSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class ChaosCradleSpell extends AbstractEldritchSpell {
    private static final ResourceLocation ID =
            new ResourceLocation("peyroscythe", "chaos_cradle");

    private final DefaultConfig defaultConfig;

    public ChaosCradleSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
                .setMaxLevel(3)
                .setCooldownSeconds(180.0)
                .build();
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 0;
        this.castTime = 20;
        this.baseManaCost = 200;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.chaosCradleAllowLooting.get();
    }


    @Override
    public CastType getCastType() {
        return CastType.LONG; // 有读条
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return ID;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.POISON_SPLASH_BEGIN.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.POISON_BREATH_LOOP.get());
    }
    private float getRadius(int spellLevel, LivingEntity entity) {
        return (float)(2 * spellLevel + 2) + 0.1F * this.getSpellPower(spellLevel, entity);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.radius",
                        new Object[]{Utils.stringTruncation((double)this.getRadius(spellLevel, caster), 1)}),
                Component.translatable("ui.irons_spellbooks.duration_seconds",
                        new Object[]{Utils.stringTruncation((double)this.MudDurationGetterInSeconds(spellLevel, caster), 2)}
                        )
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData data) {
        final double r = getRadius(spellLevel, caster);
        final Vec3 center = caster.position();

        if (!level.isClientSide && caster instanceof Player player) {
            ServerLevel sl = (ServerLevel) level;
            // 1) 若当前维度已有登记，先移除旧渊泥
            AbyssMudLink.getMudUUID(player, sl.dimension()).ifPresent(old -> {
                AbyssMudLink.discardMudIfPresent(sl, old);
                AbyssMudLink.removeMudRecord(player, sl.dimension());
            });

            // 2) 生成新渊泥
            AbyssMudEntity mud = new AbyssMudEntity(sl);
            mud.setOwner(caster);
            mud.setDuration(this.MudDurationGetter(spellLevel, caster));
            mud.setRadius((float) r);
            mud.setSpellLevel(spellLevel);
            mud.moveTo(center);
            sl.addFreshEntity(mud);


            // 3) 写入 <维度 -> 渊泥UUID>
            AbyssMudLink.putMudUUID(player, sl.dimension(), mud.getUUID());
        }

        super.onCast(level, spellLevel, caster, castSource, data);
    }
    public int MudDurationGetter (int spellLevel, LivingEntity caster)
    {
        return (PeyroScytheConfig.chaosCradleAbyssalMudBaseDuration.get() + spellLevel * PeyroScytheConfig.chaosCradleAbyssalMudDurationPerLevel.get()) ;
    }
    public double MudDurationGetterInSeconds (int spellLevel, LivingEntity caster)
    {
        return MudDurationGetter(spellLevel, caster)/20.0f;
    }

    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.PREPARE_CROSS_ARMS;
    }

    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.CAST_T_POSE;
    }

}
