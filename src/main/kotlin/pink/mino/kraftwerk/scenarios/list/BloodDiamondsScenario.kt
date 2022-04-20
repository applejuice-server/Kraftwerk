package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class BloodDiamondsScenario : Scenario(
    "Blood Diamonds",
    "Mining diamonds deal half a heart of damage.",
    "blooddiamonds",
    Material.DIAMOND_ORE
) {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.isCancelled) return
        if (e.block.type == Material.DIAMOND_ORE) e.player.damage(1.0)
    }
}