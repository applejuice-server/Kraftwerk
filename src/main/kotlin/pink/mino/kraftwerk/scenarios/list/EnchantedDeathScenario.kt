package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class EnchantedDeathScenario : Scenario(
    "Enchanted Death",
    "Enchantment Tables are disabled, the only way to acquire one is by killing someone.",
    "enchanteddeath",
    Material.ENCHANTMENT_TABLE
) {
    @EventHandler
    fun onPlayerCraft(e: PrepareItemCraftEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.inventory.result.type == Material.ENCHANTMENT_TABLE) {
            e.inventory.result.type = Material.AIR
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        e.drops.add(ItemStack(Material.ENCHANTMENT_TABLE))
    }
}