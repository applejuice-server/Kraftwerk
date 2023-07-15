package pink.mino.kraftwerk.utils

import org.bukkit.entity.Player

enum class Perk {
    TEAM_COLORS,
    EMOTES,
    TOGGLE_PICKUPS,
    RIDE_PLAYERS,
    BODY_SPEC,
    STATS_RESET,
    SPAWN_FLY,
    WHITE_CHAT,
    NO_CHAT_DELAY,
    CHOOSE_ARENA_BLOCKS,
    BYPASS_DEATH_KICK
}

class PerkChecker {
    companion object {
        fun checkPerks(player: Player) : ArrayList<Perk> {
            val list: ArrayList<Perk> = arrayListOf()
            if (player.hasPermission("uhc.donator.teamColors")) {
                list.add(Perk.TEAM_COLORS)
            }
            if (player.hasPermission("uhc.donator.noChatDelay")) {
                list.add(Perk.NO_CHAT_DELAY)
            }
            if (player.hasPermission("uhc.donator.chooseArenaBlocks")) {
                list.add(Perk.CHOOSE_ARENA_BLOCKS)
            }
            if (player.hasPermission("uhc.donator.whiteChat")) {
                list.add(Perk.WHITE_CHAT)
            }
            if (player.hasPermission("uhc.donator.emotes")) {
                list.add(Perk.EMOTES)
            }
            if (player.hasPermission("uhc.donator.togglePickups")) {
                list.add(Perk.TOGGLE_PICKUPS)
            }
            if (player.hasPermission("uhc.donator.ridePlayers")) {
                list.add(Perk.RIDE_PLAYERS)
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
            if (player.hasPermission("uhc.donator.bypassDeathKick")) {
                list.add(Perk.BYPASS_DEATH_KICK)
            }
            return list
        }

        fun checkPerk(player: Player, perk: String) : Boolean {
            return player.hasPermission("uhc.donator.$perk")
        }
    }
}