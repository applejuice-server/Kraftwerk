package pink.mino.kraftwerk.features

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.commands.SendTeamView
import pink.mino.kraftwerk.commands.WhitelistCommand
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.events.PvPEnableEvent
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.*
import java.util.*
import kotlin.math.floor

// https://github.com/LeonTG/Timer/blob/main/src/main/java/com/leontg77/timer/runnable/TimerRunnable.java#L157
private fun timeToString(ticks: Long): String {
    var t = ticks
    val hours = floor(t / 3600.toDouble()).toInt()
    t -= hours * 3600
    val minutes = floor(t / 60.toDouble()).toInt()
    t -= minutes * 60
    val seconds = t.toInt()
    val output = StringBuilder()
    if (hours > 0) {
        output.append(hours).append('h')
        if (minutes == 0) {
            output.append(minutes).append('m')
        }
    }
    if (minutes > 0) {
        output.append(minutes).append('m')
    }
    output.append(seconds).append('s')
    return output.toString()
}

enum class Events {
    PRE_START,
    START,
    FINAL_HEAL,
    PVP,
    MEETUP,

    BORDER_SHRINK_ONE,
    BORDER_SHRINK_TWO,
    BORDER_SHRINK_THREE,
    BORDER_SHRINK_FOUR,
    BORDER_SHRINK_FIVE,
    BORDER_SHRINK_SIX
}

class UHCTask : BukkitRunnable() {
    val portalLocations = hashMapOf<UUID, Location>()

