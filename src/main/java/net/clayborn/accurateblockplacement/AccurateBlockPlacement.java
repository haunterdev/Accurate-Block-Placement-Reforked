package net.clayborn.accurateblockplacement;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(AccurateBlockPlacement.MODID)
public class AccurateBlockPlacement {
    public static final String MODID = "accurateblockplacement";

    public static Boolean disableNormalItemUse = false;
    public static boolean isEnabled = true;

    private static KeyMapping toggleKey;

    public AccurateBlockPlacement() {
        // Key registration lives in ClientModEvents below. Registering it from this constructor
        // made the mod-bus listener resolve RegisterKeyMappingsEvent, which loads the client-only
        // Options class and crashed a dedicated server at mod construction.
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            toggleKey = new KeyMapping(
                    "key.accurateblockplacement.toggle",
                    InputConstants.Type.KEYSYM,
                    InputConstants.UNKNOWN.getValue(),
                    "key.categories.accurateblockplacement"
            );
            event.register(toggleKey);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            while (toggleKey != null && toggleKey.consumeClick()) {
                isEnabled = !isEnabled;
                mc.player.displayClientMessage(
                        Component.translatable(isEnabled
                                ? "message.accurateblockplacement.enabled"
                                : "message.accurateblockplacement.disabled"),
                        true
                );
            }
        }
    }
}
