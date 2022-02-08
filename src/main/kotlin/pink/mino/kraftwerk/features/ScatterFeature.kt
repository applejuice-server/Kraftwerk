package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class ScatterFeature : Listener {
    companion object {
        var scattering = false

        // Mode: "ffa" or "teams"
        // Radius: Border radius

        fun scatter(mode: String, world: World, radius: Int): Boolean {
            return when (mode) {
                "ffa" -> {
                    val scatteringList: ArrayList<Player> = ArrayList()
                    val scatteringHashmap: HashMap<Player, Location> = HashMap()
                    for (player in Bukkit.getOnlinePlayers()) {
                        scatteringList.add(player)
                    }
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Preparing to scatter players, please standby, this might take a bit."))
                    scattering = true
                    for (player in scatteringList) {
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
                        scatteringHashmap[player] = finalLocation
                    }
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Locations found, now scattering players."))
                    for ((index, player) in scatteringList.withIndex()) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            player.teleport(scatteringHashmap[player])
                            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scattering &c${player.name}&8 (&c${index + 1}&8/&c${scatteringList.size}&8)"))
                        }, 5L)
                    }
                    scattering = false
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &7Successfully scattered all players!"))
                    true
                }
                "teams" -> {
                    print(TeamsFeature.manager.getTeams())
                    val solos: ArrayList<Player> = ArrayList()
                    val teamLocations: HashMap<Team, Location> = HashMap()
                    val solosLocations: HashMap<Player, Location> = HashMap()
                    scattering = true
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Preparing to scatter players, please standby, this might take a bit. &8(&7Mode: &cTeams&8 | &7Radius: &c${radius}x${radius}&8)"))
                    for (player in Bukkit.getOnlinePlayers()) {
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
                                        finalLocation = Location(world, location.x, world.getHighestBlockAt(location).location.y + 3, location.z)
                                    }
                                }
                                val chunk = world.getChunkAt(finalLocation)
                                world.loadChunk(chunk)
                                teamLocations[team] = finalLocation
                            }
                        }
                    }
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Locations found, now scattering players."))
                    for ((index, player) in Bukkit.getOnlinePlayers().withIndex()) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            val team = TeamsFeature.manager.getTeam(player)
                            if (team == null) {
                                player.teleport(solosLocations[player])
                                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scattering solo &c${player.name}&8 (&c${index + 1}&8/&c${Bukkit.getOnlinePlayers().size}&8)"))
                            } else {
                                player.teleport(teamLocations[team])
                                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scattering &c${team.name}&7 teammate &c${player.name}&8 (&c${index + 1}&8/&c${Bukkit.getOnlinePlayers().size}&8)"))
                            }
                        }, 5L)
                    }
                    scattering = false
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &7Successfully scattered all players!"))
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