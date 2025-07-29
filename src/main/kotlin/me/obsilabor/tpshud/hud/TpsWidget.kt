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

        // Get text and value, for MSPT or TPS
        var (text, value) = getTextAndValue()

        // Render it
        val widthPartOne = minecraft.textRenderer.getWidth(text)
        context.drawText(minecraft.textRenderer, text, config.x, config.y, config.textColor, config.textShadow)
        context.drawText(minecraft.textRenderer, value, config.x+widthPartOne, config.y, config.valueTextColor, config.textShadow)
    }

    // Return text and value, ready for rendering
    private fun getTextAndValue(): Pair<String, String> {
        val config = ConfigManager.config ?: return Pair("TPS: ", "0.00")

        var (text, value) = if(config.displayModeTps) {
            Pair("TPS: ", convertToTps(TpsTracker.INSTANCE.tickTime))
        } else {
            Pair("MSPT: ", TpsTracker.INSTANCE.tickTime)
        }
        // Override text if customText is set
        if (!config.customText.isEmpty()) {
            text = config.customText
        }
        var valueStr = BigDecimal(value.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat().toString() // Limit characters
        return Pair(text, valueStr)
    }

    private fun convertToTps(mspt: Float): Float {
        var tps = kotlin.math.min(1000f / mspt, 20f); // convert mspt to tps
        if(ConfigManager.config?.satisfyTpsCount == true) {
            return tps.roundToInt().toFloat()
        }
        return tps
    }

    val width: Int
        get() {
            val (text, value) = getTextAndValue()
            return minecraft.textRenderer.getWidth(text) + minecraft.textRenderer.getWidth(value)
        }

    private fun fillBackground(drawContext: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, color: Int, alpha: Float) {
        val rgb = Color(color)
        drawContext.fill(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), Color(rgb.red, rgb.blue, rgb.green, (alpha*255).roundToInt()).rgb)
    }
}