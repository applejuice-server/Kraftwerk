package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.PrepareItemCraftEvent
import pink.mino.kraftwerk.config.ConfigOption

class DoubleArrowsOption : ConfigOption(
    "Double Arrows",
    "Get 2x arrows on craft",
    "options",
    "doublearrows",
    Material.ARROW
) {

    @EventHandler
    fun onPrepareCraft(event: PrepareItemCraftEvent) {
        if (!enabled) return
        if (event.inventory.result == null) return
        if (event.inventory.result.type != Material.ARROW) return
        event.inventory.result.amount = event.inventory.result.amount * 2
    }

}