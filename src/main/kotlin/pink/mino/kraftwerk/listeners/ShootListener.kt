package pink.mino.kraftwerk.listeners

import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.HealthChatColorer
import kotlin.math.floor

class ShootListener : Listener {
    @EventHandler
    fun onShoot(e: EntityDamageByEntityEvent) {
        if (e.damager.type == EntityType.ARROW && ((e.damager as Arrow).shooter) is Player && e.entity.type == EntityType.PLAYER) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                val shooter = ((e.damager as Arrow).shooter) as Player
                val victim = e.entity as Player
                val el: EntityLiving = (victim as CraftPlayer).handle
                val health = floor(victim.health / 2 * 10 + el.absorptionHearts / 2 * 10)
                val color = HealthChatColorer.returnHealth(health)
                val preference = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(shooter.uniqueId)!!.projectileMessages
                if (preference == "CHAT") {
                    Chat.sendMessage(shooter, "${Chat.dash} &f${victim.name}&7 is at ${color}${health}%&7!")
                } else if (preference == "SUBTITLE") {
                    shooter.sendTitle(Chat.colored("&7"), Chat.colored("&f${victim.name}&7 is at ${color}${health}%&7!"))
                }
            }, 1L)
        }
    }
}