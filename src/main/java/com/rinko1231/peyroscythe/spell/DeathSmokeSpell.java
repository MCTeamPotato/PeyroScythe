package com.rinko1231.peyroscythe.spell;

import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.DeathSmokeProjectile;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.EntityCastData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;

@AutoSpellConfig
public class DeathSmokeSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(MOD_ID, "death_smoke");
    private final DefaultConfig defaultConfig;

    public DeathSmokeSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
                .setMaxLevel(3)
                .setCooldownSeconds((double) 20.0F)
                .build();
        this.manaCostPerLevel = 9;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 100;
        this.baseManaCost = 8;
    }

    @Override
    public boolean allowLooting() {
        return PeyroScytheConfig.deathSmokeAllowLooting.get();
    }

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation((double) this.getDamage(spellLevel, caster), 2)}));
    }

    public CastType getCastType() {
        return CastType.CONTINUOUS;
    }

    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of((SoundEvent) SoundRegistry.POISON_BREATH_LOOP.get());
    }

    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (playerMagicData.isCasting() && playerMagicData.getCastingSpellId().equals(this.getSpellId())) {
            ICastData breath = playerMagicData.getAdditionalCastData();
            if (breath instanceof EntityCastData) {
                EntityCastData entityCastData = (EntityCastData) breath;
                Entity var9 = entityCastData.getCastingEntity();
                if (var9 instanceof AbstractConeProjectile) {
                    AbstractConeProjectile cone = (AbstractConeProjectile) var9;
                    cone.setDealDamageActive();
                    return;
                }
            }
        }

        DeathSmokeProjectile breath = new DeathSmokeProjectile(world, entity);
        breath.setPos(entity.position().add((double) 0.0F, (double) entity.getEyeHeight() * 0.7, (double) 0.0F));
        breath.setDamage(this.getDamage(spellLevel, entity));
        breath.setSpellLevel(spellLevel);
        world.addFreshEntity(breath);
        playerMagicData.setAdditionalCastData(new EntityCastData(breath));
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 1;
    }

    public float getDamage(int spellLevel, LivingEntity caster) {
        return 1.0F + this.getSpellPower(spellLevel, caster) * 0.75F;
    }

    @Override
    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker);
    }

    public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
        return mob.distanceToSqr(target) > (double) 120.0F;
    }
}
