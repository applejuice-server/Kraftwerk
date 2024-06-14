package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*

class AssaultAndBatteryScenario : Scenario(
    "Assault and Battery",
    "1 player can do damage with melee and the other can do damage with ranged weapons. If your teammate dies, then you will be able to use melee and ranged weapons.",
    "assaultandbattery",
    Material.IRON_SWORD
) {
    val melee = arrayListOf<UUID>()
    val ranged = arrayListOf<UUID>()
    val prefix = "&8[${Chat.primaryColor}Assault and Battery&8]&7"

    override fun onStart() {
        for (team in TeamsFeature.manager.getTeams()) {
            for ((index, member) in team.players.withIndex().shuffled()) {
                if (index == 0) {
                    melee.add(member.uniqueId)
                    Chat.sendMessage(member as Player, "${prefix} You are the Assault player on your team, you may only attack using melee weapons.")
                } else {
                    ranged.add(member.uniqueId)
                    Chat.sendMessage(member as Player, "${prefix} You are the Battery player on your team, you may only attack using ranged weapons.")
                }
            }
        }
    }

    override fun givePlayer(player: Player) {
        if (TeamsFeature.manager.getTeam(player) != null) {
            val team = TeamsFeature.manager.getTeam(player)
            for (member in team!!.players) {
                ranged.remove(member.uniqueId)
                melee.remove(member.uniqueId)
            }
            for ((index, member) in team.players.withIndex().shuffled()) {
                if (index == 0) {
                    melee.add(member.uniqueId)
                    if (member.isOnline) Chat.sendMessage(member as Player, "${prefix} You are the Assault player on your team, you may only attack using melee weapons.")
                } else {
                    ranged.add(member.uniqueId)
                    if (member.isOnline) Chat.sendMessage(member as Player, "${prefix} You are the Battery player on your team, you may only attack using ranged weapons.")
                }
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (TeamsFeature.manager.getTeam(e.entity) != null) {
            val team = TeamsFeature.manager.getTeam(e.entity)
            TeamsFeature.manager.deleteTeam(team!!)
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity is Player && e.damager is Player) {
            val player = e.entity as Player
            val damager = e.damager as Player
            if (!melee.contains(damager.uniqueId) && TeamsFeature.manager.getTeam(damager) != null) {
                e.isCancelled = true
                Chat.sendMessage(damager, "${prefix} You are not the Assault player on your team, you may only attack using melee weapons.")
                return
            }
        } else if (e.entity is Player && e.damager is org.bukkit.entity.Projectile) {
            val player = e.entity as Player
            val damager = (e.damager as org.bukkit.entity.Projectile).shooter as Player
            if (!ranged.contains(damager.uniqueId) && TeamsFeature.manager.getTeam(damager) != null) {
                e.isCancelled = true
                Chat.sendMessage(damager, "${prefix} You are not the Battery player on your team, you may only attack using ranged weapons.")
                return
            }
        }
    }
}