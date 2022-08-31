package pink.mino.kraftwerk.listeners
import net.citizensnpcs.api.CitizensAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.CombatLogFeature
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils
import pink.mino.kraftwerk.utils.Scoreboard


class PlayerDeathListener : Listener {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!e.entity.hasMetadata("NPC")) {
            val player = e.entity as Player
            val old = e.deathMessage
            player.world.strikeLightningEffect(player.location)
            e.deathMessage = ChatColor.translateAlternateColorCodes('&', "&8»&f $old")
            if (player.world.name == "Arena") {
                e.deathMessage = null
            }
            if (GameState.currentState == GameState.INGAME) {
                val killer = e.entity.killer
                if (killer != null) {
                    val o = SettingsFeature.instance.data!!.getInt("game.kills.${killer.name}")
                    SettingsFeature.instance.data!!.set("game.kills.${killer.name}", o + 1)
                    val color: String = if (TeamsFeature.manager.getTeam(killer) != null) {
                        TeamsFeature.manager.getTeam(killer)!!.prefix
                    } else {
                        "&f"
                    }
                    Scoreboard.setScore(Chat.colored("${Chat.dash} ${color}${killer.name}"), o + 1)
                }
                val list = SettingsFeature.instance.data!!.getStringList("game.list")
                list.remove(player.name)
                SettingsFeature.instance.data!!.set("game.list", list)
                SettingsFeature.instance.saveData()
                val kills = SettingsFeature.instance.data!!.getInt("game.kills.${player.name}")
                val color: String = if (TeamsFeature.manager.getTeam(player) != null) {
                    TeamsFeature.manager.getTeam(player)!!.prefix
                } else {
                    "&f"
                }
                Scoreboard.deleteScore(Chat.colored("${Chat.dash} ${color}${player.name}"))
                if (kills > 0) Scoreboard.setScore(Chat.colored("${Chat.dash} ${color}&m${player.name}"), kills)
                Scoreboard.setScore(Chat.colored("${Chat.dash} &7Playing..."), Math.max(PlayerUtils.getPlayingPlayers().size - 1, 0))
                CombatLogFeature.instance.removeCombatLog(player.name)
                if (!player.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl remove ${player.name}")
                }
            }
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                player.spigot().respawn()
            }, 20L)
        } else {
            if (GameState.currentState == GameState.INGAME) {
                e.entity.world.strikeLightningEffect(e.entity.location)
                val player = e.entity
                val killer = e.entity.killer
                val npc = CitizensAPI.getNPCRegistry().getNPC(e.entity)
                e.deathMessage =
                    ChatColor.translateAlternateColorCodes('&', "&8»&f ${killer.name} has killed ${npc.name}")
                if (killer != null) {
                    val o = SettingsFeature.instance.data!!.getInt("game.kills.${killer.name}")
                    SettingsFeature.instance.data!!.set("game.kills.${killer.name}", o + 1)
                    val color: String = if (TeamsFeature.manager.getTeam(killer) != null) {
                        TeamsFeature.manager.getTeam(killer)!!.prefix
                    } else {
                        "&f"
                    }
                    Scoreboard.setScore(Chat.colored("${Chat.dash} ${color}${killer.name}"), o + 1)
                }
                val list = SettingsFeature.instance.data!!.getStringList("game.list")
                list.remove(player.name)
                SettingsFeature.instance.data!!.set("game.list", list)
                SettingsFeature.instance.saveData()
            }
        }
    }
}