package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import pink.mino.kraftwerk.utils.Chat

class ServerListPingListener : Listener {
    @EventHandler
    fun onServerListPing(e: ServerListPingEvent) {
        val text = Chat.colored("&c&lapple&a&ljuice&7&l.games &8(&a&a1.8&8) &8(&aNA&8)\n&oReddit UHC Server")
        e.motd = text
    }
}