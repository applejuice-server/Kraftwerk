package pink.mino.kraftwerk.features

import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.BlockAnimation
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.HealthChatColorer
import kotlin.math.floor


class ArenaFeature : Listener {
    companion object {
        val instance = ArenaFeature()
    }

    fun unbreakableItem(material: Material): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.spigot().isUnbreakable = true
        item.itemMeta = meta
        return item
    }

    fun send(p: Player) {
        var statement = "SELECT (killstreaks) from arena WHERE uuid = '${p.uniqueId}'"
        val result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
        if (!result.isBeforeFirst) {
            statement = "INSERT INTO arena (uuid, killstreaks) VALUES ('${p.uniqueId}', 0)"
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                createStatement().execute(statement)
            }
        }
        p.health = 20.0
        p.foodLevel = 20
        p.saturation = 20F
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.armorContents = null
        p.gameMode = GameMode.SURVIVAL

        p.inventory.setItem(0, unbreakableItem(Material.DIAMOND_SWORD))
        p.inventory.setItem(1, unbreakableItem(Material.FISHING_ROD))
        p.inventory.setItem(2, unbreakableItem(Material.BOW))
        p.inventory.setItem(3, ItemStack(Material.COBBLESTONE, 64))
        p.inventory.setItem(4, ItemStack(Material.WATER_BUCKET))
        p.inventory.setItem(5, ItemStack(Material.LAVA_BUCKET))
        p.inventory.setItem(6, ItemStack(Material.GOLDEN_CARROT, 16))
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

        ScatterFeature.scatterSolo(p, Bukkit.getWorld("Arena"), 100)
        p.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 100, true, false))
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (e.entity.world.name != "Arena") return
        if (e.entityType === EntityType.PLAYER && e.damager != null && e.damager.type === EntityType.ARROW && (e.damager as Arrow).shooter === e.entity) {
            e.isCancelled = true
        }
        if (e.damager.type == EntityType.PLAYER) {
            if ((e.damager as Player).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                (e.damager as Player).removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (e.entity.world.name != "Arena") {
            return
        }
        val victim = e.entity
        val killer = victim.killer

        e.drops.clear()

        if (killer != null) {
            var statement = "SELECT (killstreaks) from arena WHERE uuid = '${killer.uniqueId}'"
            var result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
            result.next()
            var killerKillstreak = result.getInt("killstreaks")

            statement = "SELECT (killstreaks) from arena WHERE uuid = '${victim.uniqueId}'"
            result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
            result.next()
            val victimKillstreak = result.getInt("killstreaks")

            statement = "UPDATE arena SET killstreaks = 0 where uuid = '${victim.uniqueId}'"
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                createStatement().execute(statement)
            }
            statement = "UPDATE arena SET killstreaks = ${killerKillstreak + 1} where uuid = '${killer.uniqueId}'"
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                createStatement().execute(statement)
            }

            statement = "SELECT (killstreaks) from arena WHERE uuid = '${killer.uniqueId}'"
            result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
            result.next()
            killerKillstreak = result.getInt("killstreaks")
            if (victimKillstreak > 5) {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &f${victim.name}&7 lost their killstreak of &f${victimKillstreak} kills&7 to &f${killer.name}&7!"))
            }
            if (killerKillstreak > 3) {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &f${killer.name}&7 now has a killstreak of &f${killerKillstreak} kills&7!"))
                killer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 2, false, true))
            }
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
            killer.sendMessage(Chat.colored("${Chat.prefix} &7You killed &f${victim.name}&7!"))
            victim.sendMessage(Chat.colored("${Chat.prefix} &7You were killed by &f${killer.name} &8(${color}${health}❤&8)"))
        } else {
            val statement = "UPDATE arena SET killstreaks = 0 where uuid = '${victim.uniqueId}'"
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                createStatement().execute(statement)
            }
        }
    }

    @EventHandler
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        if (e.respawnLocation.world.name != "Arena") return
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            this.send(e.player)
        }, 1L)
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
            else -> {}
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.block.world.name != "Arena") return
        e.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (e.block.world.name != "Arena") return
        if (e.block.type == Material.COBBLESTONE) {
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
        } else if (e.block.type == Material.LAVA && e.block.type == Material.STATIONARY_LAVA) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                e.block.type = Material.AIR
            }, 200L)
        } else if (e.block.type == Material.WATER && e.block.type == Material.STATIONARY_WATER) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                e.block.type = Material.AIR
            }, 200L)
        }
    }
}