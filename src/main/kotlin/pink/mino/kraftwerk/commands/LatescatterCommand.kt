package pink.mino.kraftwerk.commands

import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ScatterFeature
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.StatsHandler
import java.util.*

class LatescatterCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.ls")) {
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "&cYou can't use this command right now.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: &f/ls <player> [teammate]")
            return false
        }
        val player: Player
        val teammate: Player
        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (list == null) list = ArrayList<String>()
        if (args.size == 1) {
            player = Bukkit.getPlayer(args[0])
            player.playSound(player.location, Sound.WOOD_CLICK, 10F, 1F)
            player.health = 20.0
            player.maxHealth = 20.0
            player.isFlying = false
            player.allowFlight = false
            player.foodLevel = 20
            player.saturation = 20F
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
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 1000, true, false))
            ScatterFeature.scatterSolo(player, Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")), SettingsFeature.instance.data!!.getInt("pregen.border"))
            player.inventory.setItem(0, ItemStack(Material.COOKED_BEEF, SettingsFeature.instance.data!!.getInt("game.starterfood")))
            StatsHandler.getStatsPlayer(player).gamesPlayed.plus(1)
            for (scenario in ScenarioHandler.getActiveScenarios()) {
                scenario.givePlayer(player)
            }
            WhitelistCommand().addWhitelist(player.name)
            list.add(player.name)
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &f${player.name}&7 has been late-scattered&7."))
            Chat.sendMessage(player, "${Chat.prefix} You've successfully been added to the game.")
        } else if (args.size == 2) {
            player = Bukkit.getPlayer(args[0])
            teammate = Bukkit.getPlayer(args[1])
            if (TeamsFeature.manager.getTeam(teammate) == null) {
                val oTeams = ArrayList<Team>()
                for (team in TeamsFeature.manager.getTeams()) {
                    if (team.size == 0) {
                        oTeams.add(team)
                    }
                }
                val det = Random().nextInt(oTeams.size)
                SendTeamView(oTeams[det]).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
                oTeams[det].addPlayer(teammate)
                oTeams[det].addPlayer(player)
            } else {
                val team = TeamsFeature.manager.getTeam(teammate)
                team!!.addPlayer(player)
            }
            player.playSound(player.location, Sound.WOOD_CLICK, 10F, 1F)
            player.health = 20.0
            player.maxHealth = 20.0
            player.isFlying = false
            player.allowFlight = false
            player.foodLevel = 20
            player.saturation = 20F
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
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 1000, true, false))
            player.teleport(teammate.location)
            player.inventory.setItem(0, ItemStack(Material.COOKED_BEEF, SettingsFeature.instance.data!!.getInt("game.starterfood")))
            StatsHandler.getStatsPlayer(player).gamesPlayed.plus(1)
            for (scenario in ScenarioHandler.getActiveScenarios()) {
                scenario.givePlayer(player)
            }
            WhitelistCommand().addWhitelist(player.name)
            list.add(player.name)

            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &f${player.name}&7 has been late-scattered to their teammate &f${teammate.name}&7."))
            Chat.sendMessage(player, "${Chat.prefix} You've successfully been added to the game, you've also been teamed with &f${teammate.name}&7")
        }
        SettingsFeature.instance.data!!.set("game.list", list)
        SettingsFeature.instance.saveData()
        return true
    }

}