package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import pink.mino.kraftwerk.utils.Chat

class PlayerJoin : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        Chat.sendCenteredMessage(player, "&8&m------------------------")
        Chat.sendCenteredMessage(player, "")
        Chat.sendCenteredMessage(player, "&fWelcome to prototype UHC!")
        Chat.sendCenteredMessage(player, "&fWe hope you have fun!")
        Chat.sendCenteredMessage(player, "")
        Chat.sendCenteredMessage(player, "&8&m------------------------")
    }
}