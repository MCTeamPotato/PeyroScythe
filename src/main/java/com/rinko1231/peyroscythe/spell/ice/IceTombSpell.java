package com.rinko1231.peyroscythe.spell.ice;


import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.spellentity.ice.IceTombEntity;
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
import io.redspace.ironsspellbooks.api.util.Utils;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class IceTombSpell extends AbstractSpell {
    private final ResourceLocation spellId = PeyroScythe.id( "ice_tomb");
    private final DefaultConfig defaultConfig;

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.healing", new Object[]{Utils.stringTruncation((double)this.getHealing(spellLevel, caster), 1)}), Component.translatable("ui.irons_spellbooks.duration", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}));
    }

    public IceTombSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.UNCOMMON)
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMaxLevel(8).
                setCooldownSeconds((double)30.0F)
                .build();
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 30;
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

    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {

        IceTombEntity iceTombEntity = new IceTombEntity(world, entity);
        iceTombEntity.moveTo(entity.position());
        iceTombEntity.setDeltaMovement(entity.getDeltaMovement());
        iceTombEntity.setHealing(this.getHealing(spellLevel, entity));
        iceTombEntity.setLifetime(114514);
        iceTombEntity.setEvil();
        world.addFreshEntity(iceTombEntity);
        entity.startRiding(iceTombEntity, true);

        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    public float getDuration(int spellLevel, LivingEntity caster) {
        return 80.0F + (float)(spellLevel * 20) * Mth.sqrt(this.getEntityPowerMultiplier(caster));
    }

    public float getHealing(int spellLevel, LivingEntity caster) {
        return 1.0F * Mth.sqrt(this.getEntityPowerMultiplier(caster));
    }

    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_TWO_HANDS;
    }
}
