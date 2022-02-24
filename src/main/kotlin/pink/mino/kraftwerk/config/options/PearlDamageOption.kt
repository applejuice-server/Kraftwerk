package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import pink.mino.kraftwerk.config.ConfigOption


class PearlDamageOption : ConfigOption(
    "Pearl Damage",
    "Toggles whether you are damaged when throwing an ender pearl.",
    "options",
    "pearldamage",
    Material.ENDER_PEARL
) {

    @EventHandler(ignoreCancelled = true)
    fun onEnderPearlDamage(event: EntityDamageByEntityEvent) {
        if (enabled) return
        if (event.entity !is Player) return
        if (event.damager.type != EntityType.ENDER_PEARL) return
        event.damage = 0.0
        event.isCancelled = true
    }
}