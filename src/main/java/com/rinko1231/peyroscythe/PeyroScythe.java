package com.rinko1231.peyroscythe;


import com.rinko1231.peyroscythe.api.PreventDismountNew;
import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.effect.abyssal_grace.AbyssalGraceEffectHooks;
import com.rinko1231.peyroscythe.effect.abyssal_grace.AbyssalGraceEvent;
import com.rinko1231.peyroscythe.effect.death_smoke.DeathSmokeEvent;
import com.rinko1231.peyroscythe.effect.illusion.IllusionEffectEventHandler;
import com.rinko1231.peyroscythe.effect.wind_order.WindOrderEvents;
import com.rinko1231.peyroscythe.event.MoonFrenzyEvent;
import com.rinko1231.peyroscythe.event.PriestAngryAtAbyssEvent;
import com.rinko1231.peyroscythe.event.SinfireEmbraceEvent;
import com.rinko1231.peyroscythe.init.*;
import com.rinko1231.peyroscythe.spellentity.SummonedBlaze;
import com.rinko1231.peyroscythe.utils.TimeFlowAccelerator;
import com.rinko1231.peyroscythe.utils.abyssmud.AbyssPortalHooks;
import com.rinko1231.peyroscythe.utils.mundus.TeleportHelper;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.min;

@Mod(PeyroScythe.MOD_ID)
public class PeyroScythe {
    public static final String MOD_ID = "peyroscythe";
    public static final String MODID = "peyroscythe"; //下划线很烦

    public PeyroScythe(IEventBus modEventBus, ModContainer modContainer) {

        modContainer.registerConfig(ModConfig.Type.COMMON, PeyroScytheConfig.SPEC,"PeyroScytheConfig.toml");
        itemRegistry.ITEMS.register(modEventBus);
        //BlockRegistry.register(modEventBus);
        TabInit.TABS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        EntityRegistry.register(modEventBus);
        SoundRegistry.register(modEventBus);
        NewSpellRegistry.register(modEventBus);
        PeyroParticleRegistry.register(modEventBus);
        MobEffectRegistry.MOB_EFFECT_DEFERRED_REGISTER.register(modEventBus);
        NeoForge.EVENT_BUS.register(new MoonFrenzyEvent());
        NeoForge.EVENT_BUS.register(new SinfireEmbraceEvent());
        NeoForge.EVENT_BUS.register(new PriestAngryAtAbyssEvent());
        NeoForge.EVENT_BUS.register(new AbyssalGraceEffectHooks());
        NeoForge.EVENT_BUS.register(new AbyssalGraceEvent());
        NeoForge.EVENT_BUS.register(new DeathSmokeEvent());
        NeoForge.EVENT_BUS.register(new IllusionEffectEventHandler());
        NeoForge.EVENT_BUS.register(new WindOrderEvents());
        NeoForge.EVENT_BUS.register(new AbyssPortalHooks());
        NeoForge.EVENT_BUS.register(new TimeFlowAccelerator());

        modEventBus.addListener(this::onEntityAttributeCreation);

    }

    //好用的ResourceLocation
    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }




    public void onEntityAttributeCreation(final EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.SUMMONED_BLAZE.get(),
                SummonedBlaze.createAttributes().build());
    }
