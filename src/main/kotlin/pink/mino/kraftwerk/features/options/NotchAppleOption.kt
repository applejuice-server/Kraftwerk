package pink.mino.kraftwerk.features.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack

class NotchAppleOption : ConfigOption(
    "Notch Apples",
    "Toggles the Notch Apple recipe.",
    "options",
    "notchapples",
    Material.GOLD_BLOCK
) {
    @EventHandler
    fun onPlayerCraft(e: CraftItemEvent) {
        if (enabled) {
            return
        }
        val item: ItemStack? = e.currentItem
        if (item != null) {
            e.isCancelled = item == ItemStack(Material.GOLDEN_APPLE, 1, 1.toShort())
        }
    }
}