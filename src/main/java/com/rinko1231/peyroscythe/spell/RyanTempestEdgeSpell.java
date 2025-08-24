package com.rinko1231.peyroscythe.spell;

import com.rinko1231.peyroscythe.PeyroScythe;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.spellentity.TempestEdgeHProjectile;
import com.rinko1231.peyroscythe.spellentity.TempestEdgeProjectile;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.blood_slash.BloodSlashProjectile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

@AutoSpellConfig
public class RyanTempestEdgeSpell extends AbstractSpell {
    private final ResourceLocation spellId = PeyroScythe.id( "ryan_tempest_edge");
    private final DefaultConfig defaultConfig;

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", this.getDamageText(spellLevel, caster)),
                Component.translatable("ui.irons_spellbooks.recast_count", this.getRecastCount(spellLevel, caster))

        );
    }

    public RyanTempestEdgeSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.EPIC)
                .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
                .setMaxLevel(5)
                .setCooldownSeconds((double)60.0F)
                .setAllowCrafting(false)
                .build();
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 2;
        this.castTime = 0;
        //this.baseManaCost = 25;
        this.baseManaCost = 100;
    }

    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public boolean allowLooting() {
        return false;
    }

    public int getRecastCount(int spellLevel, LivingEntity entity) {
        return spellLevel/2 + 4;
    }

    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!playerMagicData.getPlayerCooldowns().isOnCooldown(this)
                && !playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
            playerMagicData.getPlayerRecasts().addRecast(
                    new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity),
                            80, castSource, null),
                    playerMagicData
            );
        }

        if (!entity.isShiftKeyDown()) {
        TempestEdgeProjectile tempestEdgeProjectile = new TempestEdgeProjectile(world, entity);
        tempestEdgeProjectile.setPos(entity.getEyePosition());
        tempestEdgeProjectile.shoot(entity.getLookAngle());
        tempestEdgeProjectile.setDamage(this.getSpellPower(spellLevel, entity)*1.5f);
        world.addFreshEntity(tempestEdgeProjectile);}
        else {
            TempestEdgeHProjectile tempestEdgeHProjectile = new TempestEdgeHProjectile(world, entity);
            tempestEdgeHProjectile.setPos(entity.getEyePosition());
            tempestEdgeHProjectile.shoot(entity.getLookAngle());
            tempestEdgeHProjectile.setDamage(this.getSpellPower(spellLevel, entity));
            world.addFreshEntity(tempestEdgeHProjectile);
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker);
    }
    private float getDamage(int spellLevel, LivingEntity entity) {
        return this.getSpellPower(spellLevel, entity) + Utils.getWeaponDamage(entity,entity.getMobType());
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

    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SLASH_ANIMATION;
    }
}
