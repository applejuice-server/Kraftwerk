package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState


class BareBonesScenario : Scenario(
    "Bare Bones",
    "Iron is the highest tier you can obtain through gearing up. When a player dies, they will drop 1 diamond, 1 golden apple, 32 arrows, and 2 string. You cannot craft an enchantment table, anvil, or golden apple.",
    "barebones",
    Material.BONE
) {
    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        val diamond = ItemStack(Material.DIAMOND, 1)
        val gapple = ItemStack(Material.GOLDEN_APPLE)
        val arrows = ItemStack(Material.ARROW, 32)
        val string = ItemStack(Material.STRING, 2)
        e.drops.add(diamond)
        e.drops.add(gapple)
        e.drops.add(arrows)
        e.drops.add(string)
    }


    @EventHandler
    fun onCraft(e: PrepareItemCraftEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.recipe.result.type == Material.ENCHANTMENT_TABLE) {
            e.inventory.result = ItemStack(Material.AIR)
        }
        if (e.recipe.result.type == Material.ANVIL) {
            e.inventory.result = ItemStack(Material.AIR)
        }
        if (e.recipe.result.type == Material.GOLDEN_APPLE) {
            e.inventory.result = ItemStack(Material.AIR)
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        when (e.block.type) {
            Material.EMERALD_ORE -> {
                e.isCancelled = true
                e.block.type = Material.AIR
                val stack = ItemStack(Material.IRON_INGOT)
                e.player.world.dropItemNaturally(e.block.location, stack)
            }
            Material.DIAMOND_ORE -> {
                e.isCancelled = true
                e.block.type = Material.AIR
                val stack = ItemStack(Material.IRON_INGOT)
                e.player.world.dropItemNaturally(e.block.location, stack)
            }
            Material.GOLD_ORE -> {
                e.isCancelled = true
                e.block.type = Material.AIR
                val stack = ItemStack(Material.IRON_INGOT)
                e.player.world.dropItemNaturally(e.block.location, stack)
            }
            Material.ANVIL -> {
                e.isCancelled = true
            }
            else -> {}
        }
    }
}