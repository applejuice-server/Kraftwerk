package pink.mino.kraftwerk.features

import com.wimbli.WorldBorder.Config
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.utils.ActionBar
import pink.mino.kraftwerk.utils.Chat
import kotlin.math.roundToInt

class PregenActionBarFeature : BukkitRunnable() {
    override fun run() {
        if (Config.fillTask.valid()) {
            val players = Bukkit.getServer().onlinePlayers
            val rounded = (Config.fillTask.percentageCompleted * 100.0).roundToInt() / 100.0
            for (player in players) {
                ActionBar.sendActionBarMessage(player, ChatColor.translateAlternateColorCodes('&', "${Chat.prefix} &7Progress: &c${rounded}% &8| &7World: &8'&c${Config.fillTask.refWorld()}&8'"))
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "${Chat.dash} &7Pregeneration is now finished."))
            cancel()
        }
    }
}