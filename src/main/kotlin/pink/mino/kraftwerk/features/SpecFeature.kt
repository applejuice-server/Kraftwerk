package pink.mino.kraftwerk.features

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.lunarclient.bukkitapi.LunarClientAPI
import com.mongodb.MongoException
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.*
import java.util.*
import kotlin.math.floor
import kotlin.math.round

class InvSeeFeature(private val player: Player, private val target: Player) : BukkitRunnable() {
    override fun run() {
        if (!target.isOnline) {
            cancel()
            return
        }
        if (player.openInventory.title != "${target.name}'s Inventory") {
            cancel()
            return
        }
        for ((index, item) in target.inventory.contents.withIndex()) {
            if (item == null) {
                player.openInventory.topInventory.setItem(index, ItemStack(Material.AIR))
            } else {
                player.openInventory.topInventory.setItem(index, item)
            }
        }

        if (target.inventory.helmet != null) player.openInventory.topInventory.setItem(38, target.inventory.helmet)
        if (target.inventory.chestplate != null) player.openInventory.topInventory.setItem(39, target.inventory.chestplate)
        if (target.inventory.leggings != null) player.openInventory.topInventory.setItem(41, target.inventory.leggings)
        if (target.inventory.boots != null) player.openInventory.topInventory.setItem(42, target.inventory.boots)

        val info = ItemBuilder(Material.BOOK)
            .name("&cPlayer Info")
            .addLore(" ")
            .addLore("&cStatistics: ")
            .addLore(" ${Chat.dot} Health ${Chat.dash} ${PlayerUtils.getHealth(target)}")
            .addLore(" ${Chat.dot} Hunger ${Chat.dash} ${Chat.primaryColor}${target.foodLevel / 2}")
            .addLore(" ${Chat.dot} XP Level ${Chat.dash} ${Chat.primaryColor}${target.level} &8(${Chat.primaryColor}${round(target.exp * 100)}%&8)")
            .addLore(" ${Chat.dot} Kills ${Chat.dash} ${Chat.primaryColor}${SettingsFeature.instance.data!!.getInt("game.kills." + target.name)}")
            .addLore(" ${Chat.dot} Location ${Chat.dash} ${Chat.primaryColor}${target.location.blockX}, ${target.location.blockY}, ${target.location.blockZ}")
            .addLore(" ${Chat.dot} World ${Chat.dash} ${Chat.primaryColor}${target.location.world.name}")
            .addLore(" ")
            .addLore("&cMining: ")
            .addLore(" ${Chat.dot} Diamond ${Chat.dash} &b${SpecFeature.instance.diamondsMined[target.uniqueId] ?: 0}")
            .addLore(" ${Chat.dot} Gold ${Chat.dash} &6${SpecFeature.instance.goldMined[target.uniqueId] ?: 0}")
            .addLore(" ")
            .addLore("&cPotion Effects: ")
        if (target.activePotionEffects.isEmpty()) {
            info.addLore(" ${Chat.dot} ${Chat.primaryColor}None.")
        } else {
            for (eff in target.activePotionEffects) {
                info.addLore(" ${Chat.dot} ${Chat.primaryColor}${InvseeUtils().getPotionName(eff.type).uppercase()} ${InvseeUtils().integerToRoman(eff.amplifier + 1)} &8(&c${InvseeUtils().potionDurationToString(eff.duration / 20)}&8)")
            }
        }
        info.addLore(" ")
        val actualBook = info.make()
        player.openInventory.topInventory.setItem(40, actualBook)
    }
}

