package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ItemSpawnEvent
import pink.mino.kraftwerk.config.ConfigOption

class AntiStoneOption : ConfigOption(
    "Anti-Stone",
    "All stone blocks get turned to cobblestone.",
    "options",
    "antistone",
    Material.COBBLESTONE
) {
    @EventHandler
    fun onItemDrop(e: ItemSpawnEvent) {
        if (!enabled) {
            return
        }
        if (e.entity.itemStack.type == Material.STONE) {
            e.entity.itemStack.type = Material.COBBLESTONE
        }
    }
}