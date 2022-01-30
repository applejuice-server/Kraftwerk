package pink.mino.kraftwerk.features.options

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

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