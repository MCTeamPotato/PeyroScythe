package com.rinko1231.peyroscythe;


import com.rinko1231.peyroscythe.config.PeyroScytheConfig;
import com.rinko1231.peyroscythe.event.MoonFrenzyEvent;
import com.rinko1231.peyroscythe.event.SinfireEmbraceEvent;
import com.rinko1231.peyroscythe.init.*;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static io.redspace.ironsspellbooks.IronsSpellbooks.LOGGER;

@Mod(PeyroScythe.MOD_ID)
public class PeyroScythe {
    public static final String MOD_ID = "peyroscythe";
    public static final String MODID = "peyroscythe"; //下划线很烦

    public PeyroScythe() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PeyroScytheConfig.setup();
        itemRegistry.ITEMS.register(modEventBus);
        TabInit.TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        EntityRegistry.register(modEventBus);
        NewSpellRegistry.register(modEventBus);
        MobEffectRegistry.MOB_EFFECT_DEFERRED_REGISTER.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new MoonFrenzyEvent());
        MinecraftForge.EVENT_BUS.register(new SinfireEmbraceEvent());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    }
    public static ResourceLocation id(@NotNull String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    private void enqueueIMC(InterModEnqueueEvent event) {
        Curios.registerCurioSlot(CuriosRegistry.CHARM_SLOT, 2, false, (ResourceLocation)null);
        //Curios.registerCurioSlot(CuriosRegistry.NECKLACE_SLOT, 1, false, (ResourceLocation)null);
    }

    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if ( !PeyroScytheConfig.entityDropWintermoonHeart.get() && !PeyroScytheConfig.entityDropKnightMedal.get()) return;

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
                } else if (school == SchoolRegistry.ICE.get()&& PeyroScytheConfig.entityDropWintermoonHeart.get()) {
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
                }
                // 只允许勋章
                else if (canDropMedal) {
                    dropStack = new ItemStack(itemRegistry.PONTIFICAL_KNIGHT_MEDAL.get());
                }
                // 只允许寒冬之心
                else if (canDropHeart) {
                    dropStack = new ItemStack(itemRegistry.WINTERMOON_HEART.get());
                }
                // 两个都不允许 -> 不掉落
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

    @SubscribeEvent
    public void onEntityDropsDonut(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        Item heldItem = player.getMainHandItem().getItem();
        if (heldItem == itemRegistry.ICE_JUDGMENT.get()
                || heldItem == itemRegistry.CAPTAIN_GREG.get()
                || heldItem == itemRegistry.CAPTAIN_GREG_TRANSFORMABLE.get()
                || heldItem == itemRegistry.CAPTAIN_GREG_LANCE.get()) {

            // 1% 概率
            if (player.level().random.nextFloat() < 0.01f) {
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

/*
    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();

        // 检查 PersistentData 是否有 moonFrenzyMarks
        if (player.getPersistentData().contains("moonFrenzyMarks", Tag.TAG_LIST)) {
            ListTag marks = player.getPersistentData().getList("moonFrenzyMarks", Tag.TAG_COMPOUND);
            player.sendSystemMessage(Component.literal("§a[MoonFrenzy Debug] Found " + marks.size() + " marks:"));

            for (int i = 0; i < marks.size(); i++) {
                CompoundTag tag = marks.getCompound(i);
                UUID uuid = tag.getUUID("playerUUID");
                int spellLevel = tag.getInt("spellLevel");

                // 试着找这个 UUID 对应的玩家
                ServerPlayer markedPlayer = player.server.getPlayerList().getPlayer(uuid);
                String name = markedPlayer != null ? markedPlayer.getName().getString() : "Offline/Unknown";

                player.sendSystemMessage(Component.literal(" §eMark " + (i + 1) + ": " + uuid + " (" + name + "), spellLevel=" + spellLevel));
            }
        } else {
            player.sendSystemMessage(Component.literal("§c[MoonFrenzy Debug] No moonFrenzyMarks found on you."));
        }
    }
*/
}
