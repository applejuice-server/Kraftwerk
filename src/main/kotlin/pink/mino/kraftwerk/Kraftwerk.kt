package pink.mino.kraftwerk

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.listeners.PlayerJoin
import pink.mino.kraftwerk.listeners.ServerListPing


class Kraftwerk : JavaPlugin() {

    override fun onEnable() {
        /* Registering listeners */
        Bukkit.getServer().pluginManager.registerEvents(ServerListPing(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerJoin(), this)

        Bukkit.getLogger().info("Kraftwerk enabled")
    }
    override fun onDisable() {
        Bukkit.getLogger().info("Kraftwerk disabled")
    }

}