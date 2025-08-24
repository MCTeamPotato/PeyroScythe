package com.rinko1231.peyroscythe.spellentity.holy;



import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.EntityRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.*;

public class HolyLanceProjectile extends AbstractMagicProjectile {
    private int maxPierce = 3; // 最大穿透次数
    private int piercedCount = 0; // 已穿透次数
    private final Set<Integer> piercedEntityIds = new HashSet<>(); // 已经击中过的实体，避免重复伤害

    public HolyLanceProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.setNoGravity(false);
    }

    public HolyLanceProjectile(Level level, LivingEntity shooter) {
        this(EntityRegistry.HOLY_LANCE_PROJECTILE.get(), level);
        this.setOwner(shooter);
    }

    public void setMaxPierce(int maxPierce) {
        this.maxPierce = maxPierce;
    }
    // 飞行轨迹粒子
    @Override
    public void trailParticles() {
        Vec3 vec3 = this.position().subtract(this.getDeltaMovement());
        this.level().addParticle(ParticleTypes.END_ROD, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
    }


    public void impactParticles(double x, double y, double z) {
        if (this.level().isClientSide) {
            return;
        }
        MagicManager.spawnParticles(this.level(), ParticleHelper.WISP, x, y, z, 50, 0.1, 0.1, 0.1, 0.2, true);
    }

    public float getSpeed() {
        return 3.5F;
    }

    public Optional<SoundEvent> getImpactSound() {
        return Optional.of(SoundEvents.TRIDENT_HIT);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {

        this.impactParticles(result.getLocation().x, result.getLocation().y, result.getLocation().z);
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();

        // 如果已经击中过该实体，不再触发
        if (piercedEntityIds.contains(target.getId())) {
            return;
        }
        piercedEntityIds.add(target.getId());


        Entity owner = this.getOwner();

        DamageSources.applyDamage(target, this.damage * (1.0f + PeyroScytheConfig.holyLanceExtraRatioDamageToUndead.get().floatValue()), ( NewSpellRegistry.HOLY_LANCE.get()).getDamageSource(this, owner));

        this.impactParticles(target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ());

        piercedCount++;

        // 达到最大穿透次数，移除
        if (piercedCount >= maxPierce) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        // 只处理 block 命中时的逻辑，entity 命中在 onHitEntity 里独立处理
        if (result.getType() == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) result);
        }
        super.onHit(result);
    }

    public int getAge() {
        return this.tickCount;
    }
}
