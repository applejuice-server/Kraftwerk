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

    val finalHeal = ConfigFeature.instance.data!!.getInt("game.events.final-heal") * 60 + 30
    val pvp = ((ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.final-heal")) * 60) + 30
    val meetup = ((ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.meetup")) * 60) + 30
    val borderShrink = (ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.borderShrink")) * 60 + 30

    val rawPvP = (ConfigFeature.instance.data!!.getInt("game.events.pvp") * 60) + 30
    val rawMeetup = (ConfigFeature.instance.data!!.getInt("game.events.meetup") * 60) + 30
    val rawBs = (ConfigFeature.instance.data!!.getInt("game.events.borderShrink") * 60) + 30

    var timer = 0
    var currentEvent: Events = Events.PRE_START

    var host = ConfigFeature.instance.data!!.getString("game.host")
    var scenarios = ConfigFeature.instance.data!!.getStringList("matchpost.scenarios")
    var id = ConfigFeature.instance.data!!.getInt("matchpost.id")
    var team = ConfigFeature.instance.data!!.getString("matchpost.team")
    var winners: Any? = null
    var fill = Bukkit.getOnlinePlayers().size

    var startTime: Long = Date().time
    var endTime: Long? = null

    var paused = false
    var meetupHappened = false
    var pvpHappened = false

    var pve = 0

    fun checkWinner(): Boolean {
        val list = ConfigFeature.instance.data!!.getStringList("game.list")
        val teamSize = ConfigFeature.instance.data!!.getInt("game.teamSize")

        if (list.isEmpty()) {
            // No players left, no winner
            Bukkit.getLogger().info("No players left.")
            return false
        }

        if (teamSize <= 1) {
            // Solo mode - last player wins
            if (list.size == 1) {
                val winnerName = list[0]
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "winner ${winnerName}")
                return true
            }
        } else {
            // Team mode - filter teams by whether they still have active players
            val remainingTeams = TeamsFeature.manager.teamMap.entries.filter { (_, players) ->
                players.any { list.contains(it.name) }
            }

            if (remainingTeams.size == 1) {
                val (_, players) = remainingTeams.first()
                for (player in players) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "winner ${player.name}")
                }
                return true
            }
        }
        return false
    }

    private fun displayTimer(player: Player) {
        val preference = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.borderPreference
        val borderText = if (preference == "DIAMETER") {
            "${ConfigFeature.instance.data!!.getInt("pregen.border") * 2}x${ConfigFeature.instance.data!!.getInt("pregen.border") * 2}"
        } else {
            "±${ConfigFeature.instance.data!!.getInt("pregen.border")}"
        }
        when (currentEvent) {
            Events.PRE_START -> {
                ActionBar.sendActionBarMessage(player, "${Chat.primaryColor}Starting in ${Chat.dash} ${Chat.secondaryColor}${timeToString((30 - timer).toLong())}")
            }
            Events.START -> {
                ActionBar.sendActionBarMessage(player, "${Chat.primaryColor}Final Heal is in ${Chat.dash} ${Chat.secondaryColor}${timeToString((finalHeal - timer).toLong())}")
            }
            Events.FINAL_HEAL -> {
                ActionBar.sendActionBarMessage(player, "${Chat.primaryColor}PvP is enabled in ${Chat.dash} ${Chat.secondaryColor}${timeToString((pvp - timer).toLong())} &8| ${Chat.primaryColor}Border shrinks in ${Chat.dash} ${Chat.secondaryColor}${timeToString((borderShrink - timer).toLong())}")
            }
            Events.PVP -> {
                ActionBar.sendActionBarMessage(player, "${Chat.primaryColor}Meetup is in ${Chat.dash} ${Chat.secondaryColor}${timeToString((meetup - timer).toLong())} &8| ${Chat.primaryColor}Border shrinks in ${Chat.dash} ${Chat.secondaryColor}${timeToString((borderShrink - timer).toLong())}")
            }
            Events.BORDER_SHRINK_ONE -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}Meetup is in ${Chat.dash} ${Chat.secondaryColor}${timeToString((meetup - timer).toLong())} &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 300) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}It is now Meetup! Head to 0,0! &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 300) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_TWO -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}Meetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())} &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 600) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}It is now Meetup! Head to 0,0! &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 600) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_THREE -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}Meetup is in ${Chat.dash} ${Chat.secondaryColor}${timeToString((meetup - timer).toLong())} &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 900) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}It is now Meetup! Head to 0,0! &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 900) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_FOUR -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}Meetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())} &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 1200) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}It is now Meetup! Head to 0,0! &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 1200) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_FIVE -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}Meetup is in ${Chat.dash} ${Chat.secondaryColor}${timeToString((meetup - timer).toLong())} &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 1500) - timer.toLong())}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}It is now Meetup! Head to 0,0! &8| &7Border: ${Chat.secondaryColor}${borderText} &8| ${Chat.secondaryColor}${timeToString((borderShrink + 1500) - timer.toLong())}"
                    )
                }
            }
            Events.BORDER_SHRINK_SIX -> {
                if (!meetupHappened) {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}Meetup is in ${Chat.dash} ${Chat.secondaryColor}${timeToString((meetup - timer).toLong())} &8| &7Border: ${Chat.secondaryColor}${borderText}"
                    )
                } else {
                    ActionBar.sendActionBarMessage(
                        player,
                        "${Chat.primaryColor}It is now Meetup! Head to 0,0! &8| &7Border: ${Chat.secondaryColor}${borderText}"
                    )
                }
            }

        }
    }

    override fun run() {
        if (GameState.currentState == GameState.LOBBY) {
            cancel()
        }
        if (checkWinner()) {
            cancel()
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "end")
        }
        var list = ConfigFeature.instance.data!!.getStringList("game.list")
        if (list == null) list = ArrayList<String>()
        when (timer) {
            0 -> {
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} Starting in &f30 seconds&7..."))
            }
            30 -> {
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
                    Chat.sendCenteredMessage(player, "${Chat.primaryColor}&lUHC")
                }
                Bukkit.broadcastMessage(" ")
                for (player in Bukkit.getOnlinePlayers()) {

                    if (ConfigFeature.instance.data!!.getBoolean("game.specials.frbp")) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, pvp * 20, 0, false, false))
                    }
                    if (ConfigFeature.instance.data!!.getBoolean("game.specials.abp")) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, pvp * 20, 0, false, false))
                    }
                    Chat.sendMessage(player, "&7You may &abegin&7! The host for this game is ${Chat.primaryColor}${ConfigFeature.instance.data!!.getString("game.host")}&7!")

                    Chat.sendMessage(player, "&7Scenarios: &f${scenarios.joinToString(", ")}&7")
                    Chat.sendCenteredMessage(player, " ")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 10F, 1F)
                    player.sendTitle(Chat.colored("&a&lGO!"), Chat.colored("&7You may now play the game, do ${Chat.primaryColor}/helpop&7 for help!"))
                    if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                        if (ConfigFeature.instance.data!!.getInt("game.starterfood") > 0) {
                            player.inventory.addItem(ItemStack(Material.COOKED_BEEF, ConfigFeature.instance.data!!.getInt("game.starterfood")))
                        }
                    }
                    if (!ConfigOptionHandler.getOption("statless")!!.enabled) XpFeature().add(player, 30.0)
                }
                for (player in Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 100, true, true))
                }
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onStart()
                }
                if (ConfigOptionHandler.getOption("permaday")!!.enabled) {
                    Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")).time = 6000
                    Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")).setGameRuleValue("doDaylightCycle", false.toString())
                } else {
                    Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")).setGameRuleValue("doDaylightCycle", true.toString())
                }
            }
            31 -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (!ConfigOptionHandler.getOption("statless")!!.enabled && !SpecFeature.instance.isSpec(player)) JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(player)!!.gamesPlayed++
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
                    Chat.sendCenteredMessage(player, "${Chat.primaryColor}&lUHC")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7All players have been healed & fed.")
                    Chat.sendCenteredMessage(player, "&cPvP&7 is enabled in ${Chat.primaryColor}${rawPvP / 60} minutes&7.")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.BURP, 10F, 1F)
                }
            }
            pvp -> {
                currentEvent = Events.PVP
                pvpHappened = true
                for (world in Bukkit.getWorlds()) {
                    world.pvp = true
                }
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onPvP()
                }
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendMessage(player, Chat.line)
                    Chat.sendCenteredMessage(player, "${Chat.primaryColor}&lUHC")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7PvP has been &aenabled&7.")
                    Chat.sendCenteredMessage(player, "${Chat.primaryColor}Meetup&7 will start in ${Chat.primaryColor}${rawMeetup / 60} minutes&7.")
                    Chat.sendCenteredMessage(player, "&7The border will begin ${Chat.primaryColor}shrinking&7 in ${Chat.primaryColor}${rawBs / 60} minutes&7.")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.ANVIL_LAND, 10F, 1F)
                    if (!ConfigOptionHandler.getOption("statless")!!.enabled) XpFeature().add(player, 20.0)
                    if (ConfigFeature.instance.data!!.getBoolean("game.specials.frbp")) {
                        if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE)
                        }
                    }
                    if (ConfigFeature.instance.data!!.getBoolean("game.specials.abp")) {
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
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendCenteredMessage(player, "${Chat.primaryColor}&lUHC")
                    Chat.sendMessage(player, " ")
                    if (ScenarioHandler.getScenario("bigcrack")!!.enabled) {
                        Chat.sendCenteredMessage(player, "&7The border will start shrinking until it's at ${Chat.secondaryColor}150x150 (±75)&7!")
                    } else {
                        Chat.sendCenteredMessage(player, "&7The border will start shrinking until it's at ${Chat.secondaryColor}50x50 (±25)&7!")
                    }
                }
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                if (ConfigFeature.instance.data!!.getInt("pregen.border") == 750) {
                    UHCFeature().scheduleShrink(500)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 500) {
                    UHCFeature().scheduleShrink(250)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 1000) {
                    UHCFeature().scheduleShrink(750)
                } else {
                    UHCFeature().scheduleShrink(750)
                }
            }
            meetup + 1 -> {
                if (ConfigOptionHandler.getOption("permadayatmeetup")!!.enabled) {
                    val world = Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world"))
                    world.time = 6000
                    world.setGameRuleValue("doDaylightCycle", "false")
                }
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendCenteredMessage(player, "${Chat.primaryColor}&lUHC")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7It's now ${Chat.primaryColor}Meetup&7! Head to &a0,0&7!")
                    if (!ConfigOptionHandler.getOption("statless")!!.enabled) XpFeature().add(player, 25.0)
                }
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onMeetup()
                }
                ConfigFeature.instance.data!!.set("game.nether.nether", false)
                ConfigFeature.instance.saveData()
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                meetupHappened = true
            }
            meetup + 5 -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.world.name.endsWith("_nether")) {
                        player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 10F, 1F)
                        player.teleport(portalLocations[player.uniqueId]!!)
                        Chat.sendMessage(player, "${Chat.prefix} You've been teleported to your previous portal location as it's meetup.")
                    }
                }
            }
            borderShrink + 300 -> {
                if (ConfigFeature.instance.data!!.getInt("pregen.border") == 750) {
                    UHCFeature().scheduleShrink(500)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 500) {
                    UHCFeature().scheduleShrink(250)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 250) {
                    UHCFeature().scheduleShrink(100)
                } else {
                    UHCFeature().scheduleShrink(250)
                }
                currentEvent = Events.BORDER_SHRINK_TWO
            }
            borderShrink + 600 -> {
                if (ConfigFeature.instance.data!!.getInt("pregen.border") == 500) {
                    UHCFeature().scheduleShrink(250)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 250) {
                    UHCFeature().scheduleShrink(100)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 100) {
                    UHCFeature().scheduleShrink(75)
                } else {
                    UHCFeature().scheduleShrink(100)
                }
                currentEvent = Events.BORDER_SHRINK_THREE
            }
            borderShrink + 900 -> {
                if (ConfigFeature.instance.data!!.getInt("pregen.border") == 250) {
                    UHCFeature().scheduleShrink(100)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 100) {
                    UHCFeature().scheduleShrink(75)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 75) {
                    UHCFeature().scheduleShrink(50)
                } else {
                    UHCFeature().scheduleShrink(75)
                }
                currentEvent = Events.BORDER_SHRINK_FOUR
            }
            borderShrink + 1200 -> {
                if (ScenarioHandler.getScenario("bigcrack")!!.enabled) {
                    return
                }
                if (ConfigFeature.instance.data!!.getInt("pregen.border") == 100) {
                    UHCFeature().scheduleShrink(75)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 75) {
                    UHCFeature().scheduleShrink(50)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 50) {
                    UHCFeature().scheduleShrink(25)
                    return
                } else {
                    UHCFeature().scheduleShrink(50)
                }
                currentEvent = Events.BORDER_SHRINK_FIVE
            }
            borderShrink + 1500 -> {
                if (ScenarioHandler.getScenario("bigcrack")!!.enabled) {
                    return
                }
                if (ConfigFeature.instance.data!!.getInt("pregen.border") == 75) {
                    UHCFeature().scheduleShrink(50)
                } else if (ConfigFeature.instance.data!!.getInt("pregen.border") == 50) {
                    UHCFeature().scheduleShrink(25)
                } else {
                    UHCFeature().scheduleShrink(25)
                }
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
        if (e.player.world.name == ConfigFeature.instance.data!!.getString("pregen.world")) JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.portalLocations[e.player.uniqueId] = e.player.location
    }

    fun start(mode: String) {
        GameState.setState(GameState.WAITING)
        Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")).time = 1000
        Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")).setGameRuleValue("doDaylightCycle", false.toString())
        for (world in Bukkit.getWorlds()) {
            world.pvp = false
        }
        if (mode == "ffa") {
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Starting a ${Chat.primaryColor}FFA&7 UHC game... now freezing players."))
        } else if (mode == "teams") {
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Starting a ${Chat.primaryColor}Teams&7 UHC game... now freezing players."))
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                    if (TeamsFeature.manager.getTeam(player) == null) {
                        val team = TeamsFeature.manager.createTeam(player)
                        SendTeamView(team).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
                    }
                }
            }
        }
        var list = ConfigFeature.instance.data!!.getStringList("game.list")
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
        ConfigFeature.instance.data!!.set("game.list", list)
        ConfigFeature.instance.saveData()
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl all")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl on")

        val teams = mode != "ffa"
        val world = Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world"))
        val radius = ConfigFeature.instance.data!!.getInt("pregen.border")

        if (teams) {
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Attempting to start ${Chat.primaryColor}team &7scatter..."))
        } else {
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Attempting to start ${Chat.primaryColor}solo &7scatter..."))
        }
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Standby, this might take a while."))
        JavaPlugin.getPlugin(Kraftwerk::class.java).scattering = true
        object: BukkitRunnable() {
            override fun run() {
                val loc: List<Location> = ScatterUtils().getScatterLocations(world, radius, WhitelistCommand().getWhitelisted().size)
                if (teams) {
                    for ((index, team) in TeamsFeature.manager.getTeams().withIndex()) {
                        for (player in team.entries) {
                            JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs[player] = loc[index]
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
                        JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs[online] = loc[index]
                    }
                } else {
                    for ((index, online) in WhitelistCommand().getWhitelisted().withIndex()) {
                        val player = Bukkit.getOfflinePlayer(online)
                        if (SpecFeature.instance.getSpecs().contains(player.name)) {
                            continue
                        }
                        JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs[online] = loc[index]
                    }
                }
            }
        }.runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), 30L)

        object: BukkitRunnable() {
            override fun run() {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} All locations ${Chat.primaryColor}found&7, starting to load chunks..."))

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
                            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} All chunks ${Chat.primaryColor}loaded&7, starting to scatter players..."))

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
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scheduled Scatter for ${Chat.secondaryColor}${names[i]} &8(${Chat.primaryColor}${i + 1}&8/${Chat.primaryColor}${names.size}&8)"))
                                            } else {
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scheduled Scatter for ${team.prefix}${names[i]} &8(${Chat.primaryColor}${i + 1}&8/${Chat.primaryColor}${names.size}&8)"))
                                            }
                                        } else {
                                            val scatterP = scatter as Player
                                            scatterP.teleport(JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs[names[i]])
                                            scatterP.gameMode = GameMode.SURVIVAL
                                            scatterP.isFlying = false
                                            scatterP.allowFlight = false
                                            val team = TeamsFeature.manager.getTeam(Bukkit.getOfflinePlayer(names[i]))
                                            if (team == null) {
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scattered ${Chat.secondaryColor}${scatterP.name} &8(${Chat.primaryColor}${i + 1}&8/${Chat.primaryColor}${names.size}&8)"))
                                            } else {
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Scattered ${team.prefix}${scatterP.name} &8(${Chat.primaryColor}${i + 1}&8/${Chat.primaryColor}${names.size}&8)"))
                                            }
                                            JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs.remove(names[i])
                                            freeze()
                                            list.add(names[i])
                                        }
                                        i++
                                    } else {
                                        JavaPlugin.getPlugin(Kraftwerk::class.java).scattering = false
                                        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &7Successfully scattered all players!"))
                                        ConfigFeature.instance.data!!.set("game.list", list)
                                        ConfigFeature.instance.saveData()
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
        Bukkit.getScheduler().runTaskLater(Kraftwerk.instance, Runnable@{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "border $newBorder")
        }, 20 * 10L)
        for (player in Bukkit.getOnlinePlayers()) {
            val preference = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.borderPreference
            val borderText = if (preference == "DIAMETER") {
                "${newBorder*2}x${newBorder*2}"
            } else {
                "±${newBorder}"
            }
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f10s&7.")
                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                    if (!insideBorder(player, newBorder)) {
                        player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                        player.playSound(player.location, Sound.CLICK, 1f, 1f)
                    }
                }
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f9s&7.")
                    if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                        if (!insideBorder(player, newBorder)) {
                            player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                            player.playSound(player.location, Sound.CLICK, 1f, 1f)
                        }
                    }
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f8s&7.")
                        if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                            if (!insideBorder(player, newBorder)) {
                                player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                                player.playSound(player.location, Sound.CLICK, 1f, 1f)
                            }
                        }
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f7s&7.")
                            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                                if (!insideBorder(player, newBorder)) {
                                    player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                                    player.playSound(player.location, Sound.CLICK, 1f, 1f)
                                }
                            }
                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f6s&7.")
                                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                                    if (!insideBorder(player, newBorder)) {
                                        player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                                        player.playSound(player.location, Sound.CLICK, 1f, 1f)
                                    }
                                }
                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                    Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f5s&7.")
                                    if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                                        if (!insideBorder(player, newBorder)) {
                                            player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                                            player.playSound(player.location, Sound.CLICK, 1f, 1f)
                                        }
                                    }
                                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                        Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f4s&7.")
                                        if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                                            if (!insideBorder(player, newBorder)) {
                                                player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                                                player.playSound(player.location, Sound.CLICK, 1f, 1f)
                                            }
                                        }
                                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                            Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f3s&7.")
                                            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                                                if (!insideBorder(player, newBorder)) {
                                                    player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                                                    player.playSound(player.location, Sound.CLICK, 1f, 1f)
                                                }
                                            }
                                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f2s&7.")
                                                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                                                    if (!insideBorder(player, newBorder)) {
                                                        player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                                                        player.playSound(player.location, Sound.CLICK, 1f, 1f)
                                                    }
                                                }
                                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                    Chat.sendMessage(player, "${Chat.prefix} Shrinking to ${Chat.secondaryColor}${borderText}&7 in &f1s&7.")
                                                    if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                                                        if (!insideBorder(player, newBorder)) {
                                                            player.sendTitle(Chat.colored("&4 ! CAUTION ! "), Chat.colored("&7You are outside the border, you will be shrunk!"))
                                                            player.playSound(player.location, Sound.CLICK, 1f, 1f)
                                                        }
                                                    }
                                                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                        if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                                                            if (!insideBorder(player, newBorder)) {
                                                                player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 160, 10, true, false))
                                                                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 10, true, false))
                                                                Chat.sendMessage(player, "${Chat.prefix} You have gained ${Chat.secondaryColor}8 seconds&7 of ${Chat.secondaryColor}Resistance X&7 as you are outside the border.")
                                                            }
                                                        }
                                                        Chat.sendMessage(player, Chat.line)
                                                        Chat.sendMessage(player, "${Chat.prefix} The border has shrunken to ${Chat.secondaryColor}${borderText}&7.")
                                                        if (ScenarioHandler.getScenario("bigcrack")!!.enabled) {
                                                            if (newBorder != 75) {
                                                                Chat.sendMessage(player, "${Chat.prefix} Next border shrink in ${Chat.secondaryColor}5 minutes.")
                                                            }
                                                        } else {
                                                            if (newBorder != 25) {
                                                                Chat.sendMessage(player, "${Chat.prefix} Next border shrink in ${Chat.secondaryColor}5 minutes.")
                                                            }
                                                        }
                                                        Chat.sendMessage(player, Chat.line)
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