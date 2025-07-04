package me.obsilabor.tpshud.screen

import it.unimi.dsi.fastutil.booleans.BooleanConsumer
import me.obsilabor.tpshud.minecraft
import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.DirectionalLayoutWidget
import net.minecraft.text.Text
import net.minecraft.util.Util
import java.io.File

// Abusing minecraft's ConfirmScreen so I don't have to create my own one TODO: make my own one
class ConfigLibMissingScreen : ConfirmScreen(
    BooleanConsumer {
        val url = when(it) {
            true -> "https://modrinth.com/mod/yacl"
            false -> "https://www.curseforge.com/minecraft/mc-mods/yacl"
        }
        Util.getOperatingSystem().open(url)
    },
    Text.translatable("screen.configLibMissing.title"),
    Text.translatable("screen.configLibMissing.message"),
    Text.translatable("screen.configLibMissing.modrinth"),
    Text.translatable("screen.configLibMissing.curseforge")

) {
    override fun addButtons(directionalLayoutWidget: DirectionalLayoutWidget) {
        super.addButtons(directionalLayoutWidget)
        directionalLayoutWidget.add(ButtonWidget.builder(Text.translatable("screen.configLibMissing.openFolder")) {
            Util.getOperatingSystem().open(File("mods"))
        }.build())
        directionalLayoutWidget.add(ButtonWidget.builder(Text.translatable("screen.configLibMissing.close")) {
            minecraft.setScreen(null)
        }.build())
    }
}
