package com.rinko1231.peyroscythe.spell.holy;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.holy.HolyLanceProjectile;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
@AutoSpellConfig
public class HolyLanceSpell extends AbstractSpell {
        private final ResourceLocation spellId = PeyroScythe.id("holy_lance");
        private final DefaultConfig defaultConfig;

        public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
            return List.of(
                    Component.translatable("ui.irons_spellbooks.damage",
                            new Object[]{Utils.stringTruncation((double)this.getSpellPower(spellLevel, caster), 1)})
           );
        }

        public HolyLanceSpell() {
            this.defaultConfig = (new DefaultConfig())
                    .setMinRarity(SpellRarity.UNCOMMON)
                    .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
                    .setMaxLevel(10)
                    .setCooldownSeconds((double)8.0F)
                    .build();
            this.manaCostPerLevel = 11;
            this.baseSpellPower = 10;
            this.spellPowerPerLevel = 2;
            this.castTime = 20;
            this.baseManaCost = 60;
        }
    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.holyLanceAllowLooting.get();
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
            return Optional.of((SoundEvent) SoundRegistry.LIGHTNING_LANCE_CAST.get());
        }

        public Optional<SoundEvent> getCastFinishSound() {
            return Optional.of((SoundEvent)SoundRegistry.LIGHTNING_WOOSH_01.get());
        }

        public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
            HolyLanceProjectile lance = new HolyLanceProjectile(level, entity);
            lance.setPos(entity.position().add((double)0.0F, (double)entity.getEyeHeight(), (double)0.0F).add(entity.getForward()));
            lance.shoot(entity.getLookAngle());
            lance.setMaxPierce(this.MaxPierce(spellLevel));
            lance.setDamage(this.getSpellPower(spellLevel, entity));
            level.addFreshEntity(lance);
            super.onCast(level, spellLevel, entity, castSource, playerMagicData);
        }
        public int MaxPierce(int spellLevel)
        {
            return 3+ (int) (spellLevel/5);
        }

        public AnimationHolder getCastStartAnimation() {
            return SpellAnimations.ANIMATION_CHARGED_CAST;
        }
    }
