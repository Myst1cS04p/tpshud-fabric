@file:Suppress("unused")

package me.obsilabor.tpshud.hud

import me.obsilabor.tpshud.TpsTracker
import me.obsilabor.tpshud.config.ConfigManager
import me.obsilabor.tpshud.minecraft
import net.minecraft.client.gui.DrawContext
import java.awt.Color
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

object TpsWidget {
    fun render(context: DrawContext) {
        val config = ConfigManager.config ?: return
        if(!config.isEnabled) return
        val matrices = context.matrices
        matrices.scale(ConfigManager.config?.scale?:1f, ConfigManager.config?.scale?:1f, matrices)
        if(config.backgroundEnabled) {
            fillBackground(context, config.x.toFloat(), config.y.toFloat(), config.x+width.toFloat(), config.y+minecraft.textRenderer.fontHeight.toFloat()/*+1f*/, config.backgroundColor, config.backgroundOpacity)
        }
        val text = ConfigManager.config?.text ?: "TPS: "
        val widthPartOne = minecraft.textRenderer.getWidth(text)
        context.drawText(minecraft.textRenderer, text, config.x, config.y, config.textColor, config.textShadow)
        context.drawText(minecraft.textRenderer, round(TpsTracker.INSTANCE.tickRate), config.x+widthPartOne, config.y, config.valueTextColor, config.textShadow)
    }

    private fun round(tps: Float): String {
        var copy = tps
        copy = if(ConfigManager.config?.satisfyTpsCount == true) {
            copy.roundToInt().toFloat()
        } else {
            BigDecimal(copy.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat() // Limit characters
        }
        return copy.toString()
    }

    val width: Int
        get() = minecraft.textRenderer.getWidth(ConfigManager.config?.text ?: "TPS: ")+minecraft.textRenderer.getWidth(round(TpsTracker.INSTANCE.tickRate))

    private fun fillBackground(drawContext: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, color: Int, alpha: Float) {
        val rgb = Color(color)
        drawContext.fill(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), Color(rgb.red, rgb.blue, rgb.green, (alpha*255).roundToInt()).rgb)
    }
}