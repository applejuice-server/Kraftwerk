package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.PlayerUtils

class FuckYouItsAFireballWandScenario : Scenario(
    "Fuck you, It's a Fireball Wand!",
    "At the start of the game, you receive a stick that spawns launchable fireballs.",
    "fuckyouitsafireballwand",
    Material.FIREWORK_CHARGE
) {
    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                val stick = ItemBuilder(Material.STICK)
                    .name("&cFireball Wand")
                    .make()
                val list = arrayListOf(
                    stick
                )
                PlayerUtils.bulkItems(player, list)
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (!enabled) return
        if (e.item != null && e.item.itemMeta.displayName == Chat.colored("&cFireball Wand")) {
            val fb = e.player.launchProjectile(Fireball::class.java)
            fb.setIsIncendiary(true)
            fb.velocity = e.player.location.direction.multiply(2)
        }
    }

    @EventHandler
    fun onPlayerDamageByEntity(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (e.damager is Fireball && (e.damager as Fireball).shooter == e.entity) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (!enabled) return
        if (e.entity is Player && e.cause == EntityDamageEvent.DamageCause.FALL) {
            e.isCancelled = true
        }
    }

    override fun givePlayer(player: Player) {
        val stick = ItemBuilder(Material.STICK)
            .name("&cFireball Wand")
            .make()
        val list = arrayListOf(
            stick
        )
        PlayerUtils.bulkItems(player, list)
    }
}