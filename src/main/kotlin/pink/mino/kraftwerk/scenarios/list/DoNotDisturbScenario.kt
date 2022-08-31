package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*


class DoNotDisturbScenario : Scenario(
    "Do Not Disturb",
    "If you hit a player, it will lock you with that player, or team for 15 seconds. Every time you hit that player or team, the timer will refresh until the 15 seconds are up and can be attacked by another player or team.",
    "donotdisturb",
    Material.BARRIER
) {
    private val hashMap: MutableMap<UUID, UUID> = HashMap()

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (event.entity !is Player) return
        if (event.damager !is Player) return
        val player = event.entity as Player
        val damager = event.damager as Player
        if (hashMap.containsKey(damager.uniqueId)) {
            if (player.uniqueId != hashMap[damager.uniqueId]) {
                event.isCancelled = true
                damager.sendMessage(Chat.colored("&cYou can't hit that player, he isn't linked to you!"))
                return
            }
        } else {
            hashMap[damager.uniqueId] = player.uniqueId
            hashMap[player.uniqueId] = damager.uniqueId
            object : BukkitRunnable() {
                override fun run() {
                    hashMap.remove(player.uniqueId)
                    hashMap.remove(damager.uniqueId)
                    if (player != null) {
                        player.sendMessage(Chat.colored("&cYour Do Not Disturb has expired."))
                    }
                    if (damager != null) {
                        damager.sendMessage(Chat.colored("&cYour Do Not Disturb has expired."))
                    }
                }
            }.runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), 15 * 20L)
        }
    }
}