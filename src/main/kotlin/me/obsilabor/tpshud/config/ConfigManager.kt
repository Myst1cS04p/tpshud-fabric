package me.obsilabor.tpshud.config

import me.obsilabor.tpshud.json
import java.io.File

object ConfigManager {

    fun saveConfigToFile() {
        configFile.writeText(json.encodeToString(config))
    }

    private val configFile = File(System.getProperty("user.dir") + "/config", "tpshud_v3.json")
    var config: TpsHudConfig? = null
    val configOrException: TpsHudConfig
        get() = config ?: throw RuntimeException("Config is null")

    init {
        if(!configFile.parentFile.exists()) {
            configFile.parentFile.mkdirs()
        }
        if(!configFile.exists()) {
            configFile.createNewFile()
            configFile.writeText(json.encodeToString(TpsHudConfig.DEFAULT))
        }
        runCatching {
            config = json.decodeFromString(configFile.readText())
        }
    }
}