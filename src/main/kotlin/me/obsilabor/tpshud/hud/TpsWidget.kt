@file:Suppress("unused", "UNUSED_PARAMETER")

package me.obsilabor.tpshud.hud

import com.mojang.blaze3d.systems.RenderSystem
import me.obsilabor.tpshud.TpsTracker
import me.obsilabor.tpshud.config.ConfigManager
import me.obsilabor.tpshud.minecraft
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import org.joml.Matrix4f
import java.awt.Color
import java.math.RoundingMode
import java.math.BigDecimal
import kotlin.math.roundToInt

object TpsWidget {
    fun render(context: DrawContext) {
        val config = ConfigManager.config ?: return
        if(!config.isEnabled) return
        context.matrices.push()
        context.matrices.scale(ConfigManager.config?.scale?:1f, ConfigManager.config?.scale?:1f, 0f)
        if(config.backgroundEnabled) {
            RenderSystem.disableDepthTest()
            fillBackground(context, config.x.toFloat(), config.y.toFloat(), config.x+width.toFloat(), config.y+minecraft.textRenderer.fontHeight+1f, config.backgroundColor, config.backgroundOpacity)
            RenderSystem.enableDepthTest()
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

        // Limit value, either to an integer or to 2 decimal places
        val valueStr = if (config.satisfyTpsCount) {
            value.roundToInt().toString()
        } else {
            BigDecimal(value.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat().toString()
        }

        return Pair(text, valueStr)
    }

    private fun convertToTps(mspt: Float): Float {
        return kotlin.math.min(1000f / mspt, 20f);
    }

    val width: Int
        get() {
            val (text, value) = getTextAndValue()
            return minecraft.textRenderer.getWidth(text) + minecraft.textRenderer.getWidth(value)
        }

    private fun fillBackground(drawContext: DrawContext, x1: Float, y1: Float, x2: Float, y2: Float, color: Int, alpha: Float) {
        // Doesn't work / only works without alpha / looks ugly - Due to removal of RenderSystem.disableTexture
        //fill(matrices.peek().positionMatrix, x1, y1, x2, y2, color, alpha)
        val rgb = Color(color)
        drawContext.fill(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), Color(rgb.red, rgb.blue, rgb.green, (alpha*255).roundToInt()).rgb)
    }

    private fun fill(matrix: Matrix4f, x1: Float, y1: Float, x2: Float, y2: Float, color: Int, alpha: Float) {
        val r = (color shr 16 and 255).toFloat() / 255.0f
        val g = (color shr 8 and 255).toFloat() / 255.0f
        val b = (color and 255).toFloat() / 255.0f
        val var10000 = Tessellator.getInstance()
        val bufferBuilder = var10000.buffer
        RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
        RenderSystem.enableBlend()
        //RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1, y2, 0.0f).color(r, g, b, alpha).next()
        bufferBuilder.vertex(matrix, x2, y2, 0.0f).color(r, g, b, alpha).next()
        bufferBuilder.vertex(matrix, x2, y1, 0.0f).color(r, g, b, alpha).next()
        bufferBuilder.vertex(matrix, x1, y1, 0.0f).color(r, g, b, alpha).next()
        val buffer = bufferBuilder.end()
        BufferRenderer.drawWithGlobalProgram(buffer)
        //RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

}