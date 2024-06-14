package pink.mino.kraftwerk.listeners.donator

import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.Perk
import pink.mino.kraftwerk.utils.PerkChecker

class CowboyFeature : Listener {
    @EventHandler
    fun onRightClick(e: PlayerInteractEntityEvent) {
        if (GameState.currentState != GameState.LOBBY || e.player.world.name != "Spawn") return
        if (SpecFeature.instance.isSpec(e.player)) return
        if (e.rightClicked.type == EntityType.PLAYER && PerkChecker.checkPerks(e.player).contains(Perk.RIDE_PLAYERS) && !e.player.isInsideVehicle) {
            e.rightClicked.passenger = e.player
            Chat.sendMessage(e.player, "&8[&2$$$&8] &7You are now riding ${Chat.secondaryColor}${e.rightClicked.name}&7.")
            Chat.sendMessage(e.rightClicked, "&8[&2$$$&8] ${Chat.secondaryColor}${e.player.name}&7 is now riding you.")
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (GameState.currentState != GameState.LOBBY || e.player.world.name != "Spawn") return
        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            if (e.player.passenger != null && PerkChecker.checkPerks(e.player).contains(Perk.RIDE_PLAYERS)) {
                Chat.sendMessage(e.player.passenger, "&8[&2$$$&8] ${Chat.secondaryColor}${e.player.name}&7 has launched you!")
                val passenger = e.player.passenger
                (e.player).passenger.eject()
                (e.player).eject()
                val dir: Vector = e.player.location.direction
                val vec = Vector(dir.x * 3.5, dir.y * 3.5, dir.z * 3.5)
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    passenger.velocity = vec
                }, 2L)
            }
        }
    }

    @EventHandler
    fun onPlayerTeleport(e: PlayerTeleportEvent) {
        if (e.player.passenger != null) {
            (e.player).passenger.eject()
            (e.player).eject()
        }
    }

}