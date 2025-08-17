package com.rinko1231.peyroscythe.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.InterModComms;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class CuriosRegistry {

        public static String CHARM_SLOT = "charm";
    public static String NECKLACE_SLOT = "necklace";

        public CuriosRegistry() {
        }
        @SuppressWarnings("removal")
        public static void registerCurioSlot(String identifier, int slots, boolean isHidden, @Nullable ResourceLocation icon) {

            SlotTypeMessage.Builder message = new SlotTypeMessage.Builder(identifier);
            message.size(slots);
            if (isHidden) {

                message.hide();
            }

            if (icon != null) {
                message.icon(icon);
            }

            InterModComms.sendTo("curios", "register_type", () -> message.build());
        }
    }
