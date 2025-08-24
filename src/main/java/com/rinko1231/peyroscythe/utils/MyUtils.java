package com.rinko1231.peyroscythe.utils;

import com.rinko1231.peyroscythe.block.MundusBlock;
import com.rinko1231.peyroscythe.block.MundusBlockEntity;
import com.rinko1231.peyroscythe.init.BlockRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static io.redspace.ironsspellbooks.api.util.Utils.checkEntityIntersecting;
import static io.redspace.ironsspellbooks.damage.DamageSources.applyDamage;
import static io.redspace.ironsspellbooks.damage.DamageSources.ignoreNextKnockback;

public class MyUtils {

    // 贯穿：按距离从近到远返回命中的所有实体
    public static List<EntityHitResult> raycastAllEntities(Level level,
                                                    Entity originEntity,
                                                    float distance,
                                                    boolean stopAtBlocks,
                                                    float bbInflation) {
        Vec3 start = originEntity.getEyePosition();
        Vec3 end = originEntity.getLookAngle().normalize().scale(distance).add(start);
        return raycastAllEntities(level, originEntity, start, end, stopAtBlocks, bbInflation, MyUtils::canHitWithRaycast);
    }

    // 贯穿（带自定义过滤器）
    public static List<EntityHitResult> raycastAllEntities(Level level,
                                                           Entity originEntity,
                                                           Vec3 start,
                                                           Vec3 end,
                                                           boolean stopAtBlocks,
                                                           float bbInflation,
                                                           Predicate<? super Entity> filter) {
        BlockHitResult blockHitResult;
        if (stopAtBlocks) {
            blockHitResult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, originEntity));
            // 如果先撞到方块，就把“end”截断到方块命中点
            if (blockHitResult.getType() != HitResult.Type.MISS) {
                end = blockHitResult.getLocation();
            }
        }

        // 用 from start→end 的向量扩张一个范围，收集候选实体
        AABB range = originEntity.getBoundingBox().expandTowards(end.subtract(start));
        List<EntityHitResult> hits = new ArrayList<>();

        for (Entity target : level.getEntities(originEntity, range, filter)) {
            // 复用你已有的判定：对每个实体做一次与射线的精确相交
            HitResult hit = checkEntityIntersecting(target, start, end, bbInflation);
            if (hit.getType() == HitResult.Type.ENTITY) {
                // 安全转换：你自己的 checkEntityIntersecting 返回的是 HitResult，
                // 但在实体命中时应该是 EntityHitResult；这里做个 instanceof 保护
                if (hit instanceof EntityHitResult ehr) {
                    hits.add(ehr);
                } else {
                    // 以防某些实现返回的是通用 HitResult，这里手动构造一个 EntityHitResult
                    hits.add(new EntityHitResult(target, hit.getLocation()));
                }
            }
        }

        // 按距离从近到远排序
        hits.sort(Comparator.comparingDouble(h -> h.getLocation().distanceToSqr(start)));
        return hits;
    }

    // 简便重载：不考虑方块（纯穿透），命中所有实体
    public static List<EntityHitResult> raycastAllEntities(Level level,
                                                    Entity originEntity,
                                                    float distance) {
        return raycastAllEntities(level, originEntity, distance, false, 0.0F);
    }

    public static Vec3 firstBlockHitOrMax(LivingEntity entity, double maxDist) {
        // 起点 = 眼睛位置（带插值），方向 = 视线单位向量
        Vec3 start = entity.getEyePosition();
        Vec3 look  = entity.getLookAngle().normalize();
        Vec3 end   = start.add(look.scale(maxDist));

        // 只检测方块，不考虑流体，不考虑实体
        ClipContext ctx = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
        BlockHitResult blockHit = entity.level().clip(ctx);

        // 命中方块 → 返回命中点；未命中 → 返回极限距离的坐标
        return (blockHit.getType() != HitResult.Type.MISS) ? blockHit.getLocation() : end;
    }

    private static boolean canHitWithRaycast(Entity entity) {
        return entity.isPickable() && entity.isAlive();
    }

    public static void placeMundus(ServerLevel level, BlockPos pos, int lifetimeSeconds, int mundusLevel) {
        BlockState state = BlockRegistry.MUNDUS_BLOCK.get().defaultBlockState()
                .setValue(MundusBlock.LUMINANCE, 15);

        level.setBlock(pos, state, 3);

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MundusBlockEntity mundus) {
            mundus.initLifetimeAndMundusLevel(lifetimeSeconds, mundusLevel);
        }
    }

    /**
     * 对目标施加“按秒计”的持续伤害：将 DPS 均摊到当下 tick。
     * 特点：
     *  - 可选忽略无敌帧（仅对本次结算生效，随后还原），避免 0.5s hurtResist 限制；
     *  - 仍走你已有的 applyDamage 逻辑（法术事件、抗性、友伤判定等）。
     *
     * @param target           目标（LivingEntity）
     * @param dps              每秒伤害（绝对值，非百分比），例如 2.0f 表示每秒 2 点
     * @param damageSource     伤害来源（建议使用你的 SpellDamageSource）
     * @param ignoreHurtResist 是否在本次结算前临时清空 invulnerableTime
     * @param ignoreKnockback  是否忽略击退（会调用 ignoreNextKnockback）
     * @return 是否成功造成伤害（沿用 applyDamage 的返回）
     */
    public static boolean applyDotDamage(LivingEntity target, float dps, DamageSource damageSource,
                                         boolean ignoreHurtResist, boolean ignoreKnockback) {
        if (target.level().isClientSide) return false;
        if (dps <= 0f) return false;

        // 将 DPS 均摊为“每 tick 伤害”
        float perTick = dps / 20.0f;
        perTick = Math.max(perTick, 0.001f);

        int prevInvul = target.invulnerableTime;
        if (ignoreHurtResist) {
            target.invulnerableTime = 0; // 临时清空，避免无敌帧挡住本次 DoT
        }

        if (ignoreKnockback) {
            ignoreNextKnockback(target);
        }

        boolean ok = applyDamage(target, perTick, damageSource);

        if (ignoreHurtResist) {
            // 还原 invulnerableTime，避免影响其他来源的受击节奏
            target.invulnerableTime = prevInvul;
        }
        return ok;
    }

    /**
     * 对目标施加“按秒百分比”的持续伤害（按最大生命的比例）。
     * 例如 percentPerSecond = 0.02f 表示每秒 2% 最大生命。
     */
    public static boolean applyDotPercent(LivingEntity target, float percentPerSecond, DamageSource damageSource,
                                          boolean ignoreHurtResist, boolean ignoreKnockback) {
        if (percentPerSecond <= 0f) return false;
        float dps = target.getMaxHealth() * percentPerSecond;
        return applyDotDamage(target, dps, damageSource, ignoreHurtResist, ignoreKnockback);
    }

}
