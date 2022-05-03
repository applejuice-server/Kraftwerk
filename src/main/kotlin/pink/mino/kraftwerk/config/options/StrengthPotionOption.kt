package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.BrewEvent
import pink.mino.kraftwerk.config.ConfigOption

class StrengthPotionOption : ConfigOption(
    "Strength Potions",
    "Toggles whether Strength potions can be brewed.",
    "nether",
    "strengthpotions",
    Material.BLAZE_POWDER
) {
    @EventHandler
    fun onBrewEvent(e: BrewEvent) {
        if (enabled) return
        if (e.contents.ingredient.type == Material.BLAZE_POWDER) {
            e.isCancelled = true
        }
    }
}