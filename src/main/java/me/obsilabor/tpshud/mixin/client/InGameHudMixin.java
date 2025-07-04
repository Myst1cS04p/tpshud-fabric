package me.obsilabor.tpshud.mixin.client;

import me.obsilabor.tpshud.hud.TpsWidget;
import me.obsilabor.tpshud.screen.PositionSelectionScreen;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method = "renderMiscOverlays", at = @At("HEAD"))
    private void renderTpsHud(DrawContext drawContext, RenderTickCounter renderTickCounter, CallbackInfo ci) {
        if(!(MinecraftClient.getInstance().currentScreen instanceof PositionSelectionScreen)) {
            var matrices = drawContext.getMatrices().pushMatrix();
            TpsWidget.INSTANCE.render(drawContext);
            matrices.popMatrix();
        }
    }
}
