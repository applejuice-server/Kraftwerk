package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.UUID

class EnemyReconScenario : Scenario(
    "Enemy Recon",
    "You can do /er <player> to see their inventory and some basic information on them. You get one 'recon' for every kill you get. There's a 30% chance that you get caught spying on someone, as a message is broadcasted in chat.",
    "enemyrecon",
    Material.EYE_OF_ENDER
) {
    companion object {
        val instance = EnemyReconScenario()
    }

    val prefix = "&8[&cEnemy Recon&8]&7"
    var recons: HashMap<UUID, Int> = hashMapOf()

    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            recons[player.uniqueId] = 0
        }
    }

    override fun givePlayer(player: Player) {
        recons[player.uniqueId] = 0
    }
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity.killer == null) return
        if (e.entity.killer.type == EntityType.PLAYER) {
            val killer = e.entity.killer as Player
            if (recons[killer.uniqueId] == null) {
                recons[killer.uniqueId] = 1
            } else {
                recons[killer.uniqueId] = recons[killer.uniqueId]!! + 1
            }
            Chat.sendMessage(killer, "$prefix You have gained an extra recon! (Recons: &f${recons[killer.uniqueId]}&7)")
        } else if (e.entity.killer.type == EntityType.ARROW && (e.entity as Arrow).shooter is Player) {
            val killer = (e.entity as Arrow).shooter as Player
            if (recons[killer.uniqueId] == null) {
                recons[killer.uniqueId] = 1
            } else {
                recons[killer.uniqueId] = recons[killer.uniqueId]!! + 1
            }
            Chat.sendMessage(killer, "$prefix You have gained an extra recon! (Recons: &f${recons[killer.uniqueId]})")
        } else {
            return
        }
    }
}