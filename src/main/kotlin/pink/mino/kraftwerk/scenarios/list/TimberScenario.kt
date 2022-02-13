package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import pink.mino.kraftwerk.scenarios.Scenario
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
        handleBreak(event.block, 0)
    }

    private fun handleBreak(tree: Block?, broken: Int) {
        if (broken > 20) return
        if (tree == null || tree.type === Material.AIR) return
        if (tree.type !== Material.LOG && tree.type !== Material.LOG_2) return
        tree.breakNaturally()
        for (face in BlockFace.values()) {
            handleBreak(tree.getRelative(face), broken + 1)
        }
    }
}