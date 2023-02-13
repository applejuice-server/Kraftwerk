package pink.mino.kraftwerk.features

import me.lucko.helper.Schedulers
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.utils.GameState

class StatsFeature : Listener {

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.entity as Player)!!.deaths++
        if (e.entity.killer != null && e.entity.killer.type == EntityType.PLAYER) {
            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.entity.killer as Player)!!.kills++
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        val player = JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.player as Player)!!
        when (e.block.type) {
            Material.DIAMOND_ORE -> {
                player.diamondsMined++
            }
            Material.GOLD_ORE -> {
                player.goldMined++
            }
            Material.IRON_ORE -> {
                player.ironMined++
            }
            else -> {}
        }

    }

    @EventHandler
    fun onConsume(e: PlayerItemConsumeEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        if (e.item.type == Material.GOLDEN_APPLE) Schedulers.async().run { JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.player as Player)!!.gapplesEaten++ }
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.whoClicked as Player)!!.timesCrafted++
        if (e.currentItem.type == Material.GOLDEN_APPLE) {
            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.whoClicked as Player)!!.gapplesCrafted++
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        if (e.isCancelled) return
        if (e.entity.type == EntityType.PLAYER) {
            if (e.damager.type == EntityType.PLAYER) {
                val damager = e.damager as Player
                val victim = e.entity as Player
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(victim)!!.damageTaken += e.finalDamage / 2
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(damager)!!.damageDealt += e.finalDamage / 2
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(damager)!!.meleeHits++
            } else if (e.damager.type == EntityType.ARROW && (e.damager as Arrow).shooter is Player) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.entity as Player)!!.damageTaken += e.finalDamage
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer((e.damager as Arrow).shooter as Player)!!.bowHits++
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer((e.damager as Arrow).shooter as Player)!!.bowMisses--
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer((e.damager as Arrow).shooter as Player)!!.damageDealt += e.finalDamage
            }
        }
    }

    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        if (e.entity is Arrow && e.entity.shooter is Player) {
            val loc: Location = e.entity.location
            val vec: Vector = e.entity.velocity
            val block = Location(loc.world, loc.x + vec.x, loc.y + vec.y, loc.z + vec.z).block
            if (block != null) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer((e.entity as Arrow).shooter as Player)!!.bowMisses++
            }
        }
    }

    @EventHandler
    fun onBowShoot(e: EntityShootBowEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        if (e.projectile is Arrow && (e.projectile as Arrow).shooter is Player) {
            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer((e.projectile as Arrow).shooter as Player)!!.bowShots++
        }
    }

    @EventHandler
    fun onEnchant(e: EnchantItemEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.enchanter as Player)!!.timesEnchanted++
    }
}