    val finalHeal = SettingsFeature.instance.data!!.getInt("game.events.final-heal") * 60 + 45
    val pvp = ((SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.final-heal")) * 60) + 45
    val meetup = ((SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.meetup")) * 60) + 45
    val borderShrink = (SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.borderShrink")) * 60 + 45

    val rawPvP = (SettingsFeature.instance.data!!.getInt("game.events.pvp") * 60) + 45
    val rawMeetup = (SettingsFeature.instance.data!!.getInt("game.events.meetup") * 60) + 45
    val rawBs = (SettingsFeature.instance.data!!.getInt("game.events.borderShrink") * 60) + 45

    var timer = 0
    var currentEvent: Events = Events.PRE_START

    var host = SettingsFeature.instance.data!!.getString("game.host")
    var scenarios = SettingsFeature.instance.data!!.getStringList("matchpost.scenarios")
    var id = SettingsFeature.instance.data!!.getInt("matchpost.id")
    var team = SettingsFeature.instance.data!!.getString("matchpost.team")
    var winners: Any? = null
    var fill = Bukkit.getOnlinePlayers().size

    var startTime: Long = Date().time
    var endTime: Long? = null

    var paused = false
    var meetupHappened = false

    private fun displayTimer(player: Player) {
        when (currentEvent) {
            Events.PRE_START -> {
                ActionBar.sendActionBarMessage(player, "&cStarting in ${Chat.dash} &f${timeToString((45 - timer).toLong())}")
            }
            Events.START -> {
                ActionBar.sendActionBarMessage(player, "&cFinal Heal is in ${Chat.dash} &f${timeToString((finalHeal - timer).toLong())}")
            }
            Events.FINAL_HEAL -> {
                ActionBar.sendActionBarMessage(player, "&cPvP is enabled in ${Chat.dash} &f${timeToString((pvp - timer).toLong())} &8| &cBorder shrinks in ${Chat.dash} &f${timeToString((borderShrink - timer).toLong())}")
            }
            Events.PVP -> {
                ActionBar.sendActionBarMessage(player, "&cMeetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())} &8| &cBorder shrinks in ${Chat.dash} &f${timeToString((borderShrink - timer).toLong())}")
            }
            Events.BORDER_SHRINK_ONE -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cMeetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())} &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 300) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cIt is now Meetup! Head to 0,0! &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 300) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_TWO -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cMeetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())} &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 600) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cIt is now Meetup! Head to 0,0! &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 600) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_THREE -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cMeetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())} &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 900) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cIt is now Meetup! Head to 0,0! &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 900) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_FOUR -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cMeetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())} &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 1200) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cIt is now Meetup! Head to 0,0! &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 1200) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_FIVE -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cMeetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())} &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 1500) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cIt is now Meetup! Head to 0,0! &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        }) &8| &f${timeToString((meetup + 1500) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_SIX -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cMeetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())} &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        })"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "&cIt is now Meetup! Head to 0,0! &8| &7Border: &f${SettingsFeature.instance.data!!.getInt("pregen.border") * 2} (±${
                            SettingsFeature.instance.data!!.getInt("pregen.border")
                        })"
                    )
                }
            }

        }
    }

    override fun run() {
        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (list == null) list = ArrayList<String>()
        when (timer) {
            0 -> {
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Starting in &f45 seconds&7..."))
            }
            45 -> {
                currentEvent = Events.START
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cc")
                GameState.setState(GameState.INGAME)
                UHCFeature().unfreeze()
                val scenarios = ArrayList<String>()
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenarios.add(scenario.name)
                }
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendCenteredMessage(player, "&c&lUHC")
                }
                Bukkit.broadcastMessage(" ")
                for (player in Bukkit.getOnlinePlayers()) {

                    if (SettingsFeature.instance.data!!.getBoolean("game.specials.frbp")) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, pvp * 20, 0, false, false))
                    }
                    if (SettingsFeature.instance.data!!.getBoolean("game.specials.abp")) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, pvp * 20, 0, false, false))
                    }
                    Chat.sendMessage(player, "&7You may &abegin&7! The host for this game is &c${SettingsFeature.instance.data!!.getString("game.host")}&7!")

                    Chat.sendMessage(player, "&7Scenarios: &f${scenarios.joinToString(", ")}&7")
                    Chat.sendCenteredMessage(player, " ")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 10F, 1F)
                    player.sendTitle(Chat.colored("&a&lGO!"), Chat.colored("&7You may now play the game, do &c/helpop&7 for help!"))
                    if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                        if (SettingsFeature.instance.data!!.getInt("game.starterfood") > 0) {
                            player.inventory.addItem(ItemStack(Material.COOKED_BEEF, SettingsFeature.instance.data!!.getInt("game.starterfood")))
                        }
                        list.add(player.name)
                    }
                }
                SettingsFeature.instance.data!!.set("game.list", list)
                SettingsFeature.instance.saveData()
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onStart()
                }
                Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).setGameRuleValue("doDaylightCycle", true.toString())
                for (player in Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 100, true, true))
                }
            }
            46 -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (!ConfigOptionHandler.getOption("statless")!!.enabled) JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(player)!!.gamesPlayed++
                }
            }
            finalHeal -> {
                currentEvent = Events.FINAL_HEAL
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onFinalHeal()
                }
                for (player in Bukkit.getOnlinePlayers()) {
                    player.health = player.maxHealth
                    player.foodLevel = 20
                    player.saturation = 20F
                    player.fireTicks = 0
                    Chat.sendMessage(player, Chat.line)
                    Chat.sendCenteredMessage(player, "&c&lUHC")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7All players have been healed & fed.")
                    Chat.sendCenteredMessage(player, "&cPvP&7 is enabled in &c${rawPvP / 60} minutes&7.")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.BURP, 10F, 1F)
                }
            }
            pvp -> {
                currentEvent = Events.PVP
                for (world in Bukkit.getWorlds()) {
                    world.pvp = true
                }
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onPvP()
                }
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendMessage(player, Chat.line)
                    Chat.sendCenteredMessage(player, "&c&lUHC")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7PvP has been &aenabled&7.")
                    Chat.sendCenteredMessage(player, "&cMeetup&7 will start in &c${rawMeetup / 60} minutes&7.")
                    Chat.sendCenteredMessage(player, "&7The border will begin &cshrinking&7 in &c${rawBs / 60} minutes&7.")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.ANVIL_LAND, 10F, 1F)
                    if (SettingsFeature.instance.data!!.getBoolean("game.specials.frbp")) {
                        if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE)
                        }
                    }
                    if (SettingsFeature.instance.data!!.getBoolean("game.specials.abp")) {
                        if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                            player.removePotionEffect(PotionEffectType.ABSORPTION)
                        }
                    }
                }
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl on")
                Bukkit.getPluginManager().callEvent(PvPEnableEvent())
            }
            borderShrink -> {
                currentEvent = Events.BORDER_SHRINK_ONE
                for (player in Bukkit.getOnlinePlayers()) {
                    Bukkit.broadcastMessage(Chat.colored(Chat.line))
                    Chat.sendCenteredMessage(player, "&c&lUHC")
                    Chat.sendMessage(player, " ")
                    if (ScenarioHandler.getScenario("bigcrack")!!.enabled) {
                        Chat.sendCenteredMessage(player, "&7The border will start shrinking until it's at &f150x150 (±75)&7!")
                    } else {
                        Chat.sendCenteredMessage(player, "&7The border will start shrinking until it's at &f50x50 (±25)&7!")
                    }
                    UHCFeature().scheduleShrink(500)
                    Bukkit.broadcastMessage(Chat.colored(Chat.line))
                }
            }
            meetup + 1 -> {
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendCenteredMessage(player, "&c&lUHC")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7It's now &cMeetup&7! Head to &a0,0&7!")
                }
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onMeetup()
                }
                SettingsFeature.instance.data!!.set("game.nether.nether", false)
                SettingsFeature.instance.saveData()
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                meetupHappened = true
            }
            meetup + 5 -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.world.name.endsWith("_nether")) {
                        player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 10F, 1F)
                        player.teleport(portalLocations[player.uniqueId]!!)
                        Chat.sendMessage(player, "${Chat.dash} You've been teleported to your previous portal location as it's meetup.")
                    }
                }
            }
            meetup + 300 -> {
                UHCFeature().scheduleShrink(250)
                currentEvent = Events.BORDER_SHRINK_TWO
            }
            meetup + 600 -> {
                UHCFeature().scheduleShrink(100)
                currentEvent = Events.BORDER_SHRINK_THREE
            }
            meetup + 900 -> {
                UHCFeature().scheduleShrink(75)
                currentEvent = Events.BORDER_SHRINK_FOUR
            }
            meetup + 1200 -> {
                if (ScenarioHandler.getScenario("bigcrack")!!.enabled) {
                    return
                }
                UHCFeature().scheduleShrink(50)
                currentEvent = Events.BORDER_SHRINK_FIVE
            }
            meetup + 1500 -> {
                if (ScenarioHandler.getScenario("bigcrack")!!.enabled) {
                    return
                }
                UHCFeature().scheduleShrink(25)
                currentEvent = Events.BORDER_SHRINK_SIX
            }
        }
        Scoreboard.setScore(Chat.colored("${Chat.dash} &7Playing..."), PlayerUtils.getPlayingPlayers().size)
        for (player in Bukkit.getOnlinePlayers()) {
            displayTimer(player)
        }
        if (!paused) timer++
    }
}

