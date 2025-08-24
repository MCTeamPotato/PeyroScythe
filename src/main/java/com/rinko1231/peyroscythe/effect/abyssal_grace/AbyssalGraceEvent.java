package com.rinko1231.peyroscythe.effect.abyssal_grace;


import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.utils.abyssmud.AbyssMudLink;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.rinko1231.peyroscythe.PeyroScythe.MODID;
import static net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public class AbyssalGraceEvent {
        public static final String TAG_HAS_GRACE   = AbyssalGraceEffect.TAG_HAS_GRACE;
    public static final String TAG_PENDING_REV = "peyroscythe:abyss_pending_revive";

        /** 如果玩家既有 BUFF 又持有不死图腾：拦截图腾，不让它浪费。 */
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onUseTotem(LivingUseTotemEvent event) {
            LivingEntity e = event.getEntity();
            if (e.level().isClientSide) return;

            // 原版不会对虚空等某些来源生效，你也可以选择保持一致
            if (event.getSource().is(FELL_OUT_OF_WORLD)) return;

            if (e.getPersistentData().getBoolean(TAG_HAS_GRACE)) {
                // 标记“待复活”，交给 LivingDeathEvent 真正拉回
                e.getPersistentData().putBoolean(TAG_PENDING_REV, true);
                // 取消图腾使用（这个事件是可取消的）
                event.setCanceled(true); // 图腾不再介入
            }
        }

        /** 兜底 & 真正执行免死：无图腾时也能救，或在拦截图腾后把人拉回。 */
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onDeath(LivingDeathEvent event) {
            LivingEntity e = event.getEntity();
            if (e.level().isClientSide) return;

            // 不处理虚空/命令等（行为向原版看齐，可按需调整）
            DamageSource src = event.getSource();
            if (src.is(FELL_OUT_OF_WORLD)) return;

            boolean hasGrace  = e.getPersistentData().getBoolean(TAG_HAS_GRACE);
            boolean pending   = e.getPersistentData().getBoolean(TAG_PENDING_REV);

            if (hasGrace || pending) {
                // 消耗这次“免死”
                consumeGraceAndRevive(e);
                event.setCanceled(true); // 真正阻止死亡
            }
        }

        private static void consumeGraceAndRevive(LivingEntity livingEntity) {
            Level level = livingEntity.level();

            // 1) 清标志 & 去掉 BUFF（无论是自然过期还是现在被消费）
            livingEntity.getPersistentData().remove(TAG_HAS_GRACE);
            livingEntity.getPersistentData().remove(TAG_PENDING_REV);
            livingEntity.removeEffect(MobEffectRegistry.ABYSSAL_GRACE.get());

            // 2) 拉回 1 血，并给一组保护效果（可自定义）

            float healAmount = livingEntity.getMaxHealth() * 0.15f;
            MinecraftForge.EVENT_BUS.post(new SpellHealEvent(livingEntity, livingEntity, healAmount, SchoolRegistry.ELDRITCH.get()));
            livingEntity.setHealth(healAmount);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 45 * 20, 1));   // II, 45s
            //e.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40 * 20, 0));// I, 40s
            //e.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 5 * 20, 1));      // II, 5s


            // 3) 尝试移除当前维度自己的渊泥 + 清掉记录
            Level lvl = livingEntity.level();
            if (livingEntity instanceof Player p && lvl instanceof ServerLevel sl) {
                AbyssMudLink.getMudUUID(p, sl.dimension()).ifPresent(uuid -> {
                    AbyssMudLink.discardMudIfPresent(sl, uuid);
                    AbyssMudLink.removeMudRecord(p, sl.dimension());
                });
                p.displayClientMessage(Component.translatable("message.peyroscythe.abyssal_grace_revive"),false);
            }
            // 3) 复刻图腾的客户端演出（中心图腾动画 + 声音）
            if (level instanceof ServerLevel serverLevel) {
                // 35：与原版图腾相同的“激活演出”
                //serverLevel.broadcastEntityEvent(livingEntity, (byte)35);  // 客户端会显示悬浮的图腾动画
                serverLevel.playSound(null, livingEntity.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            // 4) 可选：短 CD 防止同 tick 多次致死反复触发（复杂战斗里更稳）
            // e.getPersistentData().putLong("peyroscythe:grace_cooldown_until", level.getGameTime() + 2);
        }
    }
