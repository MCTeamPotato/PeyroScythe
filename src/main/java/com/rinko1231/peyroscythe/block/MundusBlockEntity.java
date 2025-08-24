package com.rinko1231.peyroscythe.block;

import com.rinko1231.peyroscythe.init.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
public class MundusBlockEntity extends BlockEntity {
    // 刷新与范围参数
    private static final int GLOW_REFRESH_INTERVAL_TICKS = 20; //刷新
    private static final int GLOW_DURATION_TICKS = 40;
    private static final double BASE_RADIUS = 6.0;

    // 状态
    private long expireGameTime = -1L; // 过期时间
    private int mundusLevel = 1;       // 方块实体的“等级”
    private int tickCounter = 0;

    public MundusBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.MUNDUS_BE.get(), pos, state);
    }

    //在放置后由调用者设置：存在时长（秒）与 mundusLevel
    public void initLifetimeAndMundusLevel(int lifetimeSeconds, int mundusLevel) {
        if (mundusLevel <= 0) mundusLevel = 1;
        this.mundusLevel = mundusLevel;

        Level lvl = this.level;
        if (lvl instanceof ServerLevel sl) {
            this.expireGameTime = sl.getGameTime() + lifetimeSeconds * 20L;
        }

        // 根据等级设置亮度（示例：8 + 等级*2，最多 15）
        int lum = Math.min(15, Math.max(0, 8 + mundusLevel * 2));
        if (lvl != null) {
            BlockState now = getBlockState();
            lvl.setBlock(worldPosition, now.setValue(MundusBlock.LUMINANCE, lum), 3);
        }
        setChanged();
    }


    public void serverTick() {
        Level lvl = this.level;
        if (!(lvl instanceof ServerLevel sl)) return;

        if (expireGameTime >= 0 && sl.getGameTime() >= expireGameTime) {
            sl.removeBlock(worldPosition, false);
            return;
        }

        // 每秒为范围内生物刷新buff
        if (++tickCounter >= GLOW_REFRESH_INTERVAL_TICKS) {
            tickCounter = 0;
            double radius = BASE_RADIUS + (mundusLevel * 2.0);
            AABB box = new AABB(worldPosition).inflate(radius);

            List<LivingEntity> list = sl.getEntitiesOfClass(LivingEntity.class, box);
            for (LivingEntity e : list) {
                e.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_DURATION_TICKS, 0, true, false));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putLong("Expire", expireGameTime);
        tag.putInt("MundusLevel", mundusLevel);
        tag.putInt("Tick", tickCounter);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.expireGameTime = tag.getLong("Expire");
        this.mundusLevel = tag.getInt("MundusLevel");
        this.tickCounter = tag.getInt("Tick");
    }
}
