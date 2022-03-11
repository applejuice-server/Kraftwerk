package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class FastGetawayScenario : Scenario(
    "Fast Getaway",
    "Players receive speed 2 for 45 seconds when they get a kill.",
    "fastgetaway",
    Material.FEATHER
) {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity.killer != null) {
            e.entity.killer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 45 * 20, 2, true, false))
        }
    }
}