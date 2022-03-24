package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.BrewEvent
import pink.mino.kraftwerk.config.ConfigOption

class SplashPotionsOption : ConfigOption(
    "Splash Potions",
    "Toggles whether splash potions can be crafted.",
    "nether",
    "splashPotions",
    Material.SULPHUR
) {
    @EventHandler
    fun onBrewEvent(e: BrewEvent) {
        if (enabled) return
        if (e.contents.ingredient.type == Material.SULPHUR) {
            e.isCancelled = true
        }
    }
}