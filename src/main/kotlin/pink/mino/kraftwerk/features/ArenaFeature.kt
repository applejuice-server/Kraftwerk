package pink.mino.kraftwerk.features

import com.mongodb.client.model.Filters
import me.lucko.helper.Schedulers
import me.lucko.helper.utils.Log
import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.BlockAnimation
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.HealthChatColorer
import pink.mino.kraftwerk.utils.Killstreak
import kotlin.math.floor


class ArenaFeature : Listener {
    companion object {
        val instance = ArenaFeature()
    }
    val prefix = "&8[&cArena&8]&7"

    fun unbreakableItem(material: Material): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.spigot().isUnbreakable = true
        item.itemMeta = meta
        return item
    }

    fun alias(value: String?): ItemStack {
        when (value) {
            "SWORD" -> {
                return unbreakableItem(Material.DIAMOND_SWORD)
            }
            "BOW" -> {
                return unbreakableItem(Material.BOW)
            }
            "ROD" -> {
                return unbreakableItem(Material.FISHING_ROD)
            }
            "BLOCKS" -> {
                return ItemStack(Material.COBBLESTONE, 64)
            }
            "WATER" -> {
                return ItemStack(Material.WATER_BUCKET)
            }
            "LAVA" -> {
                return ItemStack(Material.LAVA_BUCKET)
            }
            "FOOD" -> {
                return ItemStack(Material.GOLDEN_CARROT, 64)
            }
            "GAPPLES" -> {
                return ItemStack(Material.GOLDEN_APPLE, 5)
            }
            "HEADS" -> {
                val goldenHeads = ItemStack(Material.GOLDEN_APPLE, 3)
                val meta = goldenHeads.itemMeta
                meta.displayName = Chat.colored("&6Golden Head")
                goldenHeads.itemMeta = meta
                return goldenHeads
            }
            else -> {
                return ItemStack(Material.AIR)
            }
        }
    }

    fun send(p: Player) {
        if (SpecFeature.instance.getSpecs().contains(p.name)) {
            SpecFeature.instance.unspec(p)
        }
        p.health = 20.0
        p.fireTicks = 0
        p.foodLevel = 20
        p.saturation = 20F
        p.exp = 0F
        p.level = 0
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.armorContents = null
        p.gameMode = GameMode.SURVIVAL

        Schedulers.async().run {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("kits")) {
                try {
                    val document = find(Filters.eq("uuid", p.uniqueId)).first()!!
                    Schedulers.sync().run {
                        p.inventory.setItem(0, alias(document.getEmbedded(listOf("kit", "slot1"), String::class.java)))
                        p.inventory.setItem(1, alias(document.getEmbedded(listOf("kit", "slot2"), String::class.java)))
                        p.inventory.setItem(2, alias(document.getEmbedded(listOf("kit", "slot3"), String::class.java)))
                        p.inventory.setItem(3, alias(document.getEmbedded(listOf("kit", "slot4"), String::class.java)))
                        p.inventory.setItem(4, alias(document.getEmbedded(listOf("kit", "slot5"), String::class.java)))
                        p.inventory.setItem(5, alias(document.getEmbedded(listOf("kit", "slot6"), String::class.java)))
                        p.inventory.setItem(6, alias(document.getEmbedded(listOf("kit", "slot7"), String::class.java)))
                        p.inventory.setItem(7, alias(document.getEmbedded(listOf("kit", "slot8"), String::class.java)))
                        p.inventory.setItem(8, alias(document.getEmbedded(listOf("kit", "slot9"), String::class.java)))
                        p.inventory.setItem(9, ItemStack(Material.ARROW, 64))

                        p.inventory.helmet = unbreakableItem(Material.IRON_HELMET)
                        p.inventory.chestplate = unbreakableItem(Material.IRON_CHESTPLATE)
                        p.inventory.leggings = unbreakableItem(Material.IRON_LEGGINGS)
                        p.inventory.boots = unbreakableItem(Material.DIAMOND_BOOTS)
                    }
                } catch (e: NullPointerException) {
                    Schedulers.sync().run {
                        p.inventory.setItem(0, unbreakableItem(Material.DIAMOND_SWORD))
                        p.inventory.setItem(1, unbreakableItem(Material.FISHING_ROD))
                        p.inventory.setItem(2, unbreakableItem(Material.BOW))
                        p.inventory.setItem(3, ItemStack(Material.COBBLESTONE, 64))
                        p.inventory.setItem(4, ItemStack(Material.WATER_BUCKET))
                        p.inventory.setItem(5, ItemStack(Material.LAVA_BUCKET))
                        p.inventory.setItem(6, ItemStack(Material.GOLDEN_CARROT, 64))
                        p.inventory.setItem(7, ItemStack(Material.GOLDEN_APPLE, 5))
                        val goldenHeads = ItemStack(Material.GOLDEN_APPLE, 3)
                        val meta = goldenHeads.itemMeta
                        meta.displayName = Chat.colored("&6Golden Head")
                        goldenHeads.itemMeta = meta
                        p.inventory.setItem(8, goldenHeads)
                        p.inventory.setItem(9, ItemStack(Material.ARROW, 64))

                        p.inventory.helmet = unbreakableItem(Material.IRON_HELMET)
                        p.inventory.chestplate = unbreakableItem(Material.IRON_CHESTPLATE)
                        p.inventory.leggings = unbreakableItem(Material.IRON_LEGGINGS)
                        p.inventory.boots = unbreakableItem(Material.DIAMOND_BOOTS)
                    }
                }
            }
        }
        ScatterFeature.scatterSolo(p, Bukkit.getWorld("Arena"), 100)
        p.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 100, true, false))
        p.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 60, 100, true, false))
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (e.entity.world.name != "Arena") return
        if (e.entityType == EntityType.PLAYER) {
            if (e.finalDamage >= (e.entity as Player).health) {
                e.damage = 0.0
                e.isCancelled = true
                e.entity.world.strikeLightningEffect(e.entity.location)
                this.send((e.entity as Player))
                if(e is EntityDamageByEntityEvent) {
                    if (e.entityType === EntityType.PLAYER && e.damager != null && e.damager.type === EntityType.ARROW && (e.damager as Arrow).shooter === e.entity) {
                        e.isCancelled = true
                    }
                    if (e.damager.type == EntityType.PLAYER) {
                        if ((e.damager as Player).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                            (e.damager as Player).removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
                        }
                    }
                    if (e.damager.type == EntityType.PLAYER) {
                        val killer = e.damager as Player
                        val victim = e.entity as Player
                        killer.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 2, true, true))
                        val goldenHeads = ItemStack(Material.GOLDEN_APPLE, 1)
                        val meta = goldenHeads.itemMeta
                        meta.displayName = Chat.colored("&6Golden Head")
                        goldenHeads.itemMeta = meta
                        killer.inventory.addItem(goldenHeads)
                        killer.inventory.addItem(ItemStack(Material.ARROW, 8))
                        val el: EntityLiving = (killer as CraftPlayer).handle
                        val health = floor(killer.health / 2 * 10 + el.absorptionHearts / 2 * 10)
                        val color = HealthChatColorer.returnHealth(health)
                        killer.sendMessage(Chat.colored("$prefix &7You killed &f${victim.name}&7!"))
                        victim.sendMessage(Chat.colored("$prefix &7You were killed by &f${killer.name} &8(${color}${health}❤&8)"))
                        Killstreak.addKillstreak(killer)
                        if (Killstreak.getKillstreak(killer) > JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(killer)!!.highestArenaKs) {
                            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(killer)!!.highestArenaKs = Killstreak.getKillstreak(killer)
                        }
                        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(killer)!!.arenaKills++
                        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(victim)!!.arenaDeaths++
                        Log.info("${killer.name} now has a killstreak of ${Killstreak.getKillstreak(killer)}.")
                        if (Killstreak.getKillstreak(victim) >= 5) {
                            sendToPlayers("${prefix}&f ${victim.name}&7 lost their killstreak of &f${
                                Killstreak.getKillstreak(
                                    victim
                                )
                            } kills&7 to &f${killer.name}&7!")
                        }
                        if (Killstreak.getKillstreak(killer) > 3) {
                            sendToPlayers(Chat.colored("$prefix &f${killer.name}&7 now has a killstreak of &f${
                                Killstreak.getKillstreak(
                                    killer
                                )
                            } kills&7!"))
                            killer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 2, false, true))
                        }
                        Killstreak.resetKillstreak(victim)
                    } else if (e.damager.type === EntityType.ARROW && (e.damager as Arrow).shooter as Player != e.entity as Player) {
                        val killer = (e.damager as Arrow).shooter as Player
                        val victim = e.entity as Player
                        killer.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 2, true, true))
                        val goldenHeads = ItemStack(Material.GOLDEN_APPLE, 1)
                        val meta = goldenHeads.itemMeta
                        meta.displayName = Chat.colored("&6Golden Head")
                        goldenHeads.itemMeta = meta
                        killer.inventory.addItem(goldenHeads)
                        killer.inventory.addItem(ItemStack(Material.ARROW, 8))
                        val el: EntityLiving = (killer as CraftPlayer).handle
                        val health = floor(killer.health / 2 * 10 + el.absorptionHearts / 2 * 10)
                        val color = HealthChatColorer.returnHealth(health)
                        killer.sendMessage(Chat.colored("$prefix &7You killed &f${victim.name}&7!"))
                        victim.sendMessage(Chat.colored("$prefix &7You were killed by &f${killer.name} &8(${color}${health}❤&8)"))
                        Killstreak.addKillstreak(killer)
                        print("${killer.name} now has a killstreak of ${Killstreak.getKillstreak(killer)}.")
                        if (Killstreak.getKillstreak(victim) >= 5) {
                            sendToPlayers("${prefix}&f ${victim.name}&7 lost their killstreak of &f${
                                Killstreak.getKillstreak(
                                    victim
                                )
                            } kills&7 to &f${killer.name}&7!")
                        }
                        if (Killstreak.getKillstreak(killer) > 3) {
                            sendToPlayers(Chat.colored("$prefix &f${killer.name}&7 now has a killstreak of &f${
                                Killstreak.getKillstreak(
                                    killer
                                )
                            } kills&7!"))
                            killer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 2, false, true))
                        }
                        Killstreak.resetKillstreak(victim)
                    }
                } else {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.entity as Player)!!.arenaDeaths++
                    Chat.sendMessage((e.entity as Player), "$prefix You died!")
                    if (Killstreak.getKillstreak((e.entity as Player)) >= 5) {
                        sendToPlayers("${prefix}&f ${(e.entity as Player).name}&7 lost their killstreak of &f${
                            Killstreak.getKillstreak(
                                (e.entity as Player)
                            )
                        } kills&7!")
                    }
                    Killstreak.resetKillstreak((e.entity as Player))
                }
            }
        }
    }

    fun getPlayers(): List<Player> {
        val players = ArrayList<Player>()
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.world.name == "Arena" && !SpecFeature.instance.isSpec(player)) {
                players.add(player)
            }
        }
        return players
    }
    fun sendToPlayers(message: String) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.world.name == "Arena") {
                Chat.sendMessage(player, message)
            }
        }
    }

    @EventHandler
    fun onAnimalSpawn(e: EntitySpawnEvent) {
        if (e.location.world.name != "Arena") {
            return
        }
        when (e.entityType) {
            EntityType.CHICKEN -> {
                e.isCancelled = true
            }
            EntityType.HORSE -> {
                e.isCancelled = true
            }
            EntityType.COW -> {
                e.isCancelled = true
            }
            EntityType.SHEEP -> {
                e.isCancelled = true
            }
            EntityType.OCELOT -> {
                e.isCancelled = true
            }
            EntityType.PIG -> {
                e.isCancelled = true
            }
            EntityType.WOLF -> {
                e.isCancelled = true
            }
            EntityType.MUSHROOM_COW -> {
                e.isCancelled = true
            }
            EntityType.CREEPER -> {
                e.isCancelled = true
            }
            EntityType.SKELETON -> {
                e.isCancelled = true
            }
            EntityType.ZOMBIE -> {
                e.isCancelled = true
            }
            EntityType.ENDERMAN -> {
                e.isCancelled = true
            }
            EntityType.RABBIT -> {
                e.isCancelled = true
            }
            EntityType.WITCH -> {
                e.isCancelled = true
            }
            EntityType.SPIDER -> {
                e.isCancelled = true
            }
            else -> {}
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.block.world.name != "Arena") return
        e.isCancelled = e.block.type != Material.LONG_GRASS && e.block.type != Material.LEAVES && e.block.type != Material.LEAVES_2
    }

    @EventHandler
    fun onPlayerDrop(e: PlayerDropItemEvent) {
        if (e.player.world.name != "Arena") return
        e.isCancelled = true
    }

    @EventHandler
    fun onBucketEmpty(e: PlayerBucketEmptyEvent) {
        if (e.player.world.name != "Arena") return
        if (e.bucket == Material.LAVA || e.bucket == Material.LAVA_BUCKET || e.bucket == Material.STATIONARY_LAVA) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                e.blockClicked.getRelative(e.blockFace).type = Material.AIR
            }, 100L)
        }
        if (e.bucket == Material.WATER || e.bucket == Material.WATER_BUCKET || e.bucket == Material.STATIONARY_WATER) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                e.blockClicked.getRelative(e.blockFace).type = Material.AIR
            }, 100L)
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (e.block.world.name != "Arena") return
        if (e.block.location.y > 100.0) {
            Chat.sendMessage(e.player, "$prefix You can't place blocks above &cy-100&7.")
            e.isCancelled = true
        }
        when (e.block.type) {
            Material.COBBLESTONE -> {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    e.player.inventory.addItem(ItemStack(Material.COBBLESTONE))
                }, 1L)
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    BlockAnimation().blockCrackAnimation(e.player, e.block, 1)
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        BlockAnimation().blockCrackAnimation(e.player, e.block, 2)
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            BlockAnimation().blockCrackAnimation(e.player, e.block, 3)
                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                BlockAnimation().blockCrackAnimation(e.player, e.block, 4)
                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                    BlockAnimation().blockCrackAnimation(e.player, e.block, 5)
                                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                        BlockAnimation().blockCrackAnimation(e.player, e.block, 6)
                                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                            BlockAnimation().blockCrackAnimation(e.player, e.block, 7)
                                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                BlockAnimation().blockCrackAnimation(e.player, e.block, 8)
                                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                    BlockAnimation().blockCrackAnimation(e.player, e.block, 9)
                                                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                        BlockAnimation().blockBreakAnimation(null, e.block)
                                                        e.block.type = Material.AIR
                                                    }, 20L)
                                                }, 20L)
                                            }, 20L)
                                        }, 20L)
                                    }, 20L)
                                }, 20L)
                            }, 20L)
                        }, 20L)
                    }, 20L)
                }, 20L)
            }
            Material.LAVA, Material.STATIONARY_LAVA -> {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    e.block.type = Material.AIR
                }, 100L)
            }
            Material.WATER, Material.STATIONARY_WATER -> {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    e.block.type = Material.AIR
                }, 100L)
            }
            else -> {}
        }
    }
}