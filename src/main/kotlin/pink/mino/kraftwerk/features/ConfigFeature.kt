package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import pink.mino.kraftwerk.Kraftwerk
import java.io.File
import java.io.IOException


class ConfigFeature private constructor() {
    /**
     * Gets the data config.
     * @return the config.
     */
    var data: FileConfiguration? = null
        private set
    private var dfile: File? = null
    var worlds: FileConfiguration? = null
        private set
    private var wfile: File? = null
    var config: FileConfiguration? = null
        private set

    /**
     * Sets the settings manager up and creates missing files.
     * @param p the main class.
     */
    fun setup(p: Plugin) {
        if (!p.dataFolder.exists()) {
            p.dataFolder.mkdir()
        }
        dfile = File(p.dataFolder, "data.yml")
        if (!dfile!!.exists()) {
            try {
                dfile!!.createNewFile()
            } catch (ex: IOException) {
                Bukkit.getServer().logger.severe(ChatColor.RED.toString() + "Could not create config.yml!")
            }
        }
        data = YamlConfiguration.loadConfiguration(dfile)

        wfile = File(p.dataFolder, "worlds.yml")
        if (!wfile!!.exists()) {
            try {
                wfile!!.createNewFile()
            } catch (ex: IOException) {
                Bukkit.getServer().logger.severe(ChatColor.RED.toString() + "Could not create worlds.yml!")
            }
        }
        worlds = YamlConfiguration.loadConfiguration(wfile)

        val configFile = File(Kraftwerk.instance.dataFolder, "config.yml")
        if (!(configFile.exists())) {
            configFile.parentFile.mkdir()
            Kraftwerk.instance.saveResource("config.yml", false)
        }
        try {
            config = YamlConfiguration.loadConfiguration(configFile)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InvalidConfigurationException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Saves the data config.
     */
    fun saveData() {
        try {
            data!!.save(dfile)
            val configFile = File(Kraftwerk.instance.dataFolder, "config.yml")
            config!!.save(configFile)
        } catch (ex: IOException) {
            Bukkit.getServer().logger.severe(ChatColor.RED.toString() + "Could not save data.yml!")
        }
    }

    /**
     * Reloads the data file.
     */
    fun reloadData() {
        data = YamlConfiguration.loadConfiguration(dfile)
    }

    companion object {
        val instance = ConfigFeature()
    }

    fun saveWorlds() {
        try {
            worlds!!.save(wfile)
        } catch (ex: IOException) {
            Bukkit.getServer().logger.severe(ChatColor.RED.toString() + "Could not save worlds.yml!")
        }
    }

    fun reloadWorlds() {
        worlds = YamlConfiguration.loadConfiguration(wfile)
    }
}