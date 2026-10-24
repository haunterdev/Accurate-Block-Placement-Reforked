package net.clayborn.accurateblockplacement.mixin;

import net.clayborn.accurateblockplacement.AccurateBlockPlacement;
import net.clayborn.accurateblockplacement.IEntityRendererPickHook;
import net.clayborn.accurateblockplacement.IMinecraftAccessor;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements IMinecraftAccessor {

    @Shadow(aliases = {"rightClickDelayTimer"})
    private int field_71467_ac;

    @Shadow(aliases = {"rightClickMouse"})
    protected abstract void func_147121_ag();

    @Unique
    private boolean wasUseKeyDown = false;

    @Override
    public void accurateblockplacement_doItemUseBypassDisable() {
        Boolean oldValue = AccurateBlockPlacement.disableNormalItemUse;
        AccurateBlockPlacement.disableNormalItemUse = false;
        this.func_147121_ag();
        AccurateBlockPlacement.disableNormalItemUse = oldValue;
    }

    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    private void onRightClickMouse(CallbackInfo ci) {
        if (AccurateBlockPlacement.disableNormalItemUse) {
            ci.cancel();
        }
    }

    /**
     * Detect fresh use-key presses at the top of runTick, before vanilla polls
     * input. runTick later calls EntityRenderer.getMouseOver(1.0F) BEFORE it
     * processes keybind clicks, so the EntityRenderer mixin (which consumes
     * freshPressThisTick) both places the first block itself and raises
     * disableNormalItemUse in time to cancel vanilla's rightClickMouse for the
     * same click, mirroring the 1.20.1 pick()-before-handleKeybinds ordering.
     */
    @Inject(method = "runTick", at = @At("HEAD"))
    private void onRunTickHead(CallbackInfo ci) {
        if (!AccurateBlockPlacement.isEnabled) {
            this.wasUseKeyDown = false;
            return;
        }

        Minecraft mc = (Minecraft) (Object) this;
        if (mc.gameSettings == null || mc.gameSettings.keyBindUseItem == null) {
            return;
        }

        int keyCode = mc.gameSettings.keyBindUseItem.getKeyCode();
        boolean useKeyDown = keyCode < 0
                ? Mouse.isButtonDown(keyCode + 100)
                : Keyboard.isKeyDown(keyCode);
        boolean freshPress = useKeyDown && !this.wasUseKeyDown;
        this.wasUseKeyDown = useKeyDown;

        if (freshPress) {
            AccurateBlockPlacement.freshPressThisTick = true;
        }
    }

    // The runTick half of the getMouseOver fallback in EntityRendererMixin (Potion Core).
    @Inject(method = "runTick", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;getMouseOver(F)V"))
    private void beforeTickPick(CallbackInfo ci) {
        ((IEntityRendererPickHook) ((Minecraft) (Object) this).entityRenderer).accurateblockplacement_beforePick();
    }

    @Inject(method = "runTick", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;getMouseOver(F)V",
                    shift = At.Shift.AFTER))
    private void afterTickPick(CallbackInfo ci) {
        ((IEntityRendererPickHook) ((Minecraft) (Object) this).entityRenderer).accurateblockplacement_afterPick(1.0F);
    }

    @Override
    public void accurateblockplacement_setRightClickDelay(int delay) {
        this.field_71467_ac = delay;
    }

    @Override
    public int accurateblockplacement_getRightClickDelay() {
        return this.field_71467_ac;
    }
}