/*
    //世界传送
    @SubscribeEvent
    public void onChat(ServerChatEvent event) {

        ServerPlayer player = event.getPlayer();

        if (player.hasEffect(MobEffectRegistry.VIATOR_MUNDI)) {

            String message = event.getMessage().getString().toLowerCase();

            if (message.contains("ad village")) { // 前往村庄
                int effectLevel = player.getEffect(MobEffectRegistry.VIATOR_MUNDI).getAmplifier() + 1;
                int searchRadius = min(32 + 32 * effectLevel, 160);
                TeleportHelper.tpToNearestVillage(player, searchRadius);
                player.removeEffect(MobEffectRegistry.VIATOR_MUNDI);
                // 阻止消息继续广播到公共聊天
                event.setCanceled(true);
            } else if (message.contains("ad sanctum")) { // 前往圣所
                int effectLevel = player.getEffect(MobEffectRegistry.VIATOR_MUNDI).getAmplifier() + 1;
                int searchRadius = min(32 + 32 * effectLevel, 160);
                TeleportHelper.tpToNearestMundusSupport(player, searchRadius);
                player.removeEffect(MobEffectRegistry.VIATOR_MUNDI);
                // 阻止消息继续广播到公共聊天
                event.setCanceled(true);
            }
        }
    }*/

    //饰品掉落
    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!PeyroScytheConfig.entityDropWintermoonHeart.get() && !PeyroScytheConfig.entityDropKnightMedal.get())
            return;

        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (!entity.getType().is(TagsRegistry.CURIOS_DROP)) return;

        ItemStack dropStack = ItemStack.EMPTY;

        // —— 法术击杀分支：神圣 / 寒冰 必掉对应物品 ——
        if (source instanceof SpellDamageSource spellSource) {
            AbstractSpell spell = spellSource.spell();
            if (spell != null) {
                SchoolType school = spell.getSchoolType();

                if (school == SchoolRegistry.HOLY.get() && PeyroScytheConfig.entityDropKnightMedal.get()) {
                    dropStack = new ItemStack(itemRegistry.PONTIFICAL_KNIGHT_MEDAL.get());
                } else if (school == SchoolRegistry.ICE.get() && PeyroScytheConfig.entityDropWintermoonHeart.get()) {
                    dropStack = new ItemStack(itemRegistry.WINTERMOON_HEART.get());
                }
            }
        }

        // —— 非指定法术（或非法术）击杀：33% 概率掉落其一（不同时） ——
        if (dropStack.isEmpty()) {
            if (entity.level().random.nextFloat() < PeyroScytheConfig.entityDropCuriosItemPossibility.get()) {
                boolean canDropMedal = PeyroScytheConfig.entityDropKnightMedal.get();
                boolean canDropHeart = PeyroScytheConfig.entityDropWintermoonHeart.get();

                // 都允许时，50/50 随机
                if (canDropMedal && canDropHeart) {
                    boolean pickMedal = entity.level().random.nextBoolean();
                    dropStack = new ItemStack(pickMedal
                            ? itemRegistry.PONTIFICAL_KNIGHT_MEDAL.get()
                            : itemRegistry.WINTERMOON_HEART.get());
                } else if (canDropMedal) {
                    dropStack = new ItemStack(itemRegistry.PONTIFICAL_KNIGHT_MEDAL.get());
                } else if (canDropHeart) {
                    dropStack = new ItemStack(itemRegistry.WINTERMOON_HEART.get());
                }
                // 两个都不允许,不掉落
            }
        }


        // —— 生成掉落 ——
        if (!dropStack.isEmpty()) {
            event.getDrops().add(new ItemEntity(
                    entity.level(),
                    entity.getX(), entity.getY(), entity.getZ(),
                    dropStack
            ));
        }
    }

    //尼禄圣光特效
    //暂时弃用，将来潮水我已归来类法术可以用
/*
    @SubscribeEvent
    public void afterLivingRender(RenderLivingEvent.Post<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
        var livingEntity = event.getEntity();
        if (livingEntity instanceof Player) {
            var syncedData = ClientMagicData.getSyncedSpellData(livingEntity);
            if (syncedData.isCasting()) {
                MySpellRenderingHelper.renderSpellHelper(syncedData, livingEntity, event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick());
            }
        }
    }*/
    //甜甜圈掉落
    @SubscribeEvent
    public void onEntityDropsDonut(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        Item heldItem = player.getMainHandItem().getItem();
        if (heldItem == itemRegistry.ICE_JUDGMENT.get()
                || heldItem == itemRegistry.CAPTAIN_GREG.get()
                || heldItem == itemRegistry.CAPTAIN_GREG_PA.get()
                || heldItem == itemRegistry.CAPTAIN_GREG_LANCE_PA.get()
                || heldItem == itemRegistry.CAPTAIN_GREG_TRANSFORMABLE.get()
                || heldItem == itemRegistry.CAPTAIN_GREG_LANCE.get()) {

            if (player.level().random.nextFloat() < PeyroScytheConfig.entityDropDonutPossibility.get()) {
                ItemStack donut = new ItemStack(itemRegistry.CAPTAIN_GREG_DONUT.get());
                event.getDrops().add(new ItemEntity(
                        player.level(),
                        event.getEntity().getX(),
                        event.getEntity().getY(),
                        event.getEntity().getZ(),
                        donut
                ));
            }
        }
    }

    //禁止下马
    @SubscribeEvent
    public void preventDismount(EntityMountEvent event) {
        Entity mount = event.getEntityBeingMounted();
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide && event.isDismounting() && mount instanceof PreventDismountNew preventDismount) {
            if (!mount.isRemoved() && !entity.isRemoved() && !preventDismount.canEntityDismount(entity)) {
                event.setCanceled(true);
            }
        }
    }


}