class UHCFeature : Listener {
    var scattering = false
    @EventHandler
    fun onPortalEvent(e: PlayerPortalEvent) {
        if (e.player.world.name == SettingsFeature.instance.data!!.getString("pregen.world")) JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.portalLocations[e.player.uniqueId] = e.player.location
    }

    fun start(mode: String) {
        GameState.setState(GameState.WAITING)
        Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).time = 1000
        Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).setGameRuleValue("doDaylightCycle", false.toString())
        for (world in Bukkit.getWorlds()) {
            world.pvp = false
        }
        if (mode == "ffa") {
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Starting a &cFFA&7 UHC game... now freezing players."))
        } else if (mode == "teams") {
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Starting a &cTeams&7 UHC game... now freezing players."))
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                    if (TeamsFeature.manager.getTeam(player) == null) {
                        val team = TeamsFeature.manager.createTeam(player)
                        SendTeamView(team).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
                    }
                }
            }
        }
        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (list == null) list = ArrayList<String>()
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                SpawnFeature.instance.send(player)
                SpawnFeature.instance.editorList.remove(player.uniqueId)
                CombatLogFeature.instance.removeCombatLog(player.name)
                player.playSound(player.location, Sound.WOOD_CLICK, 10F, 1F)
                player.enderChest.clear()
                player.maxHealth = 20.0
                player.health = 20.0
                player.foodLevel = 20
                player.saturation = 20F
                player.exp = 0F
                player.level = 0
                player.gameMode = GameMode.SURVIVAL
                player.inventory.clear()
                player.inventory.armorContents = null
                player.itemOnCursor = ItemStack(Material.AIR)
                val openInventory = player.openInventory
                if (openInventory.type == InventoryType.CRAFTING) {
                    openInventory.topInventory.clear()
                }
                val effects = player.activePotionEffects
                for (effect in effects) {
                    player.removePotionEffect(effect.type)
                }
            }
        }
        SettingsFeature.instance.data!!.set("game.list", list)
        SettingsFeature.instance.saveData()
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl all")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl on")

        val teams = mode != "ffa"
        val world = Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world"))
        val radius = SettingsFeature.instance.data!!.getInt("pregen.border")

        if (teams) {
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Attempting to start &cteam &7scatter..."))
        } else {
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Attempting to start &csolo &7scatter..."))
        }
        Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Standby, this might take a bit."))
        JavaPlugin.getPlugin(Kraftwerk::class.java).scattering = true
        object: BukkitRunnable() {
            override fun run() {
                val loc: List<Location> = ScatterUtils().getScatterLocations(world, radius, WhitelistCommand().getWhitelisted().size)
                if (teams) {
                    for ((index, team) in TeamsFeature.manager.getTeams().withIndex()) {
                        for (player in team.entries) {
                            JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs[player.lowercase()] = loc[index]
                            WhitelistCommand().addWhitelist(player)
                        }
                    }

                    for ((index, online) in WhitelistCommand().getWhitelisted().withIndex()) {
                        val player = Bukkit.getOfflinePlayer(online)
                        if (SpecFeature.instance.getSpecs().contains(player.name)) {
                            continue
                        }
                        if (TeamsFeature.manager.getTeam(Bukkit.getOfflinePlayer(online)) != null) {
                            continue
                        }
                        JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs[online.lowercase()] = loc[index]
                    }
                } else {
                    for ((index, online) in WhitelistCommand().getWhitelisted().withIndex()) {
                        val player = Bukkit.getOfflinePlayer(online)
                        if (SpecFeature.instance.getSpecs().contains(player.name)) {
                            continue
                        }
                        JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs[online.lowercase()] = loc[index]
                    }
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), 30L)

        object: BukkitRunnable() {
            override fun run() {
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} All locations &cfound&7, starting to load chunks..."))

                val locs = JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs.values.toMutableList()
                val names = JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs.keys.toMutableList()

                object: BukkitRunnable() {
                    var i = 0
                    override fun run() {
                        if (i < locs.size) {
                            locs[i].chunk.load(true)
                            i++
                        } else {
                            cancel()
                            locs.clear()
                            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} All chunks &cloaded&7, starting to scatter players..."))

                            object: BukkitRunnable() {
                                var i = 0
                                override fun run() {
                                    if (i < names.size) {
                                        val scatter = Bukkit.getOfflinePlayer(names[i])

                                        if (SpecFeature.instance.getSpecs().contains(scatter.name)) {
                                            return
                                        }

                                        if (JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs[names[i]] == null) {
                                            for (player in Bukkit.getOnlinePlayers()) {
                                                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                                                    Chat.sendMessage(player, "&cCould not find a scatter location for ${scatter.name}&c. Perhaps they were already scattered?")
                                                }
                                            }
                                            return
                                        }
                                        if (!scatter.isOnline) {
                                            val team = TeamsFeature.manager.getTeam(scatter)
                                            if (team == null) {
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Scheduled Scatter for &f${names[i]} &8(&c${i + 1}&8/&c${names.size}&8)"))
                                            } else {
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Scheduled Scatter for ${team.prefix}${names[i]} &8(&c${i + 1}&8/&c${names.size}&8)"))
                                            }
                                        } else {
                                            val scatterP = scatter as Player
                                            scatterP.teleport(JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs[names[i]])
                                            scatterP.gameMode = GameMode.SURVIVAL
                                            scatterP.isFlying = false
                                            scatterP.allowFlight = false
                                            val team = TeamsFeature.manager.getTeam(Bukkit.getOfflinePlayer(names[i]))
                                            if (team == null) {
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Scattered &f${scatterP.name} &8(&c${i + 1}&8/&c${names.size}&8)"))
                                            } else {
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Scattered ${team.prefix}${scatterP.name} &8(&c${i + 1}&8/&c${names.size}&8)"))
                                            }
                                            JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs.remove(names[i])
                                            freeze()
                                            list.add(names[i])
                                        }
                                        i++
                                    } else {
                                        JavaPlugin.getPlugin(Kraftwerk::class.java).scattering = false
                                        Bukkit.broadcastMessage(Chat.colored("${Chat.dash} &7Successfully scattered all players!"))
                                        SettingsFeature.instance.data!!.set("game.list", list)
                                        SettingsFeature.instance.saveData()
                                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                            freeze()
                                            JavaPlugin.getPlugin(Kraftwerk::class.java).game = UHCTask()
                                            JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
                                        }, 20L)
                                        names.clear()
                                        cancel()

                                    }
                                }
                            }.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 40L, 5L)
                        }
                    }
                }.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 5L, 5L)
            }
        }.runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), 40L)
    }

    fun freeze() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                player.removePotionEffect(PotionEffectType.SLOW)
                player.removePotionEffect(PotionEffectType.JUMP)
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
                player.removePotionEffect(PotionEffectType.BLINDNESS)
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 999999999, 10, true, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 999999999, 100, true, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 999999999, -100, true, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999999, 1000, true, false))
            }
        }
    }

    fun unfreeze() {
        for (player in Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.SLOW)
            player.removePotionEffect(PotionEffectType.JUMP)
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
            player.removePotionEffect(PotionEffectType.BLINDNESS)
        }
    }

    fun insideBorder(player: Player, border: Int): Boolean {
        val xLoc = player.location.x
        val zLoc = player.location.z
        val minX = border - (border * 2)
        val maxX = border * 2
        val minZ = border - (border * 2)
        val maxZ = border * 2
        return !(xLoc < minX || xLoc > maxX || zLoc < minZ || zLoc > maxZ)
    }

    fun scheduleShrink(newBorder: Int) {
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f10s&7."))
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f9s&7."))
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f8s&7."))
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f7s&7."))
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f6s&7."))
                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f5s&7."))
                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                    Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f4s&7."))
                                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                        Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f3s&7."))
                                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f2s&7."))
                                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Shrinking to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7 in &f1s&7."))
                                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                    for (player in Bukkit.getOnlinePlayers()) {
                                                        if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                                                            if (!insideBorder(player, newBorder)) {
                                                                player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 160, 10, true, false))
                                                                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 10, true, false))
                                                                Chat.sendMessage(player, "${Chat.dash} You have gained &f8 seconds&7 of &fResistance X&7 as you are outside the border.")
                                                            }
                                                        }
                                                    }
                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "border $newBorder")
                                                    Bukkit.broadcastMessage(Chat.colored(Chat.line))
                                                    Bukkit.broadcastMessage(Chat.colored("${Chat.dash} The border has shrunken to &f${newBorder*2}x${newBorder*2} (±${newBorder})&7."))
                                                    if (ScenarioHandler.getScenario("bigcrack")!!.enabled) {
                                                        if (newBorder != 75) {
                                                            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Next border shrink in &f5 minutes."))
                                                        }
                                                    } else {
                                                        if (newBorder != 25) {
                                                            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Next border shrink in &f5 minutes."))
                                                        }
                                                    }
                                                    Bukkit.broadcastMessage(Chat.colored(Chat.line))
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
        }, 20L)
    }
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (GameState.currentState == GameState.WAITING) {
            e.isCancelled = true
        }
        /*
        if (GameState.currentState == GameState.INGAME) {
            when (e.block.type) {
                Material.DIAMOND_ORE -> {
                    Stats.addDiamondMined(e.player)
                }
                Material.GOLD_ORE -> {
                    Stats.addGoldMined(e.player)
                }
                Material.IRON_ORE -> {
                    Stats.addIronMined(e.player)
                }
            }
        }*/
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (GameState.currentState == GameState.WAITING) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (GameState.currentState == GameState.WAITING) {
            e.isCancelled = true
        }
        if (e.entity !is Player) {
            return
        }
        if (SpecFeature.instance.isSpec(e.entity as Player)) {
            e.isCancelled = true
        }
    }
}