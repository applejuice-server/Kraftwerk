package pink.mino.kraftwerk.listeners

import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class PvPListener : Listener {
    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (e.entityType == EntityType.PLAYER) {
            if (SettingsFeature.instance.data!!.getBoolean("game.pvp")) {
                e.isCancelled = true
                Chat.sendMessage(e.damager as Player, "&cPvP is disabled at the moment.")
            }
        }
    }
}