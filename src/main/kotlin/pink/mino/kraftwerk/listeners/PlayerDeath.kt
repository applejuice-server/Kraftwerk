package pink.mino.kraftwerk.listeners

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeath : Listener {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val player = e.entity as Player
        player.spigot().respawn()
        val old = e.deathMessage
        e.deathMessage = ChatColor.translateAlternateColorCodes('&', "&8»&f $old &8«")
    }
}