package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
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
                    for (player in Bukkit.getOnlinePlayers()) {
                        scatteringList.add(player)
                    }
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Preparing to scatter players, please standby, this will take a bit. &8(&7Mode: &cFFA&8 | &7Radius: &c${radius}x${radius} &8| &7Scattering: &c${scatteringList.size}&8)"))
                    for ((index, player) in scatteringList.withIndex()) {
                        var finalLocation: Location? = null
                        while (finalLocation == null) {
                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                val location = Location(world, Random.nextDouble(-radius.toDouble(), radius.toDouble()), 255.0, Random.nextDouble(-radius.toDouble(), radius.toDouble()))
                                if (world.getHighestBlockAt(location).type != Material.CACTUS &&
                                    world.getHighestBlockAt(location).type != Material.LAVA &&
                                    world.getHighestBlockAt(location).type != Material.WATER &&
                                    world.getHighestBlockAt(location).type != Material.STATIONARY_WATER
                                ) {
                                    finalLocation = location
                                }
                            }, 1L)
                        }
                        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scattering &c${player.name}&8 (&c${index}&8/&c${scatteringList.size}&8)"))
                        player.teleport(finalLocation)
                    }
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &7Successfully scattered all players!"))
                    true
                }
                "teams" -> {

                    true
                }
                else -> {
                    false
                }
            }
        }
    }

}