package pink.mino.kraftwerk.scenarios.list

import me.lucko.helper.Schedulers
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*

class GappleRouletteScenario : Scenario(
    "Gapple Roulette",
    "Whenever you eat a Golden Apple, you will get a random potion effect for a certain time.",
    "gappleroulette",
    Material.GOLDEN_APPLE
) {
    val potionEffects = listOf<PotionEffectType>(
        PotionEffectType.BLINDNESS,
        PotionEffectType.CONFUSION,
        PotionEffectType.DAMAGE_RESISTANCE,
        PotionEffectType.FAST_DIGGING,
        PotionEffectType.FIRE_RESISTANCE,
        PotionEffectType.HUNGER,
        PotionEffectType.INCREASE_DAMAGE,
        PotionEffectType.INVISIBILITY,
        PotionEffectType.JUMP,
        PotionEffectType.NIGHT_VISION,
        PotionEffectType.POISON,
        PotionEffectType.REGENERATION,
        PotionEffectType.SATURATION,
        PotionEffectType.SLOW,
        PotionEffectType.SLOW_DIGGING,
        PotionEffectType.SPEED,
        PotionEffectType.WATER_BREATHING,
        PotionEffectType.WEAKNESS,
        PotionEffectType.WITHER
    )

    @EventHandler
    fun onPlayerConsume(e: PlayerItemConsumeEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.item.type == Material.GOLDEN_APPLE) {
            Schedulers.sync().runLater ({
                val effect = potionEffects.random()
                val seconds = Random().nextInt(90)
                e.player.removePotionEffect(PotionEffectType.ABSORPTION)
                e.player.removePotionEffect(PotionEffectType.REGENERATION)
                e.player.addPotionEffect(PotionEffect(effect, 20 * seconds, 0))
                Chat.sendMessage(e.player, "${Chat.dash} You got &f${effect.name}&7 for &f${seconds} seconds&7!")
            }, 1L)
        }
    }
}