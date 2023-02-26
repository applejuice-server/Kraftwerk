package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import pink.mino.kraftwerk.config.ConfigOption

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
        }
    }
}