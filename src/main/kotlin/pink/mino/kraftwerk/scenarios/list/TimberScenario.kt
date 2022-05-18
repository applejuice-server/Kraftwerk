package pink.mino.kraftwerk.scenarios.list

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
        handleBreak(event.block, 0, event.player)
    }

    private fun handleBreak(tree: Block?, broken: Int, player: Player) {
        if (broken > 30) return
        if (tree == null || tree.type === Material.AIR) return
        if (tree.type !== Material.LOG && tree.type !== Material.LOG_2) return
        tree.breakNaturally()
        val loc = tree.location
        loc.world.playSound(loc, Sound.DIG_WOOD, 0.5f, 1f)
        BlockUtil().degradeDurability(player)
        for (x in loc.blockX - 1..loc.blockX + 1) {
            for (y in loc.blockY - 1..loc.blockY + 1) {
                for (z in loc.blockZ - 1..loc.blockZ + 1) {
                    val block = loc.world.getBlockAt(x, y, z)
                    handleBreak(block, broken + 1, player)
                }
            }
        }
    }
}