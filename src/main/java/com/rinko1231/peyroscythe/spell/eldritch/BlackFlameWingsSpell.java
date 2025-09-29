package com.rinko1231.peyroscythe.spell.eldritch;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

import static io.redspace.ironsspellbooks.registries.MobEffectRegistry.ANGEL_WINGS;

@AutoSpellConfig
public class BlackFlameWingsSpell extends AbstractSpell {
        private final ResourceLocation spellId = PeyroScythe.id( "black_flame_wings");
        private final DefaultConfig defaultConfig;

        public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
            return List.of(Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getSpellPower(spellLevel, caster) * 20.0F, 1)}));
        }

        public BlackFlameWingsSpell() {
            this.defaultConfig = (new DefaultConfig())
                    .setMinRarity(SpellRarity.EPIC)
                    .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
                    .setMaxLevel(5)
                    .setCooldownSeconds((double)150.0F)
                    .build();
            this.manaCostPerLevel = 20;
            this.baseSpellPower = 30;
            this.spellPowerPerLevel = 30;
            this.castTime = 0;
            this.baseManaCost = 80;
        }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.chaosCradleAllowLooting.get();
    }

        public CastType getCastType() {
            return CastType.INSTANT;
        }

        private int getEffectDuration(int spellLevel, LivingEntity entity) {
            return (int)this.getSpellPower(spellLevel, entity) * 20;
        }

        public DefaultConfig getDefaultConfig() {
            return this.defaultConfig;
        }

        public ResourceLocation getSpellResource() {
            return this.spellId;
        }

        public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
            if(entity.hasEffect(ANGEL_WINGS))
                entity.removeEffect(ANGEL_WINGS);
            entity.addEffect(new MobEffectInstance(MobEffectRegistry.BLACK_FLAME_WINGS, this.getEffectDuration(spellLevel, entity), 0, false, false, true), entity);
            super.onCast(world, spellLevel, entity, castSource, playerMagicData);
        }
    }
