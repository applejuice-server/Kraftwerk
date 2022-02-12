package pink.mino.kraftwerk.listeners
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.Stats


class PlayerDeathListener : Listener {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val player = e.entity as Player
        val old = e.deathMessage
        player.world.strikeLightningEffect(player.location)
        e.deathMessage = ChatColor.translateAlternateColorCodes('&', "&8»&f $old &8«")
        if (player.world.name == "Arena") {
            e.deathMessage = null
        }
        if (GameState.currentState == GameState.INGAME) {
            val killer = e.entity.killer
            if (killer != null) {
                val o = SettingsFeature.instance.data!!.getInt("game.kills.${killer.name}")
                SettingsFeature.instance.data!!.set("game.kills.${killer.name}", o + 1)
                Stats.addKill(killer)
            }
            val list = SettingsFeature.instance.data!!.getStringList("game.list")
            list.remove(player.name)
            Stats.addDeath(player)
            SettingsFeature.instance.data!!.set("game.list", list)
            SettingsFeature.instance.saveData()
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl remove ${player.name}")
                player.kickPlayer(Chat.colored("&7Thank you for playing!\n\n&7Join our discord for more games: &cdsc.gg/apple-juice"))
            }, 200L)
        }
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            player.spigot().respawn()
        }, 20L)
    }
}