package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class TripleOresScenario : Scenario(
    "Triple Ores",
    "Ores drop tripple of what they're supposed to drop.",
    "tripleores",
    Material.GOLD_ORE
) {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        when (e.block.type) {
            Material.GOLD_ORE -> {
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.GOLD_ORE))
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.GOLD_ORE))
            }
            Material.DIAMOND_ORE -> {
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.DIAMOND))
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.DIAMOND))
            }
            Material.IRON_ORE -> {
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.IRON_ORE))
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.IRON_ORE))
            }
            Material.EMERALD_ORE -> {
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.EMERALD))
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.EMERALD))
            }
            Material.COAL_ORE -> {
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.COAL))
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.COAL))
            }
            else -> {}
        }
    }
}