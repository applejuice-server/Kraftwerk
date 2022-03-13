package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class AvengersScenario : Scenario(
    "Avengers",
    "Players are assigned a random power like an Avenger, you'll be told the effects of said power.",
    "avengers",
    Material.DIAMOND_SWORD
) {
    val superheroes: HashMap<Player, String> = hashMapOf()
    val prefix = "&8[&cAvengers&8]&7 "
    var cooldowns = HashMap<String, Long>()

    /*
            val cooldownTime = 60 // Get number of seconds from wherever you want
            if (cooldowns.containsKey(sender.name)) {
                val secondsLeft: Long = cooldowns[sender.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                if (secondsLeft > 0) {
                    sender.sendMessage(Chat.colored("&cYou can't use this command for another $secondsLeft second(s)!"))
                    return false
                }
            }
     */

    // cooldowns[sender.name] = System.currentTimeMillis()

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
                        val hero = pool[Random.nextInt(pool.size)]
                        superheroes[player as Player] = hero
                        pool.remove(hero)
                        Chat.colored("$prefix Your assigned Avenger is: &f${hero}&7.")
                    }
                }
            }
        }
    }

    override fun onStart() {
        assignAvengers()
        applyAvengers()
    }

    fun applyAvengers() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                when (superheroes[player]) {
                    "Captain America" -> {

                    }
                    "Spiderman" -> {

                    }
                    "Quicksilver" -> {
                        player.maxHealth = 14.0
                        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999999, 3, false, false))
                        player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 99999999, 1, false, false))
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
                            Chat.colored("&Right-click: Receive &c12 seconds&7 of Speed IV & Jump Boost II&7."),
                            Chat.colored("&7Cooldown: 50 seconds")
                        )
                        bloodRush.itemMeta = bloodRushMeta
                        player.inventory.addItem(bloodRush)
                        player.inventory.addItem(combatBoots)
                    }
                    "Hulk" -> {
                        player.maxHealth = 22.0
                        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999999, 1, false, false))
                        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999999, 2, false, false))
                        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 99999999, 2, false, false))
                    }
                    "Thor" -> {
                        player.maxHealth = 28.0
                        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999999, 1, false, false))
                        val stormbreaker = ItemStack(Material.IRON_AXE)
                        val stormbreakerMeta = stormbreaker.itemMeta
                        stormbreakerMeta.spigot().isUnbreakable = true
                        stormbreakerMeta.lore = listOf(
                            "&7Right-click: Smite everyone within a 7 block radius.",
                            "&7Cooldown: 50 seconds"
                        )
                        stormbreaker.itemMeta = stormbreakerMeta
                        player.inventory.addItem(stormbreaker)
                    }
                    "Iron Man" -> {
                        player.maxHealth = 16.0
                        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999999, 2, false, false))
                        player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999999, 1, false, false))
                        player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 99999999, 1, false, false))
                        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 99999999, 1, false, false))
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
                        bowMeta.displayName = Chat.colored("&dHoyt Gamemaster 2")
                        bowMeta.lore = listOf(
                            Chat.colored("&7Left-click: Has a 50% chance to fire &8cTNT&7 or a &cFireball&7."),
                            Chat.colored("&7Passive: Heals &c2% of your health upon shooting someone with an arrow.")
                        )
                        val chestplate = ItemStack(Material.IRON_CHESTPLATE)
                        val chestplateMeta = chestplate.itemMeta
                        chestplateMeta.spigot().isUnbreakable = true
                        chestplateMeta.displayName = Chat.colored("&fHawkeye's Chestplate")
                        chestplate.itemMeta = chestplateMeta
                        bow.itemMeta = bowMeta
                        player.inventory.addItem(chestplate)
                        player.inventory.addItem(bow)
                    }
                }
                player.health = player.maxHealth
            }
        }
    }
}