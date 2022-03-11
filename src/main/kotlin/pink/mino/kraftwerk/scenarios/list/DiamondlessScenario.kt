package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class DiamondlessScenario : Scenario(
    "Diamondless",
    "Diamonds are unobtainable except by killing players.",
    "diamondless",
    Material.DIAMOND
) {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        e.entity.world.dropItemNaturally(e.entity.location, ItemStack(Material.DIAMOND))
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.block.type == Material.DIAMOND_ORE) {
            e.isCancelled = true
            e.block.type = Material.AIR
        }
    }
}