class SpecClickFeature : PacketAdapter(JavaPlugin.getPlugin(Kraftwerk::class.java), ListenerPriority.MONITOR, PacketType.Play.Client.WINDOW_CLICK) {
    override fun onPacketReceiving(e: PacketEvent) {
        if (e.packetType.equals(PacketType.Play.Client.WINDOW_CLICK)) {
            val packet = e.packet
            if (SpecFeature.instance.isSpec(e.player)) {
                val p = e.player
                when (packet.integers.read(1)) {
                    19 -> {
                        p.teleport(Location(p.world, 0.0, 100.0, 0.0))
                        Chat.sendMessage(p, "${Chat.dash} You have been teleported to 0,0.")
                    }
                    21 -> {
                        Bukkit.dispatchCommand(p, "nearby")
                    }
                    22 -> {
                        if (JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get().specSocialSpy) {
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get().specSocialSpy = false
                            Chat.sendMessage(p, "${SpecFeature.instance.prefix} Disabled &5Social Spy&7!")
                        } else {
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get().specSocialSpy = true
                            Chat.sendMessage(p, "${SpecFeature.instance.prefix} Enabled &5Social Spy&7!")
                        }
                        val socialSpy = ItemBuilder(Material.NAME_TAG)
                            .addLore("&7Click to view social commands.")
                        val pref = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get().specSocialSpy
                        if (pref) {
                            socialSpy.name("&aSocial Spy")
                        } else {
                            socialSpy.name("&cSocial Spy")
                        }
                        p.inventory.setItem(22, socialSpy.make())
                    }
                    23 -> {
                        val list = ArrayList<Player>()
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (player != p && !SpecFeature.instance.isSpec(player)) {
                                list.add(player)
                            }
                        }
                        if (list.isEmpty()) {
                            Chat.sendMessage(p, "${Chat.dash} There are no players nearby.")
                            return
                        }
                        Chat.sendMessage(p, Chat.line)
                        Chat.sendCenteredMessage(p, "${Chat.primaryColor}&lPlayer Locations")
                        for (player in list) {
                            Chat.sendMessage(
                                p,
                                "${Chat.dash} &7${player.name} &7is at &b${floor(player.location.x)}&7, &b${floor(player.location.y)}&7, &b${
                                    floor(player.location.z)
                                }"
                            )
                        }
                        Chat.sendMessage(p, Chat.line)
                    }
                    25 -> {
                        Bukkit.dispatchCommand(p, "respawn")
                    }
                    else -> { return }
                }
            }
        }
    }
}


class SpecFeature : Listener {
    val diamondsMined: HashMap<UUID, Int> = HashMap()
    val goldMined: HashMap<UUID, Int> = HashMap()

    companion object {
        val instance = SpecFeature()
    }
    val prefix = "&8[${Chat.primaryColor}Spec&8]&7"
    val specStartTimes: HashMap<UUID, Long> = hashMapOf()

