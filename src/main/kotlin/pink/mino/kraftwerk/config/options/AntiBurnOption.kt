package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import pink.mino.kraftwerk.config.ConfigOption

class AntiBurnOption : ConfigOption(
    "AntiBurn",
    "Prevents items from burning.",
    "options",
    "antiburn",
    Material.FLINT_AND_STEEL
) {
    @EventHandler
    fun onItemDeath(e: EntityDamageEvent) {
        if (!enabled) {
            return
        }
        val entity = e.entity
        if (entity.type === EntityType.DROPPED_ITEM) {
            e.isCancelled = true
        }
    }
}