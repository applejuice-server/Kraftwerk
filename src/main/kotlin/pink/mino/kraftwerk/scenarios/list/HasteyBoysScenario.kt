package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.PrepareItemCraftEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState


class HasteyBoysScenario : Scenario(
    "HasteyBoys",
    "All tools are enchanted with efficiency 3 & unbreaking 1.",
    "hasteyboys",
    Material.STONE_PICKAXE
) {

    private var types: List<Material> = ArrayList(
        listOf(
            Material.WOOD_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLD_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.WOOD_SPADE,
            Material.GOLD_SPADE,
            Material.IRON_SPADE,
            Material.STONE_SPADE,
            Material.DIAMOND_SPADE,
            Material.WOOD_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLD_AXE,
            Material.DIAMOND_AXE
        )
    )

    @EventHandler
    fun onCraft(e: PrepareItemCraftEvent) {
        if (!enabled) return
        if (GameState.currentState !== GameState.INGAME) return
        if (types.contains(e.recipe.result.type)) {
            val item = e.recipe.result
            item.addEnchantment(Enchantment.DIG_SPEED, 3)
            item.addEnchantment(Enchantment.DURABILITY, 1)
            e.inventory.result = item
        }
    }
}