    fun joinSpec(p: Player) {
        specStartTimes[p.uniqueId] = Date().time
        p.health = 20.0
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
        p.gameMode = GameMode.SPECTATOR

        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (list.contains(p.name)) list.remove(p.name)
        SettingsFeature.instance.data!!.set("game.list", list)
        list = SettingsFeature.instance.data!!.getStringList("game.specs")
        if (!list.contains(p.name)) list.add(p.name)
        SettingsFeature.instance.data!!.set("game.specs", list)
        SettingsFeature.instance.saveData()

        specChat("${Chat.secondaryColor}${p.name}&7 has entered spectator mode.", p)
        Scoreboard.setScore(Chat.colored("${Chat.dash} &7Playing..."), PlayerUtils.getPlayingPlayers().size)

        val teleportTo00 = ItemBuilder(Material.EYE_OF_ENDER)
            .name("${Chat.primaryColor}Teleport to 0,0")
            .addLore("&7Click the item to teleport yourself to &c0,0&7.")
            .make()
        val nearby = ItemBuilder(Material.COMPASS)
            .name("${Chat.primaryColor}Nearby Players")
            .addLore("&7Click the item to see a list of nearby players.")
            .make()
        val locations = ItemBuilder(Material.MAP)
            .name("${Chat.primaryColor}Player Locations")
            .addLore("&7Click the item to see a list of player locations.")
            .make()
        val respawn = ItemBuilder(Material.BONE)
            .name("${Chat.primaryColor}Respawn Players")
            .addLore("&7Click to view a list of dead players that can be respawned.")
            .make()
        val socialSpy = ItemBuilder(Material.NAME_TAG)
            .addLore("&7Click to view social commands.")
        val pref = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get().specSocialSpy
        if (pref) {
            socialSpy.name("&aSocial Spy")
        } else {
            socialSpy.name("&cSocial Spy")
        }

        p.inventory.setItem(19, teleportTo00)
        p.inventory.setItem(21, nearby)
        p.inventory.setItem(22, socialSpy.make())
        p.inventory.setItem(23, locations)
        p.inventory.setItem(25, respawn)

        Chat.sendMessage(p, "$prefix You are now in spectator mode.")

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            if (LunarClientAPI.getInstance().isRunningLunarClient(p)) {
                Chat.sendMessage(p, "${Chat.dash} &7Your &bLunar Client&7 staff modules have been enabled.")
                LunarClientAPI.getInstance().giveAllStaffModules(p)
            }
        }, 5L)
    }

    val commands = arrayListOf(
        "/msg",
        "/pm",
        "/r",
        "/reply",
        "/tell",
        "/message",
        "/whisper",
        "/mcc"
    )

    @EventHandler
    fun onPlayerCommand(e: PlayerCommandPreprocessEvent) {
        val message = e.message.lowercase().split(" ")
        if (commands.contains(message[0])) {
            for (spectator in getSpecs()) {
                val player = Bukkit.getPlayer(spectator)
                if (player != null && JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(player.uniqueId)
                        .get().specSocialSpy) {
                    Chat.sendMessage(player, "&e&o${e.player.name} ${Chat.dash} &7${message.joinToString(" ")}")
                }
             }
        }
    }

    fun spec(p: Player) {
        specStartTimes[p.uniqueId] = Date().time
        SpawnFeature.instance.send(p)
        p.health = 20.0
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
        p.gameMode = GameMode.SPECTATOR

        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (list.contains(p.name)) list.remove(p.name)
        SettingsFeature.instance.data!!.set("game.list", list)
        list = SettingsFeature.instance.data!!.getStringList("game.specs")
        if (!list.contains(p.name)) list.add(p.name)
        SettingsFeature.instance.data!!.set("game.specs", list)
        SettingsFeature.instance.saveData()

        specChat("${Chat.secondaryColor}${p.name}&7 has entered spectator mode.", p)
        Scoreboard.setScore(Chat.colored("${Chat.dash} &7Playing..."), PlayerUtils.getPlayingPlayers().size)

        val teleportTo00 = ItemBuilder(Material.EYE_OF_ENDER)
            .name("${Chat.primaryColor}Teleport to 0,0")
            .addLore("&7Click the item to teleport yourself to &c0,0&7.")
            .make()
        val nearby = ItemBuilder(Material.COMPASS)
            .name("${Chat.primaryColor}Nearby Players")
            .addLore("&7Click the item to see a list of nearby players.")
            .make()
        val locations = ItemBuilder(Material.MAP)
            .name("${Chat.primaryColor}Player Locations")
            .addLore("&7Click the item to see a list of player locations.")
            .make()
        val respawn = ItemBuilder(Material.BONE)
            .name("${Chat.primaryColor}Respawn Players")
            .addLore("&7Click to view a list of dead players that can be respawned.")
            .make()
        val socialSpy = ItemBuilder(Material.NAME_TAG)
            .addLore("&7Click to view social commands.")
        val pref = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get().specSocialSpy
        if (pref) {
            socialSpy.name("&aSocial Spy")
        } else {
            socialSpy.name("&cSocial Spy")
        }

        p.inventory.setItem(19, teleportTo00)
        p.inventory.setItem(21, nearby)
        p.inventory.setItem(22, socialSpy.make())
        p.inventory.setItem(23, locations)
        p.inventory.setItem(25, respawn)

        Chat.sendMessage(p, "${prefix} You are now in spectator mode.")

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            if (LunarClientAPI.getInstance().isRunningLunarClient(p)) {
                Chat.sendMessage(p, "${Chat.dash} &7Your &bLunar Client&7 staff modules have been enabled.")
                LunarClientAPI.getInstance().giveAllStaffModules(p)
            }
        }, 5L)
    }

    fun unspec(p: Player) {
        val endTime = Date().time
        try {
            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(p)!!.timeSpectated += (endTime - specStartTimes[p.uniqueId]!!)
        } catch (e: MongoException) {
            e.printStackTrace()
        }
        p.health = 20.0
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

        SpawnFeature.instance.send(p)
        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (!list.contains(p.name)) list.add(p.name)
        SettingsFeature.instance.data!!.set("game.list", list)
        list = SettingsFeature.instance.data!!.getStringList("game.specs")
        list.remove(p.name)
        SettingsFeature.instance.data!!.set("game.specs", list)
        SettingsFeature.instance.saveData()

        specChat("${Chat.secondaryColor}${p.name}&7 has left spectator mode.", p)
        Chat.sendMessage(p, "${prefix} You are no longer in spectator mode.")
        Scoreboard.setScore(Chat.colored("${Chat.dash} &7Playing..."), PlayerUtils.getPlayingPlayers().size)

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            if (LunarClientAPI.getInstance().isRunningLunarClient(p)) {
                LunarClientAPI.getInstance().disableAllStaffModules(p)
                Chat.sendMessage(p, "${Chat.dash} &7Your &bLunar Client&7 staff modules have been disabled.")
            }
        }, 5L)

    }

    fun getSpecs(): List<String> {
        return SettingsFeature.instance.data!!.getStringList("game.specs")
    }

    fun isSpec(p: OfflinePlayer): Boolean {
        return getSpecs().contains(p.name)
    }

    fun specChat(chat: String, p: Player? = null) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (getSpecs().contains(player.name)) {
                if (p != null && p == player) {
                    continue
                }
                Chat.sendMessage(player, "$prefix $chat")
            }
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        if (isSpec(p)) {
            e.isCancelled = true
            if (e.currentItem != null && e.currentItem.type != Material.AIR) {
                when (e.currentItem.itemMeta.displayName) {
                    "${Chat.primaryColor}Teleport to 0,0" -> {
                        p.teleport(Location(p.world, 0.0, 100.0, 0.0))
                        Chat.sendMessage(p, "${prefix} You have been teleported to 0,0.")
                    }
                    "${Chat.primaryColor}Nearby Players" -> {
                        Bukkit.dispatchCommand(p, "nearby")
                    }
                    "${Chat.primaryColor}Player Locations" -> {
                        val list = ArrayList<Player>()
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (player != p && !isSpec(player)) {
                                list.add(player)
                            }
                        }
                        if (list.isEmpty()) {
                            Chat.sendMessage(p, "${prefix} There are no players online.")
                        }
                        Chat.sendMessage(p, Chat.line)
                        Chat.sendCenteredMessage(p, "${Chat.primaryColor}&lPlayer Locations")
                        for (player in list) {
                            Chat.sendMessage(p, "${prefix} &7${player.name} &7is at &b${floor(player.location.x)}, &7${floor(player.location.y)}, &7${floor(player.location.z)}")
                        }
                        Chat.sendMessage(p, Chat.line)
                    }
                    "${Chat.primaryColor}Respawn Players" -> {
                        Bukkit.dispatchCommand(p, "respawn")
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (getSpecs().contains(e.player.name)) {
            if (e.player.gameMode == GameMode.SPECTATOR) {
                if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                    e.isCancelled = true
                    val list = ArrayList<Player>()
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (getSpecs().contains(player.name) || player.world.name == "Spawn") continue
                        list.add(player)
                    }
                    if (list.isEmpty()) {
                        Chat.sendMessage(e.player, "&cNo players to teleport to!")
                        return
                    }
                    val target = list.random()
                    e.player.teleport(target.location)
                    Chat.sendMessage(e.player, "${prefix} Teleported to ${Chat.secondaryColor}${target.name}&7!")
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteractWithPlayer(e: PlayerInteractEntityEvent) {
        if (e.player.itemInHand == null) return
        if (getSpecs().contains(e.player.name)) {
            if (e.rightClicked.type == EntityType.PLAYER) {
                val player = (e.rightClicked as Player)
                val gui = GuiBuilder().rows(5).name(ChatColor.translateAlternateColorCodes('&', "${player.name}'s Inventory"))
                e.player.openInventory(gui.make())
                InvSeeFeature(e.player, player).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0, 20L)
            } else {
                Chat.sendMessage(e.player, "&cYou aren't right clicking anyone.")
                return
            }
        }
    }

    @EventHandler
    fun onItemDrop(e: PlayerDropItemEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (e.damager is Player) {
            if (getSpecs().contains(((e.damager) as Player).name)) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onItemPickup(e: PlayerPickupItemEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    var brokenBlocks: HashMap<UUID, HashSet<Block>> = HashMap<UUID, HashSet<Block>>()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(e: BlockBreakEvent) {
        if (GameState.currentState != GameState.INGAME) return
        val p = e.player
        if (brokenBlocks.containsKey(p.uniqueId)) {
            if (brokenBlocks[p.uniqueId]!!.contains(e.block)) return
        }
        if (e.block.type == Material.DIAMOND_ORE) {
            var diamonds = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.DIAMOND_ORE) {
                            diamonds++
                            if (diamondsMined[p.uniqueId] == null) diamondsMined[p.uniqueId] = 0
                            diamondsMined[p.uniqueId] = diamondsMined[p.uniqueId]!! + 1
                            if (brokenBlocks.containsKey(p.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[p.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name}&7 has mined &bDiamond Ore&7. &8(&7T: &b${diamondsMined[p.uniqueId]} &8| &7V: &b${diamonds}&8)"))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        } else if (e.block.type == Material.GOLD_ORE) {
            var gold = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.GOLD_ORE) {
                            gold++
                            if (goldMined[p.uniqueId] == null) goldMined[p.uniqueId] = 0
                            goldMined[p.uniqueId] = goldMined[p.uniqueId]!! + 1
                            if (brokenBlocks.containsKey(p.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[p.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name}&7 has mined &6Gold Ore&7. &8(&7T: &6${goldMined[p.uniqueId]} &8| &7V: &6${gold}&8)"))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity !is Player) return
        val p = e.entity as Player
        if (e.finalDamage == 0.0 || e.damage == 0.0 || e.isCancelled) return
        if (p.health - e.finalDamage <= 0) return
        val percentage = (e.finalDamage / 2) * 10
        when (e.cause) {
            EntityDamageEvent.DamageCause.FALL -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to &fFall&7."))
                            comp.clickEvent = ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/tp ${p.name}"
                            )
                            player.spigot().sendMessage(comp)
                        }, 1L)
                    }
                }
            }
            EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.FIRE_TICK, EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.MELTING -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to &fBurning&7."))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }
                }
            }
            EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.PROJECTILE -> {
                return
            }
            else -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to &fUnknown&7."))
                            comp.clickEvent = ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/tp ${p.name}"
                            )
                            player.spigot().sendMessage(comp)
                        }, 1L)
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDamageByPlayer(e: EntityDamageByEntityEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity !is Player) return
        val p = e.entity as Player
        if (e.finalDamage == 0.0 || e.damage == 0.0 || e.isCancelled) return
        if (p.health - e.finalDamage <= 0) return
        val percentage = (e.finalDamage / 2) * 10
        if (e.entity is Monster) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to &fPvE&7."))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        }
        if (e.damager is Player) {
            val damager = e.damager as Player
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to ${PlayerUtils.getPrefix(damager)}${damager.name} &8(${PlayerUtils.getHealth(damager)}&8)&7. &8(&fPvP&8)"))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        } else if (e.damager is Arrow) {
            val a = e.damager as Arrow
            if (a.shooter is Player) {
                val damager = a.shooter as Player
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to ${PlayerUtils.getPrefix(damager)}${damager.name} &8(${PlayerUtils.getHealth(damager)}&8)&7. &8(&fBow&8)"))
                            comp.clickEvent = ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/tp ${p.name}"
                            )
                            player.spigot().sendMessage(comp)
                        }, 1L)
                    }
                }
            }
        }
    }

}