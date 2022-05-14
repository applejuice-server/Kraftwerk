package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class FFAScatterTask(
    val players: ArrayList<Player>,
    val scatterList: HashMap<Player, Location>,
) : BukkitRunnable() {
    var i = 0
    override fun run() {
        if (i == players.size) {
            cancel()
            return
        }
        if (!SpecFeature.instance.getSpecs().contains(players[i].name)) {
            if (players[i].isOnline) {
                players[i].teleport(scatterList[players[i]])
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scattering &c${players[i].name}&8 (&c${i + 1}&8/&c${players.size}&8)"))
            }
        }
        i++
    }
}

class TeamScatterTask(
    val players: ArrayList<Player>,
    val solosList: HashMap<Player, Location>,
    val teamsList: HashMap<Team, Location>
) : BukkitRunnable() {
    var i = 0
    override fun run() {
        if (i == players.size) {
            cancel()
            return
        }
        if (!SpecFeature.instance.getSpecs().contains(players[i].name)) {
            if (players[i].isOnline) {
                val team = TeamsFeature.manager.getTeam(players[i])
                if (team == null) {
                    players[i].teleport(solosList[players[i]])
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scattering solo &c${players[i].name}&8 (&c${i + 1}&8/&c${players.size}&8)"))
                } else {
                    players[i].teleport(teamsList[team])
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scattering ${team.prefix}${team.name}&7 teammate &c${players[i].name}&8 (&c${i + 1}&8/&c${players.size}&8)"))
                }
            }
        }
        i++
    }
}

class ScatterFeature : Listener {
    companion object {
        var scattering = false

        // Mode: "ffa" or "teams"
        // Radius: Border radius

        fun scatter(mode: String, world: World, radius: Int, freezing: Boolean): Boolean {
            return when (mode) {
                "ffa" -> {
                    val scatteringList: ArrayList<Player> = ArrayList()
                    val scatteringHashmap: HashMap<Player, Location> = HashMap()
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                            scatteringList.add(player)
                        }
                    }
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Preparing to scatter players, please standby, this might take a bit."))
                    scattering = true
                    for (player in scatteringList) {
                        if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                            var finalLocation: Location? = null
                            while (finalLocation == null) {
                                val location = Location(
                                    world,
                                    Random.nextDouble(-radius.toDouble(), radius.toDouble()),
                                    255.0,
                                    Random.nextDouble(-radius.toDouble(), radius.toDouble())
                                )
                                if (world.getHighestBlockAt(location).type != Material.CACTUS &&
                                    world.getHighestBlockAt(location).type != Material.LAVA &&
                                    world.getHighestBlockAt(location).type != Material.WATER &&
                                    world.getHighestBlockAt(location).type != Material.STATIONARY_WATER &&
                                    world.getHighestBlockAt(location).type != Material.WATER_LILY
                                ) {
                                    finalLocation = Location(
                                        world,
                                        location.x,
                                        world.getHighestBlockAt(location).location.y + 3,
                                        location.z
                                    )
                                }
                            }
                            val chunk = world.getChunkAt(finalLocation)
                            world.loadChunk(chunk)
                            scatteringHashmap[player] = finalLocation
                        }
                    }
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Locations found, now scattering players."))
                    val players = ArrayList<Player>()
                    for (player in Bukkit.getOnlinePlayers()) {
                        players.add(player)
                    }
                    FFAScatterTask(players, scatteringHashmap).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 5L)
                    scattering = false
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &7Successfully scattered all players!"))
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        if (freezing) UHCFeature().freeze()
                    }, 20L)
                    true
                }
                "teams" -> {
                    val solos: ArrayList<Player> = ArrayList()
                    val teamLocations: HashMap<Team, Location> = HashMap()
                    val solosLocations: HashMap<Player, Location> = HashMap()
                    scattering = true
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Preparing to scatter players, please standby, this might take a bit. &8(&7Mode: &cTeams&8 | &7Radius: &c${radius}x${radius}&8)"))
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                            val team = TeamsFeature.manager.getTeam(player)
                            if (team == null) {
                                solos.add(player)
                                var finalLocation: Location? = null
                                while (finalLocation == null) {
                                    val location = Location(world, Random.nextDouble(-radius.toDouble(), radius.toDouble()), 255.0, Random.nextDouble(-radius.toDouble(), radius.toDouble()))
                                    if (world.getHighestBlockAt(location).type != Material.CACTUS &&
                                        world.getHighestBlockAt(location).type != Material.LAVA &&
                                        world.getHighestBlockAt(location).type != Material.WATER &&
                                        world.getHighestBlockAt(location).type != Material.STATIONARY_WATER &&
                                        world.getHighestBlockAt(location).type != Material.WATER_LILY
                                    ) {
                                        finalLocation = Location(world, location.x, world.getHighestBlockAt(location).location.y + 3, location.z)
                                    }
                                }
                                val chunk = world.getChunkAt(finalLocation)
                                world.loadChunk(chunk)
                                solosLocations[player] = finalLocation
                            } else {
                                if (teamLocations[team] == null) {
                                    var finalLocation: Location? = null
                                    while (finalLocation == null) {
                                        val location = Location(world, Random.nextDouble(-radius.toDouble(), radius.toDouble()), 255.0, Random.nextDouble(-radius.toDouble(), radius.toDouble()))
                                        if (world.getHighestBlockAt(location).type != Material.CACTUS &&
                                            world.getHighestBlockAt(location).type != Material.LAVA &&
                                            world.getHighestBlockAt(location).type != Material.WATER &&
                                            world.getHighestBlockAt(location).type != Material.STATIONARY_WATER &&
                                            world.getHighestBlockAt(location).type != Material.WATER_LILY
                                        ) {
                                            finalLocation = Location(world, location.x, world.getHighestBlockAt(location).location.y + 5, location.z)
                                        }
                                    }
                                    val chunk = world.getChunkAt(finalLocation)
                                    world.loadChunk(chunk)
                                    teamLocations[team] = finalLocation
                                }
                            }
                        }
                        }
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Locations found, now scattering players."))
                    val players = ArrayList<Player>()
                    for (player in Bukkit.getOnlinePlayers()) {
                        players.add(player)
                    }
                    TeamScatterTask(players, solosLocations, teamLocations).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 5L)
                    scattering = false
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &7Successfully scattered all players!"))
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        if (freezing) UHCFeature().freeze()
                    }, 20L)
                    true
                }
                else -> {
                    false
                }
            }
        }

        fun scatterSolo(player: Player, world: World, radius: Int): Boolean {
            var finalLocation: Location? = null
            while (finalLocation == null) {
                val location = Location(world, Random.nextDouble(-radius.toDouble(), radius.toDouble()), 255.0, Random.nextDouble(-radius.toDouble(), radius.toDouble()))
                if (world.getHighestBlockAt(location).type != Material.CACTUS &&
                    world.getHighestBlockAt(location).type != Material.LAVA &&
                    world.getHighestBlockAt(location).type != Material.WATER &&
                    world.getHighestBlockAt(location).type != Material.STATIONARY_WATER &&
                    world.getHighestBlockAt(location).type != Material.WATER_LILY
                ) {
                    finalLocation = Location(world, location.x, world.getHighestBlockAt(location).location.y + 3, location.z)
                }
            }
            val chunk = world.getChunkAt(finalLocation)
            world.loadChunk(chunk)
            player.teleport(finalLocation)
            return true
        }
    }

}