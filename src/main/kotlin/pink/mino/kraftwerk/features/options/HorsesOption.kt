package pink.mino.kraftwerk.features.options

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.spigotmc.event.entity.EntityMountEvent
import pink.mino.kraftwerk.utils.Chat

class HorsesOption : ConfigOption(
    "Horses",
    "Toggles horse riding.",
    "options",
    "horses",
    Material.SADDLE
) {
    @EventHandler
    fun onPlayerMount(e: EntityMountEvent) {
        if (enabled) {
            return
        }
        if (e.entityType === EntityType.PLAYER) {
            val player = e.entity as Player
            if (e.mount.type === EntityType.HORSE) {
                e.isCancelled = true
                Chat.sendMessage(player, "&cHorse riding is disabled.")
            }
        }
    }

    override fun onToggle(to: Boolean) {
        if (to) {
            return
        }
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.vehicle !== null && player.vehicle.type === EntityType.HORSE) {
                player.vehicle.eject()
                Chat.sendMessage(player, "&cHorse riding has now been disabled by the host.")
            }
        }
    }
}