package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState
import kotlin.random.Random

class BatsScenario : Scenario(
    "Bats",
    "Bats drop golden apples when they die, however there's a 5% chance of killing the player.",
    "bats",
    Material.GOLDEN_APPLE
) {
    @EventHandler
    fun onBatDeath(e: EntityDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity.killer != null) {
            e.entity.world.dropItemNaturally(e.entity.location, ItemStack(Material.GOLDEN_APPLE))
            val odds = Random.nextInt(100)
            if (odds >= 95) e.entity.killer.damage(9999.9)
        }
    }
}