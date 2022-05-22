package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.BlockUtil
import pink.mino.kraftwerk.utils.GameState


class TimberScenario : Scenario(
    "Timber",
    "Breaking one part of a tree breaks the entire tree.",
    "timber",
    Material.LOG
) {
    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        if (!enabled) return
        if (event.isCancelled) return
        if (GameState.currentState !== GameState.INGAME) return
        if (event.block.type != Material.LOG && event.block.type != Material.LOG_2) return
        timberTree(event.block.location, event.block.type, event.player)
    }

    private fun timberTree(loc: Location, material: Material, player: Player) {
        for (x in loc.blockX - 1..loc.blockX + 1) {
            for (y in loc.blockY - 1..loc.blockY + 1) {
                for (z in loc.blockZ - 1..loc.blockZ + 1) {
                    val newLoc = Location(loc.world, x.toDouble(), y.toDouble(), z.toDouble())
                    if (loc.world.getBlockAt(x, y, z).type == material) {
                        loc.world.getBlockAt(x, y, z).breakNaturally()
                        loc.world.playSound(newLoc, Sound.DIG_WOOD, 1f, 1f)
                        BlockUtil().degradeDurability(player)
                        timberTree(newLoc, material, player)
                    }
                }
            }
        }
    }
}