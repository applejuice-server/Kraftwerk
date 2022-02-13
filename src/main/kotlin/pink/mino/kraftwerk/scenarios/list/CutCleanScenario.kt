package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.ExperienceOrb
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ItemSpawnEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class CutCleanScenario : Scenario(
    "CutClean",
    "Ores/food drop smelted.",
    "cutclean",
    Material.BLAZE_POWDER
) {
    @EventHandler
    fun onItemSpawn(e: ItemSpawnEvent) {
        if (GameState.currentState == GameState.LOBBY) return
        if (!enabled) return
        when (e.entity.itemStack.type) {
            Material.IRON_ORE -> {
                e.entity.itemStack.type = Material.IRON_INGOT
            }
            Material.GOLD_ORE -> {
                e.entity.itemStack.type = Material.GOLD_INGOT
            }
            Material.RAW_CHICKEN -> {
                e.entity.itemStack.type = Material.COOKED_CHICKEN
            }
            Material.RAW_BEEF -> {
                e.entity.itemStack.type = Material.COOKED_BEEF
            }
            Material.MUTTON -> {
                e.entity.itemStack.type = Material.COOKED_MUTTON
            }
            Material.PORK -> {
                e.entity.itemStack.type = Material.GRILLED_PORK
            }
            else -> {}
        }
        (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 5
    }
}