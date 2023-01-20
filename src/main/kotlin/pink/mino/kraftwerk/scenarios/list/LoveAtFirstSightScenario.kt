package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils

class LoveAtFirstSightScenario : Scenario(
    "Love at First Sight",
    "Players are scattered as solos, the first player they hit at PvP will be teamed up together.",
    "lafs",
    Material.APPLE
) {
    val prefix = "&8[&cLAFS&8]&7"
    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.isCancelled) return
        if (e.entity is Player && e.damager is Player) {
            val player = e.entity as Player
            val damager = e.damager as Player
            if (player != damager) {
                if (TeamsFeature.manager.getTeam(player) == null && TeamsFeature.manager.getTeam(damager) == null) {
                    e.damage = 0.0
                    val team = TeamsFeature.manager.createTeam(player)
                    TeamsFeature.manager.joinTeam(team.name, player)
                    Chat.sendMessage(player, "$prefix You are now teamed with &f${PlayerUtils.getPrefix(damager)}${damager.name}&7!")
                    Chat.sendMessage(damager, "$prefix You are now teamed with &f${PlayerUtils.getPrefix(player)}${player.name}&7!")
                    player.playSound(player.location, Sound.LEVEL_UP, 1f, 1f)
                    damager.playSound(damager.location, Sound.LEVEL_UP, 1f, 1f)
                }
            }
        }
    }
}