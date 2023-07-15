package pink.mino.kraftwerk.listeners.lunar

import com.lunarclient.bukkitapi.LunarClientAPI
import com.lunarclient.bukkitapi.event.LCPlayerRegisterEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerRegisterListener : Listener {
    @EventHandler
    fun onPlayerRegister(e: LCPlayerRegisterEvent) {
        val player = e.player
        LunarClientAPI.getInstance().registerPlayer(player)
        //Chat.sendMessage(player, "${Chat.dash}&7 You're playing on &bLunar Client&7!")
        if (LunarClientAPI.getInstance().isRunningLunarClient(player)) {
            Bukkit.getLogger().info("${player.name} is running Lunar Client.")
        }
    }
}