package pink.mino.kraftwerk.listeners

import com.wimbli.WorldBorder.Config
import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent
import com.wimbli.WorldBorder.Events.WorldBorderFillStartEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.ActionBar
import pink.mino.kraftwerk.utils.Chat
import kotlin.math.roundToInt

class PregenListener : Listener {

    val prefix = "&8[${Chat.primaryColor}Server&8]&7"
    @EventHandler
    fun on(event: WorldBorderFillStartEvent) {
        object : BukkitRunnable() {
            override fun run() {
                if (Config.fillTask.valid()) {
                    if (!(event.fillTask.refWorld() == null)) {
                        val rounded = (Config.fillTask.percentageCompleted * 100.0).roundToInt() / 100.0
                        for (player in Bukkit.getOnlinePlayers()) {
                            ActionBar.sendActionBarMessage(player, ChatColor.translateAlternateColorCodes('&', "${Chat.prefix} &7Progress: ${Chat.primaryColor}${rounded}% &8| &7World: &8'${Chat.primaryColor}${Config.fillTask.refWorld()}&8'"))
                        }
                    } else {
                        cancel()
                        event.fillTask.cancel()
                        Bukkit.broadcastMessage(Chat.colored("${prefix} Cancelled pregen because no world was set."))
                    }
                } else {
                    cancel()
                }
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0, 1)
    }

    @EventHandler
    fun on(event: WorldBorderFillFinishedEvent) {
        Bukkit.broadcastMessage(Chat.colored("${prefix} Pregeneration in world '${Chat.primaryColor}${event.world.name}&7' finished."))
        Bukkit.broadcastMessage(Chat.colored("${prefix} Please wait for TPS to stabilize at ${Chat.primaryColor}20 &7before restarting."))
    }
}