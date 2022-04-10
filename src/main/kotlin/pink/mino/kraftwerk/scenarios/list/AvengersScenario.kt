package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.BlockUtil
import pink.mino.kraftwerk.utils.Chat
import kotlin.math.floor
import kotlin.random.Random


class AvengersScenario : Scenario(
    "Avengers",
    "Players are assigned a random power like an Avenger, you'll be told the effects of said power.",
    "avengers",
    Material.DIAMOND_SWORD
) {
    val superheroes: HashMap<Player, String> = hashMapOf()
    val prefix = "&8[&cAvengers&8]&7"
    var cooldowns = HashMap<String, Long>()
    var pvpEnabled: Boolean = false

    override fun onPvP() {
        pvpEnabled = true
    }

    override fun givePlayer(player: Player) {
        val pool = arrayListOf(
            "Captain America",
            "Spiderman",
            "Quicksilver",
            "Hulk",
            "Thor",
            "Iron Man",
            "Hawkeye"
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
        giveAvengers(player)
        Chat.sendMessage(player, "$prefix Your assigned Avenger is: &f${hero}&7.")
    }

    fun assignAvengers() {
        for (team in TeamsFeature.manager.getTeams()) {
            if (team.size > 0) {
                val pool = arrayListOf(
                    "Captain America",
                    "Spiderman",
                    "Quicksilver",
                    "Hulk",
                    "Thor",
                    "Iron Man",
                    "Hawkeye"
                )
                for (player in team.players) {
                    if (pool.size == 0) continue
                    if (player.isOnline) {
                        try {
                            val hero = pool[Random.nextInt(pool.size)]
                            superheroes[player as Player] = hero
                            pool.remove(hero)
                            Chat.sendMessage(player, "$prefix Your assigned Avenger is: &f${hero}&7.")
                        } catch(_: Error) {}
                    }
                }
            }
        }
    }

    override fun onStart() {
        if (!enabled) return
        assignAvengers()
        applyAvengers()
    }

    fun giveAvengers(player: Player) {
        when (superheroes[player]) {
            "Captain America" -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999999, 0, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999999, 0, false, false))
                val shield = ItemStack(Material.IRON_CHESTPLATE)
                val shieldMeta = shield.itemMeta
                shieldMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true)
                shieldMeta.addEnchant(Enchantment.THORNS, 2, true)
                shieldMeta.spigot().isUnbreakable = true
                shieldMeta.displayName = Chat.colored("&cShield")
                shield.itemMeta = shieldMeta
                val superSoldier = ItemStack(Material.BLAZE_POWDER)
                val superSoldierMeta = superSoldier.itemMeta
                superSoldierMeta.displayName = Chat.colored("&cSuper Soldier Serum")
                superSoldierMeta.lore = listOf(
                    Chat.colored("&7Right-click: Gives all your teammates strength for 10s."),
                    Chat.colored("&7Cooldown: 40 seconds")
                )
                superSoldier.itemMeta = superSoldierMeta
                player.inventory.addItem(superSoldier)
                player.inventory.addItem(shield)
            }
            "Spiderman" -> {
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999999, 0, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999999, 0, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 99999999, 0, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 99999999, 2, false, false))
                val spideySense = ItemStack(Material.WATCH)
                val spideySenseMeta = spideySense.itemMeta
                spideySenseMeta.displayName = Chat.colored("&cSpidey Sense")
                spideySenseMeta.lore = listOf(
                    Chat.colored("&7Right-click: View all players within a 150 block radius."),
                    Chat.colored("&7Cooldown: 20 seconds")
                )
                spideySense.itemMeta = spideySenseMeta
                val webShooter = ItemStack(Material.WEB)
                val webShooterMeta = webShooter.itemMeta
                webShooterMeta.displayName = Chat.colored("&cWeb Shooter")
                webShooterMeta.lore = listOf(
                    Chat.colored("&7Right-click: Gives slowness to nearby enemies."),
                    Chat.colored("&7Passive: Makes a webcage whenever you kill someone.")
                )
                webShooter.itemMeta = webShooterMeta
                player.inventory.addItem(webShooter)
                player.inventory.addItem(spideySense)
            }
            "Quicksilver" -> {
                player.maxHealth = 14.0
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999999, 2, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 99999999, 0, false, false))
                val combatBoots = ItemStack(Material.IRON_BOOTS)
                val combatBootsMeta = combatBoots.itemMeta
                combatBootsMeta.displayName = Chat.colored("&cCombat Boots")
                combatBootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                combatBootsMeta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true)
                combatBoots.itemMeta = combatBootsMeta
                val bloodRush = ItemStack(Material.REDSTONE)
                val bloodRushMeta = bloodRush.itemMeta
                bloodRushMeta.displayName = Chat.colored("&cBlood Rush")
                bloodRushMeta.lore = listOf(
                    Chat.colored("&7Right-click: Receive &c12 seconds&7 of Speed IV & Jump Boost II&7."),
                    Chat.colored("&7Cooldown: 50 seconds")
                )
                bloodRush.itemMeta = bloodRushMeta
                player.inventory.addItem(bloodRush)
                player.inventory.addItem(combatBoots)
            }
            "Hulk" -> {
                player.maxHealth = 22.0
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999999, 0, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999999, 1, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 99999999, 1, false, false))
            }
            "Thor" -> {
                player.maxHealth = 28.0
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999999, 0, false, false))
                val stormbreaker = ItemStack(Material.IRON_AXE)
                val stormbreakerMeta = stormbreaker.itemMeta
                stormbreakerMeta.displayName = Chat.colored("&cStormbreaker")
                stormbreakerMeta.spigot().isUnbreakable = true
                stormbreakerMeta.lore = listOf(
                    Chat.colored("&7Right-click: Smite everyone within a 7 block radius."),
                    Chat.colored("&7Cooldown: 50 seconds")
                )
                stormbreaker.itemMeta = stormbreakerMeta
                player.inventory.addItem(stormbreaker)
            }
            "Iron Man" -> {
                player.maxHealth = 16.0
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999999, 1, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999999, 0, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 99999999, 0, false, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999999, 0, false, false))
                val repulsorTech = ItemStack(Material.MAGMA_CREAM)
                val repulsorTechMeta = repulsorTech.itemMeta
                repulsorTechMeta.displayName = Chat.colored("&cRepulsor Tech Mark LXXXV")
                repulsorTechMeta.lore = listOf(
                    Chat.colored("&Right-click: Grants flight for &d5 seconds&7 & all players &cfire resistance&7 for &d20 seconds&7."),
                    Chat.colored("&760 second cooldown")
                )
                repulsorTech.itemMeta = repulsorTechMeta
                player.inventory.addItem(repulsorTech)
            }
            "Hawkeye" -> {
                val bow = ItemStack(Material.BOW)
                val bowMeta = bow.itemMeta
                bowMeta.spigot().isUnbreakable = true
                bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false)
                bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 2, false)
                bowMeta.displayName = Chat.colored("&cHoyt Gamemaster 2")
                bowMeta.lore = listOf(
                    Chat.colored("&7Left-click: Has a 50% chance to fire &cFireball&7."),
                    Chat.colored("&7Passive: Heals &c2%&7 of your health upon shooting someone with an arrow.")
                )
                val chestplate = ItemStack(Material.IRON_CHESTPLATE)
                val chestplateMeta = chestplate.itemMeta
                chestplateMeta.spigot().isUnbreakable = true
                chestplateMeta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 4, true)
                chestplateMeta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 1, true)
                chestplateMeta.displayName = Chat.colored("&fHawkeye's Chestplate")
                chestplate.itemMeta = chestplateMeta
                bow.itemMeta = bowMeta
                player.inventory.addItem(chestplate)
                player.inventory.addItem(bow)
            }
        }
        player.health = player.maxHealth
    }

    fun applyAvengers() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                giveAvengers(player)
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            (e.entity as Player).maxHealth = 20.0
        }, 20L)
        if (e.entity.killer == null) return
        if (superheroes[e.entity.killer] == "Spiderman") {
            val location: Location = e.entity.location
            val blocks: ArrayList<Block>? = BlockUtil().getBlocks(location.block, 10)
            blocks!!.stream().filter { block: Block ->
                floor(
                    block.location.distance(location)
                ) == 4.0
            }.filter { block: Block -> block.type === Material.AIR }.forEach { block: Block ->
                block.type = Material.WEB
            }
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (!enabled) return
        if (e.entity.type == EntityType.PLAYER) {
            if (superheroes[(e.entity as Player)] == "Thor") {
                if (e.cause == EntityDamageEvent.DamageCause.LIGHTNING) e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (!enabled) return
        when (superheroes[e.player]) {
            "Captain America" -> {
                if (e.item != null && e.item.itemMeta.displayName == Chat.colored("&cSuper Soldier Serum")) {
                    e.isCancelled = true
                    val cooldownTime = 40
                    if (cooldowns.containsKey(e.player.name)) {
                        val secondsLeft: Long = cooldowns[e.player.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                        if (secondsLeft > 0) {
                            e.player.sendMessage(Chat.colored("&cYou can't use this ability for another $secondsLeft second(s)!"))
                            return
                        }
                    }
                    cooldowns[e.player.name] = System.currentTimeMillis()
                    for (teammate in TeamsFeature.manager.getTeam(e.player)!!.players) {
                        if (teammate.isOnline) {
                            Chat.sendMessage((teammate as Player), "$prefix Your teammate &f${e.player.name}&7 has given your &cStrength I&7 for 10 seconds.")
                            teammate.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, true, true))
                        }
                    }
                }
            }
            "Spiderman" -> {
                if (e.item != null) {
                    if (e.item.itemMeta.displayName == Chat.colored("&cSpidey Sense")) {
                        e.isCancelled = true
                        val cooldownTime = 25
                        if (cooldowns.containsKey(e.player.name)) {
                            val secondsLeft: Long = cooldowns[e.player.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                            if (secondsLeft > 0) {
                                e.player.sendMessage(Chat.colored("&cYou can't use this ability for another $secondsLeft second(s)!"))
                                return
                            }
                        }
                        val nearby = e.player.getNearbyEntities(75.0, 75.0, 75.0)
                        val nearbyPlayers: ArrayList<Player> = arrayListOf()
                        for (entity in nearby) {
                            if (entity.type == EntityType.PLAYER) {
                                nearbyPlayers.add(entity as Player)
                            }
                        }
                        Chat.sendMessage(e.player, "$prefix There are &f${nearbyPlayers.size} players&7 near you.")
                        if (nearbyPlayers.size == 0) {
                            Chat.sendMessage(e.player, "$prefix No players found nearby...")
                            return
                        }
                        cooldowns[e.player.name] = System.currentTimeMillis()
                    }
                    if (e.item.itemMeta.displayName == Chat.colored("&cWeb Shooter")) {
                        e.isCancelled = true
                        val cooldownTime = 25
                        if (cooldowns.containsKey(e.player.name)) {
                            val secondsLeft: Long = cooldowns[e.player.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                            if (secondsLeft > 0) {
                                e.player.sendMessage(Chat.colored("&cYou can't use this ability for another $secondsLeft second(s)!"))
                                return
                            }
                        }
                        val nearby = e.player.getNearbyEntities(8.0, 8.0, 8.0)
                        val nearbyPlayers: ArrayList<Player> = arrayListOf()
                        for (entity in nearby) {
                            if (entity.type == EntityType.PLAYER) {
                                nearbyPlayers.add(entity as Player)
                            }
                        }
                        if (nearbyPlayers.size == 0) {
                            Chat.sendMessage(e.player, "$prefix No players found nearby...")
                            return
                        }
                        for (player in nearbyPlayers) {
                            if (player != e.player) {
                                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 160, 0, true, false))
                                Chat.sendMessage(player, "$prefix You've been slowed from &f${e.player.name}&7's Web Shooter.")
                            }
                        }
                        Chat.sendMessage(e.player, "$prefix Slowed down &f${nearbyPlayers.size} players&7.")
                        cooldowns[e.player.name] = System.currentTimeMillis()
                    }
                }
            }
            "Thor" -> {
                if (e.item != null) {
                    if (e.item.itemMeta.displayName == Chat.colored("&cStormbreaker") && e.action == Action.RIGHT_CLICK_AIR) {
                        val cooldownTime = 10
                        if (cooldowns.containsKey(e.player.name)) {
                            val secondsLeft: Long = cooldowns[e.player.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                            if (secondsLeft > 0) {
                                e.player.sendMessage(Chat.colored("&cYou can't use this ability for another $secondsLeft second(s)!"))
                                return
                            }
                        }
                        if (!pvpEnabled) {
                            Chat.sendMessage(e.player, "&cYou can't use this ability before PvP.")
                            return
                        }
                        val near = e.player.getNearbyEntities(5.0, 5.0, 5.0)
                        val nearby: ArrayList<Player> = arrayListOf()
                        for (entity in near) {
                            if (entity.type == EntityType.PLAYER) nearby.add(entity as Player)
                        }
                        if (nearby.size == 0) {
                            Chat.sendMessage(e.player, "$prefix No players found nearby...")
                            return
                        }
                        for (player in nearby) {
                            if (TeamsFeature.manager.getTeam(player) != TeamsFeature.manager.getTeam(e.player) && player != e.player) {
                                e.player.world.strikeLightning(player.location)
                                Chat.sendMessage(player, "$prefix You've been smited by &f${e.player.name}&7's Stormbreaker.")
                            }
                        }
                        cooldowns[e.player.name] = System.currentTimeMillis()
                    }
                }
            }
            "Quicksilver" -> {
                if (e.item != null) {
                    if (e.item.itemMeta.displayName == Chat.colored("&cBlood Rush")) {
                        e.isCancelled = true
                        val cooldownTime = 50
                        if (cooldowns.containsKey(e.player.name)) {
                            val secondsLeft: Long = cooldowns[e.player.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                            if (secondsLeft > 0) {
                                e.player.sendMessage(Chat.colored("&cYou can't use this ability for another $secondsLeft second(s)!"))
                                return
                            }
                        }
                        cooldowns[e.player.name] = System.currentTimeMillis()
                        Chat.sendMessage(e.player, "$prefix You're feeling a bit lighter... perhaps you can go a bit faster...")
                        e.player.removePotionEffect(PotionEffectType.JUMP)
                        e.player.removePotionEffect(PotionEffectType.SPEED)
                        e.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 240, 3, true, false))
                        e.player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 240, 1, true, false))
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            e.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 999999999, 2, true, false))
                            e.player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 999999999, 0, true, false))
                        }, 400L)
                    }
                }
            }
            "Iron Man" -> {
                if (e.item != null) {
                    if (e.item.itemMeta.displayName == Chat.colored("&cRepulsor Tech Mark LXXXV")) {
                        e.isCancelled = true
                        val cooldownTime = 60
                        if (cooldowns.containsKey(e.player.name)) {
                            val secondsLeft: Long = cooldowns[e.player.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                            if (secondsLeft > 0) {
                                e.player.sendMessage(Chat.colored("&cYou can't use this ability for another $secondsLeft second(s)!"))
                                return
                            }
                        }
                        cooldowns[e.player.name] = System.currentTimeMillis()
                        e.player.allowFlight = true
                        e.player.isFlying = true
                        Chat.sendMessage(e.player, "$prefix You've been given &c5 seconds&7 of flight.")
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (player == e.player) continue
                            if (TeamsFeature.manager.getTeam(player) != null && TeamsFeature.manager.getTeam(player) == TeamsFeature.manager.getTeam(e.player)) {
                                Chat.sendMessage(player, "$prefix You've been given &c20 seconds&7 of Fire Resistance by &f${e.player.name}&7.")
                                player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 0, true, false))
                            }
                        }
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            e.player.allowFlight = false
                            e.player.isFlying = false
                        }, 100L)
                    }
                }
            }
            "Hawkeye" -> {
                if (e.item != null) {
                    if (e.item.itemMeta.displayName == Chat.colored("&cHoyt Gamemaster 2") && e.action == Action.LEFT_CLICK_AIR) {
                        if (Random.nextBoolean()) e.player.launchProjectile(Fireball::class.java)
                    }
                }
            }
        }
    }
}