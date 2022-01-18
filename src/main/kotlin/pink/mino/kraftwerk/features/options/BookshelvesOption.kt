package pink.mino.kraftwerk.features.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

class BookshelvesOption : ConfigOption(
    "Bookshelves",
    "Bookshelves drop books.",
    "options",
    "bookshelves",
    Material.BOOKSHELF
) {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (enabled) {
            return
        }
        if (e.block.type === Material.BOOKSHELF) {
            e.isCancelled = true
            e.block.type = Material.AIR
        }
    }
}