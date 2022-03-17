package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPortalEvent
import org.bukkit.event.player.PlayerPortalEvent
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.LocationUtils


class PortalListener : Listener {
    @EventHandler
    fun on(event: PlayerPortalEvent) {
        val agent = event.portalTravelAgent
        val player = event.player
        val from: Location = event.from
        val fromWorld: World = from.world
        if (!LocationUtils.hasBlockNearby(Material.PORTAL, from)) {
            return
        }
        val fromName = fromWorld.name
        val targetName: String = when (fromWorld.environment) {
            World.Environment.NORMAL -> fromName + "_nether"
            World.Environment.NETHER -> {
                if (!fromName.endsWith("_nether")) {
                    Chat.sendMessage(player, "${Chat.prefix} You don't appear to be in the nether. Please helpop if this is incorrect.")
                    return
                }
                fromName.substring(0, fromName.length - 7)
            }
            else -> return
        }
        val targetWorld = Bukkit.getWorld(targetName)
        if (targetWorld == null) {
            player.sendMessage(Chat.prefix + "The nether hasn't been generated for this world.")
            return
        }
        val multiplier = if (fromWorld.environment === World.Environment.NETHER) 8.0 else 0.125
        var to: Location? = Location(
            targetWorld,
            from.x * multiplier,
            from.y,
            from.z * multiplier,
            from.yaw,
            from.pitch
        )
        to!!.chunk.load(true)
        to = agent.findOrCreate(to)
        to = LocationUtils.findSafeLocationInsideBorder(to, 10, agent)
        if (to == null || to.y < 0) {
            Chat.sendMessage(player, "${Chat.prefix} Couldn't find a safe place inside of the overworld, defaulting to 0,0.")
            to = agent.findOrCreate(Location(targetWorld, 0.0, 100.0, 0.0))
            to = LocationUtils.findSafeLocationInsideBorder(to, 10, agent)
            event.to = to
        } else {
            event.to = to
        }
    }

    @EventHandler
    fun onEntityPortal(event: EntityPortalEvent) {
        val agent = event.portalTravelAgent
        val from: Location = event.from
        val fromWorld: World = from.world
        if (!LocationUtils.hasBlockNearby(Material.PORTAL, from)) {
            return
        }
        val fromName = fromWorld.name
        val targetName: String = when (fromWorld.environment) {
            World.Environment.NORMAL -> fromWorld.toString() + "_nether"
            World.Environment.NETHER -> {
                if (!fromName.endsWith("_nether")) {
                    return
                }
                fromName.substring(0, fromName.length - 7)
            }
            else -> return
        }
        val targetWorld = Bukkit.getWorld(targetName) ?: return
        val multiplier = if (fromWorld.environment === World.Environment.NETHER) 8.0 else 0.125
        var to: Location? = Location(
            targetWorld,
            from.x * multiplier,
            from.y,
            from.z * multiplier,
            from.yaw,
            from.pitch
        )
        to = agent.findOrCreate(to)
        to = LocationUtils.findSafeLocationInsideBorder(to, 10, agent)
        if (to == null || to.y < 0) {
            to = agent.findOrCreate(Location(targetWorld, 0.0, 100.0, 0.0))
            to = LocationUtils.findSafeLocationInsideBorder(to, 10, agent)
            event.to = to
        } else {
            event.to = to
        }
    }
}