package pink.mino.kraftwerk.utils

import org.bukkit.entity.Player

enum class Perk {
    TEAM_COLORS,
    EMOTES,
    TOGGLE_PICKUPS,
    PROJECTILE_PARTICLES,
    RIDE_PLAYERS,
    MOB_EGGS,
    BODY_SPEC,
    STATS_RESET,
    SPAWN_FLY
}

class PerkChecker {
    companion object {
        fun checkPerks(player: Player) : ArrayList<Perk> {
            val list: ArrayList<Perk> = arrayListOf()
            if (player.hasPermission("uhc.donator.teamColors")) {
                list.add(Perk.TEAM_COLORS)
            }
            if (player.hasPermission("uhc.donator.emotes")) {
                list.add(Perk.EMOTES)
            }
            if (player.hasPermission("uhc.donator.togglePickups")) {
                list.add(Perk.TOGGLE_PICKUPS)
            }
            if (player.hasPermission("uhc.donator.projectileParticles")) {
                list.add(Perk.PROJECTILE_PARTICLES)
            }
            if (player.hasPermission("uhc.donator.ridePlayers")) {
                list.add(Perk.RIDE_PLAYERS)
            }
            if (player.hasPermission("uhc.donator.mobEggs")) {
                list.add(Perk.MOB_EGGS)
            }
            if (player.hasPermission("uhc.donator.bodySpec")) {
                list.add(Perk.BODY_SPEC)
            }
            if (player.hasPermission("uhc.donator.statsReset")) {
                list.add(Perk.STATS_RESET)
            }
            if (player.hasPermission("uhc.donator.spawnFly")) {
                list.add(Perk.SPAWN_FLY)
            }
            return list
        }

        fun checkPerk(player: Player, perk: String) : Boolean {
            return player.hasPermission("uhc.donator.$perk")
        }
    }
}