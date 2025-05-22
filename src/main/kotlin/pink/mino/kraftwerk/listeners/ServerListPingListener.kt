package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat

class ServerListPingListener : Listener {
    @EventHandler
    fun onServerListPing(e: ServerListPingEvent) {
        val text = Chat.colored(if (ConfigFeature.instance.config!!.getString("chat.motd") != null) ConfigFeature.instance.config!!.getString("chat.motd") else "No MOTD set in Config tough tits :3 hi marcus")
        e.motd = text
    }
}