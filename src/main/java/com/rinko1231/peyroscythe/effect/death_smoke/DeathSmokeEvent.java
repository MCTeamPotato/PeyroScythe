package com.rinko1231.peyroscythe.effect.death_smoke;


import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.init.MobEffectRegistry;
import com.rinko1231.peyroscythe.init.NewSpellRegistry;
import com.rinko1231.peyroscythe.init.TagsRegistry;
import com.rinko1231.peyroscythe.spellentity.eldritch.BlackFireball;
import com.rinko1231.peyroscythe.spellentity.eldritch.BlackFireballSmall;
import com.rinko1231.peyroscythe.spellentity.eldritch.DeathSmokeProjectile;
import com.rinko1231.peyroscythe.utils.MyUtils;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;

import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;


import static com.rinko1231.peyroscythe.PeyroScythe.MODID;
import static io.redspace.ironsspellbooks.registries.MobEffectRegistry.ABYSSAL_SHROUD;
import static io.redspace.ironsspellbooks.registries.MobEffectRegistry.HEARTSTOP;


public class DeathSmokeEvent {


    //死烟结算
    @SubscribeEvent
    public void DeathSmokeKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        DamageSource source = event.getSource();

        LivingEntity victim = event.getEntity();
        if (!(source instanceof SpellDamageSource))
            return;

        if (source instanceof SpellDamageSource spellSource) {
            if (spellSource.spell()!= NewSpellRegistry.DEATH_SMOKE_SPELL.get())
                return;
        }

        MagicData magicData = MagicData.getPlayerMagicData(player);
        // 计算回血/回蓝
        int spellLevel;
        if (source.getDirectEntity() instanceof DeathSmokeProjectile deathSmokeProjectile)
            spellLevel = deathSmokeProjectile.getSpellLevel();
        else spellLevel = 1;

        float healAmount = (float) (PeyroScytheConfig.deathSmokeHealthToHealthTransferRatePerLevel.get()
                * spellLevel * victim.getMaxHealth());
        float manaAmount = (float) (PeyroScytheConfig.deathSmokeHealthToManaTransferRatePerLevel.get()
                * spellLevel * victim.getMaxHealth());

        // 给玩家回血
        player.heal(healAmount);
//测试
        //player.displayClientMessage(Component.literal("deathSmokeKill"),false);
        // 给玩家回蓝
        magicData.addMana(manaAmount);
        PacketDistributor.sendToPlayer(player, new SyncManaPacket(magicData));
    }

    @SubscribeEvent
    public void BlackFireballKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        DamageSource source = event.getSource();

        LivingEntity victim = event.getEntity();
        if (!(source instanceof SpellDamageSource))
            return;
        if (source instanceof SpellDamageSource spellSource) {
            if (spellSource.spell()!= NewSpellRegistry.BLACK_FIREBALL.get())
                return;
        }

        MagicData magicData = MagicData.getPlayerMagicData(player);
        // 计算回血/回蓝
        int spellLevel;
        if (source.getDirectEntity() instanceof BlackFireball blackFireball)
            spellLevel = blackFireball.getSpellLevel();
        else if (source.getDirectEntity() instanceof BlackFireballSmall blackFireballSmall)
            spellLevel = blackFireballSmall.getSpellLevel();
        else
            spellLevel = 1;

        float healAmount = (float) (PeyroScytheConfig.deathSmokeHealthToHealthTransferRatePerLevel.get()
                * spellLevel * victim.getMaxHealth());
        float manaAmount = (float) (PeyroScytheConfig.deathSmokeHealthToManaTransferRatePerLevel.get()
                * spellLevel * victim.getMaxHealth());

        // 给玩家回血
        player.heal(healAmount);
//测试
        //player.displayClientMessage(Component.literal("blackFireballKill"),false);
        // 给玩家回蓝
        magicData.addMana(manaAmount);
        PacketDistributor.sendToPlayer(player, new SyncManaPacket(magicData));
    }


    //非死烟的死烟伤害
    @SubscribeEvent
    public void onLivingHurt(LivingDamageEvent.Pre event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        LivingEntity target = event.getEntity();
        // 跳过免疫目标
        if (target.getType().is(TagsRegistry.DEATH_SMOKE_IMMUNE)) {
            return;
        }
        if(target.hasEffect(ABYSSAL_SHROUD) || target.hasEffect(HEARTSTOP)) return;
        DamageSource source = event.getSource();
        if (!(source instanceof SpellDamageSource) )
            return;
        // 判断是否是 DeathSmoke 的伤害
        if (source instanceof SpellDamageSource spellSource) {
            if (spellSource.spell()!= NewSpellRegistry.DEATH_SMOKE_SPELL.get())
                return;
        }

        // DeathSmokeProjectile 已经自己处理过了，这里只管“非Projectile来源”
        if (source.getDirectEntity() instanceof DeathSmokeProjectile) {
            return;
        }



        // ========== 叠加侵蚀效果 ==========
        MobEffectInstance existing = target.getEffect(MobEffectRegistry.DEATH_SMOKE_EROSION);
        int amp = 0;

        if (existing != null) {
            amp = existing.getAmplifier() + 1; // 叠加等级

        }
        target.addEffect(new MobEffectInstance(
                MobEffectRegistry.DEATH_SMOKE_EROSION,
                PeyroScytheConfig.deathSmokeErosionDuration.get(),
                amp,
                false,
                true,
                true
        ));

        // ========== 斩杀检测 ==========
        int level = amp + 1;
        // 这里要获取施法者信息（伤害来源的 owner）
        Entity attacker = source.getEntity();
        int spellLevel = 1;

        double killThreshold = PeyroScytheConfig.deathSmokeBasicKillThreshold.get()
                + (PeyroScytheConfig.deathSmokeKillThresholdGrowthPerLevel.get() * spellLevel * (level - 1));

        float healthPct = target.getHealth() / target.getMaxHealth();
        //if (source.getEntity() instanceof ServerPlayer p)
        //   p.displayClientMessage(Component.literal("死烟吞噬阈值: " + killThreshold), false);

        if (healthPct <= killThreshold) {

            target.removeAllEffects(); // 避免庇护
            if(target.getHealth()>1F) target.setHealth(1F); // 防止假死
            event.setNewDamage(MyUtils.minCap(100, target.getMaxHealth()));
        }
    }

/*
    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        // 检查是不是生物
        if (event.getEntity().level().isClientSide) return;

        MobEffectInstance inst = event.getEffectInstance(); // Expired 这里一般不为空，但也防御

        // 检查是不是死烟侵蚀效果
        if (inst != null && inst.getEffect() == MobEffectRegistry.DEATH_SMOKE_EROSION.get()) {
            MobEffectInstance expired = event.getEffectInstance();
            int amp = expired.getAmplifier();
        if(event.getEntity() instanceof  ServerPlayer pp)
            pp.displayClientMessage(Component.literal("expired: "+amp),false);
        if (amp >= 2) {
                // 刷新新的效果，amp - 2，持续 4 秒（80 tick）
                event.getEntity().addEffect(new MobEffectInstance(
                        MobEffectRegistry.DEATH_SMOKE_EROSION.get(),
                        80,                // 4 秒
                        amp - 2,           // 降 2 级
                        false,
                        true,
                        true
                ));
            }
        }
    }
*/


}
