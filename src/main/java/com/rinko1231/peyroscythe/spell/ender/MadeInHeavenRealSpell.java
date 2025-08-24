package com.rinko1231.peyroscythe.spell.ender;

import com.rinko1231.peyroscythe.spellentity.ender.MadeInHeavenProjectile;
import com.rinko1231.peyroscythe.spellentity.ender.MadeInHeavenRealProjectile;
import com.rinko1231.peyroscythe.utils.AbstractProjectileReverse;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.EntityCastData;
import io.redspace.ironsspellbooks.spells.eldritch.AbstractEldritchSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

import static com.rinko1231.peyroscythe.PeyroScythe.MOD_ID;

@AutoSpellConfig
public class MadeInHeavenRealSpell extends AbstractEldritchSpell {
    private static final ResourceLocation ID = new ResourceLocation(MOD_ID, "made_in_heaven_real");
    private final DefaultConfig defaultConfig;



    public MadeInHeavenRealSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
                .setMaxLevel(2)
                .setCooldownSeconds(300.0)
                .setAllowCrafting(true)
                .build();
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = 400;
        this.baseManaCost = 30;
    }

    @Override
    public boolean allowLooting() { return false; }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.radius",
                        new Object[]{Utils.stringTruncation((double)this.getSpeedUpRadius(spellLevel), 0)}),
                Component.translatable("ui.peyroscythe.time_factor",
                        new Object[]{Utils.stringTruncation((double)this.getFactor(spellLevel), 0)}));
    }
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.ANIMATION_CONTINUOUS_OVERHEAD;
    }

    @Override
    public CastType getCastType() { return CastType.CONTINUOUS; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public ResourceLocation getSpellResource() { return ID; }

    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of((SoundEvent) SoundRegistry.BLACK_HOLE_CHARGE.get());
    }

    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    public int getFactor(int spellLevel) {
        spellLevel = Mth.clamp(spellLevel, 1, 2);
        return LEVEL_TO_FACTOR[spellLevel];
    }
    // —— 你可以把这个倍率表挪到 config —— //
    private static final int[] LEVEL_TO_FACTOR = {1, 20, 40, 120};

    public float getSpeedUpRadius(int spellLevel) {
        spellLevel = Mth.clamp(spellLevel, 1, 2);
        return LEVEL_TO_SPEEDUP_RADIUS[spellLevel];
    }
    // —— 你可以把这个倍率表挪到 config —— //
    private static final int[] LEVEL_TO_SPEEDUP_RADIUS = {1, 3, 5};


    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (playerMagicData.isCasting() && playerMagicData.getCastingSpellId().equals(this.getSpellId())) {
            ICastData breath = playerMagicData.getAdditionalCastData();
            if (breath instanceof EntityCastData) {
                EntityCastData entityCastData = (EntityCastData) breath;
                Entity var9 = entityCastData.getCastingEntity();
                if (var9 instanceof AbstractProjectileReverse) {
                    AbstractProjectileReverse cone = (AbstractProjectileReverse) var9;
                    return;
                }
            }
        }

        MadeInHeavenRealProjectile breath = new MadeInHeavenRealProjectile(world, entity);
        breath.setPos(entity.position().add((double) 0.0F, (double) entity.getEyeHeight() * 0.7, (double) 0.0F));
        breath.setSpellLevel(spellLevel);
        breath.setSpeedUpRadius(this.getSpeedUpRadius(spellLevel));
        world.addFreshEntity(breath);
        playerMagicData.setAdditionalCastData(new EntityCastData(breath));

        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }


    @Override
    public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
        return true;
    }

}
