package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import pink.mino.kraftwerk.utils.Chat

class ServerListPing : Listener {
    @EventHandler
    fun onServerListPing(e: ServerListPingEvent) {
        val text = Chat.centerMotd("&fprototype &7uhc\n&7in development, check back again soon for more news!")
        e.motd = text
    }
}