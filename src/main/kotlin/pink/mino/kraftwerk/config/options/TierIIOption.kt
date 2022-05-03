package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.BrewEvent
import pink.mino.kraftwerk.config.ConfigOption

class TierIIOption : ConfigOption(
    "Tier II",
    "Toggles whether Tier II potions can be crafted.",
    "nether",
    "tierii",
    Material.GLOWSTONE
) {
    @EventHandler
    fun onBrewEvent(e: BrewEvent) {
        if (enabled) return
        if (e.contents.ingredient.type == Material.GLOWSTONE_DUST) {
            e.isCancelled = true
        }
    }
}