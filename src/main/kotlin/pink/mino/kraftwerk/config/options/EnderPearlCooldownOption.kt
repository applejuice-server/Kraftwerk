package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import pink.mino.kraftwerk.config.ConfigOption
import pink.mino.kraftwerk.utils.Chat

class EnderPearlCooldownOption : ConfigOption(
    "Pearl Cooldown",
    "When toggles, introduces a cooldown to whether you can throw an enderpearl.",
    "options",
    "pearlcooldown",
    Material.EYE_OF_ENDER
) {
    var cooldowns = HashMap<String, Long>()
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (!enabled) return
        if (e.item != null && e.item.type == Material.ENDER_PEARL) {
            if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
                val cooldownTime = 10
                if (cooldowns.containsKey(e.player.name)) {
                    val secondsLeft: Long = cooldowns[e.player.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                    if (secondsLeft > 0) {
                        Chat.sendMessage(e.player, "&cYou are on pearl cooldown for $secondsLeft second(s)!")
                        e.isCancelled = true
                        return
                    }
                }
                cooldowns[e.player.name] = System.currentTimeMillis()
                return
            }
        }
    }
}