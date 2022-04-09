package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class ServerListPingListener : Listener {
    @EventHandler
    fun onServerListPing(e: ServerListPingEvent) {
        val text = Chat.colored("&c&lapple&a&ljuice&7&l.bar &8(&a1.8.x&8) &8(&a${SettingsFeature.instance.data!!.getString("server.region")}&8)\n&oReddit UHC & PR server")
        e.motd = text
    }
}