package pink.mino.kraftwerk.scenarios.list

import me.lucko.helper.Schedulers
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.features.SettingsFeature
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
            PotionEffectType.DAMAGE_RESISTANCE,
            PotionEffectType.INCREASE_DAMAGE,
        )
        if (SettingsFeature.instance.data!!.getInt("game.teamSize") >= 6) {
            pool.add(PotionEffectType.INVISIBILITY)
        }
        if (SettingsFeature.instance.data!!.getInt("game.teamSize") >= 5) {
            pool.add(PotionEffectType.JUMP)
        }
        if (TeamsFeature.manager.getTeam(player) != null) {
            for (teammate in TeamsFeature.manager.getTeam(player)!!.players) {
                pool.remove(superheroes[teammate])
            }
        }
        val hero = pool[Random.nextInt(pool.size)]
        superheroes[player] = hero
        givePower(player)
        Chat.sendMessage(player, "$prefix Your assigned power is: &f${hero.name}&7.")
    }

    fun assignPowers() {
        if (SettingsFeature.instance.data!!.getInt("game.teamSize") > 1) {
            for (team in TeamsFeature.manager.getTeams()) {
                if (team.size > 0) {
                    val pool = arrayListOf(
                        PotionEffectType.HEALTH_BOOST,
                        PotionEffectType.SPEED,
                        PotionEffectType.DAMAGE_RESISTANCE,
                        PotionEffectType.INCREASE_DAMAGE,
                    )
                    if (SettingsFeature.instance.data!!.getInt("game.teamSize") >= 6) {
                        pool.add(PotionEffectType.INVISIBILITY)
                    }
                    if (SettingsFeature.instance.data!!.getInt("game.teamSize") >= 5) {
                        pool.add(PotionEffectType.JUMP)
                    }
                    for (player in team.players) {
                        if (pool.size == 0) continue
                        try {
                            val hero = pool[Random.nextInt(pool.size)]
                            if (player.isOnline) {
                                superheroes[player as Player] = hero
                                pool.remove(hero)
                                Chat.sendMessage(player, "$prefix Your assigned power is: &f${hero.name}&7.")
                                givePower(player)
                            }
                        } catch(_: Error) {}
                    }
                }
            }
        } else {
            for (player in Bukkit.getOnlinePlayers()) {
                val pool = arrayListOf(
                    PotionEffectType.HEALTH_BOOST,
                    PotionEffectType.SPEED,
                    PotionEffectType.DAMAGE_RESISTANCE,
                    PotionEffectType.INCREASE_DAMAGE,
                )
                if (SettingsFeature.instance.data!!.getInt("game.teamSize") >= 6) {
                    pool.add(PotionEffectType.INVISIBILITY)
                }
                if (SettingsFeature.instance.data!!.getInt("game.teamSize") >= 5) {
                    pool.add(PotionEffectType.JUMP)
                }
                val hero = pool[Random.nextInt(pool.size)]
                superheroes[player as Player] = hero
                Chat.sendMessage(player, "$prefix Your assigned power is: &f${hero.name}&7.")
                givePower(player)
            }
        }
    }

    override fun onPvP() {
        if (!enabled) return
        assignPowers()
    }

    @EventHandler
    fun onPlayerToggleFlight(e: PlayerToggleFlightEvent) {
        val player = e.player
        if (player.gameMode == GameMode.SPECTATOR || player.gameMode == GameMode.SPECTATOR || player.isFlying || superheroes[e.player] != PotionEffectType.JUMP) {
            return
        } else {
            e.isCancelled = true
            player.allowFlight = false
            player.isFlying = true
            player.velocity = e.player.location.direction.multiply(1.5).setY(1)
            Schedulers.sync().runLater(runnable@ {
                player.allowFlight = true
            }, 20)
        }
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

    @EventHandler
    fun onGoldenAppleConsume(e: PlayerItemConsumeEvent) {
        if (!enabled) return
        val absorption = SettingsFeature.instance.data!!.getBoolean("game.options.absorption")
        if (superheroes[(e.player)] == PotionEffectType.HEALTH_BOOST && e.item.type == Material.GOLDEN_APPLE) {
            Schedulers.sync().runLater(runnable@ {
                if (absorption) {
                    if (e.item.itemMeta.displayName != null && e.item.itemMeta.displayName == "§6Golden Head") {
                        e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 160 * 2, 1))
                    } else {
                        e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 160, 1))
                    }
                } else {
                    if (e.item.itemMeta.displayName != null && e.item.itemMeta.displayName == "§6Golden Head") {
                        e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200 * 2, 1))
                    } else {
                        e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 1))
                    }
                }
            }, 1)
        }
    }
    fun givePower(player: Player) {
        if (SpecFeature.instance.isSpec(player)) return
        val absorption = SettingsFeature.instance.data!!.getBoolean("game.options.absorption")
        when (superheroes[player]) {
            PotionEffectType.HEALTH_BOOST -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.HEALTH_BOOST, 99999, 4))
                player.health = player.maxHealth
            }
            PotionEffectType.SPEED -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999, 1))
                player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 99999, 1))
            }
            PotionEffectType.JUMP -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 99999, 3))
                player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 99999, 0))
            }
            PotionEffectType.DAMAGE_RESISTANCE -> {
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
                if (!absorption) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 1))
                } else {
                    player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 0))
                    player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999, 0))
                }
            }
            PotionEffectType.INCREASE_DAMAGE -> {
                if (!absorption) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 0))
                } else {
                    player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 0))
                    player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999, 0))
                }
            }
            PotionEffectType.INVISIBILITY -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0))
                player.addPotionEffect(PotionEffect(PotionEffectType.WATER_BREATHING, 99999, 0))
            }
        }
    }
}