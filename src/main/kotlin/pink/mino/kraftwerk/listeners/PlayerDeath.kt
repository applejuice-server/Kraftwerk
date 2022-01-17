package pink.mino.kraftwerk.listeners
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk


class PlayerDeath : Listener {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val player = e.entity as Player
        val old = e.deathMessage
        player.world.strikeLightningEffect(player.location)
        e.deathMessage = ChatColor.translateAlternateColorCodes('&', "&8»&f $old &8«")
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            player.spigot().respawn()
        }, 1L)
    }
}