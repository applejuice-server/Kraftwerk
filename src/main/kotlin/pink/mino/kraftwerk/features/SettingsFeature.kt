package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException


class SettingsFeature private constructor() {
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

    /**
     * Sets the settings manager up and creates missing files.
     * @param p the main class.
     */
    fun setup(p: Plugin) {
        if (!p.dataFolder.exists()) {
            p.dataFolder.mkdir()
        }
        dfile = File(p.dataFolder, "config.yml")
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
    }

    /**
     * Saves the data config.
     */
    fun saveData() {
        try {
            data!!.save(dfile)
        } catch (ex: IOException) {
            Bukkit.getServer().logger.severe(ChatColor.RED.toString() + "Could not save config.yml!")
        }
    }

    /**
     * Reloads the data file.
     */
    fun reloadData() {
        data = YamlConfiguration.loadConfiguration(dfile)
    }

    companion object {
        val instance = SettingsFeature()
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