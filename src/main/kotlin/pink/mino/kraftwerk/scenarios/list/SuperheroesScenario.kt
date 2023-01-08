package pink.mino.kraftwerk.scenarios.list

import me.lucko.helper.Schedulers
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class SuperheroesScenario : Scenario(
    "Superheroes",
    "Players at the beginning of the day will be assigned a random potion effect, you'll be told the effect given.",
    "superheroes",
    Material.NETHER_STAR
){
    val superheroes: HashMap<OfflinePlayer, PotionEffectType> = hashMapOf()
    val prefix = "&8[&cSuperheroes&8]&7"

    override fun givePlayer(player: Player) {
        val pool = arrayListOf(
            PotionEffectType.HEALTH_BOOST,
            PotionEffectType.SPEED,
            PotionEffectType.FAST_DIGGING,
            PotionEffectType.JUMP,
            PotionEffectType.DAMAGE_RESISTANCE,
            PotionEffectType.INCREASE_DAMAGE,
            PotionEffectType.INVISIBILITY
        )
        if (TeamsFeature.manager.getTeam(player) == null) {
            SpecFeature.instance.specChat("&f${player.name}&7 hasn't been late-scattered to a teammate, not giving them any powers.")
            return
        }
        for (teammate in TeamsFeature.manager.getTeam(player)!!.players) {
            pool.remove(superheroes[teammate])
        }
        val hero = pool[Random.nextInt(pool.size)]
        superheroes[player] = hero
        givePower(player)
        Chat.sendMessage(player, "$prefix Your assigned Avenger is: &f${hero.name}&7.")
    }

    fun assignPowers() {
        for (team in TeamsFeature.manager.getTeams()) {
            if (team.size > 0) {
                val pool = arrayListOf(
                    PotionEffectType.HEALTH_BOOST,
                    PotionEffectType.SPEED,
                    PotionEffectType.JUMP,
                    PotionEffectType.DAMAGE_RESISTANCE,
                    PotionEffectType.INCREASE_DAMAGE,
                    PotionEffectType.INVISIBILITY
                )
                for (player in team.players) {
                    if (pool.size == 0) continue
                    try {
                        val hero = pool[Random.nextInt(pool.size)]
                        superheroes[player as Player] = hero
                        pool.remove(hero)
                        Chat.sendMessage(player, "$prefix Your assigned power is: &f${hero.name}&7.")
                        givePower(player)
                    } catch(_: Error) {}
                }
            }
        }
    }

    override fun onStart() {
        if (!enabled) return
        assignPowers()
    }

    @EventHandler
    fun onPlayerConsume(e: PlayerItemConsumeEvent) {
        if (!enabled) return
        if (e.item.type == Material.MILK_BUCKET) {
            Schedulers.sync().runLater({
                givePower(e.player)
            }, 1L)
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (!enabled) return
        if (e.entity.type == EntityType.PLAYER) {
            if (superheroes[(e.entity as Player)] == PotionEffectType.JUMP) {
                if (e.cause == EntityDamageEvent.DamageCause.FALL) e.isCancelled = true
            }
        }
    }

    fun givePower(player: Player) {
        if (SpecFeature.instance.isSpec(player)) return
        when (superheroes[player]) {
            PotionEffectType.HEALTH_BOOST -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.HEALTH_BOOST, 99999, 4))
            }
            PotionEffectType.SPEED -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999, 1))
                player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 99999, 1))
            }
            PotionEffectType.JUMP -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 99999, 3))
            }
            PotionEffectType.DAMAGE_RESISTANCE -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 1))
                player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999, 0))
            }
            PotionEffectType.INCREASE_DAMAGE -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 0))
            }
            PotionEffectType.INVISIBILITY -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0))
                player.addPotionEffect(PotionEffect(PotionEffectType.WATER_BREATHING, 99999, 0))
            }
        }
    }
}