package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import pink.mino.kraftwerk.config.ConfigOption

class NerfedQuartzOption : ConfigOption(
    "Nerfed Quartz",
    "Halves the XP of Quartz.",
    "nether",
    "nerfedQuartz",
    Material.QUARTZ_ORE
) {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (e.block.type != Material.QUARTZ_ORE) return
        e.expToDrop = e.expToDrop / 2
    }
}