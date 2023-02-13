package pink.mino.kraftwerk.commands

import me.lucko.helper.promise.Promise
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder
import kotlin.math.floor
import kotlin.math.round

class StatsCommand : CommandExecutor {
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

    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        val gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "&4Stats")).owner(sender)
        val target: OfflinePlayer = if (args.isEmpty()) {
            sender
        } else {
            Bukkit.getOfflinePlayer(args[0])
        }
        Promise.start()
            .thenRunSync runnable@ {
                Chat.sendMessage(sender, "${Chat.prefix} &7Loading stats for &f${target.name}&7...")
            }
            .thenApplyAsync {
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.lookupStatsPlayer(target)
            }
            .thenAcceptSync { statsPlayer ->
                val ores = ItemBuilder(Material.DIAMOND_ORE)
                    .name(" &4&lOres")
                    .addLore(" ")
                    .addLore(" &7Diamonds Mined ${Chat.dash} &f${statsPlayer.diamondsMined}")
                    .addLore(" &7Gold Mined ${Chat.dash} &f${statsPlayer.goldMined}")
                    .addLore(" &7Iron Mined ${Chat.dash} &f${statsPlayer.ironMined}")
                    .addLore(" ")
                    .make()
                val general = ItemBuilder(Material.DIAMOND)
                    .name(" &4&lGeneral")
                    .addLore(" ")
                    .addLore(" &7Kills ${Chat.dash} &f${statsPlayer.kills}")
                    .addLore(" &7Deaths ${Chat.dash} &f${statsPlayer.deaths}")
                    .addLore(" ")
                    .addLore(" &7Wins ${Chat.dash} &f${statsPlayer.wins}")
                    .addLore(" &7KDR ${Chat.dash} &f${round((statsPlayer.kills.toDouble() / statsPlayer.deaths.toDouble()))}")
                    .addLore(" &7Games Played ${Chat.dash} &f${statsPlayer.gamesPlayed}")
                    .addLore(" ")
                    .noAttributes()
                    .make()
                val pvp = ItemBuilder(Material.DIAMOND_SWORD)
                    .noAttributes()
                    .name(" &4&lPvP")
                    .addLore(" ")
                    .addLore(" &7Damage Dealt ${Chat.dash } &f${round(statsPlayer.damageDealt)}❤")
                    .addLore(" &7Damage Taken ${Chat.dash } &f${round(statsPlayer.damageTaken)}❤")
                    .addLore(" ")
                    .addLore(" &7Bow Shots ${Chat.dash} &f${statsPlayer.bowShots}")
                    .addLore(" &8&o(${statsPlayer.bowMisses} misses, ${statsPlayer.bowHits} hits)")
                    .addLore(" ")
                    .addLore(" &7Melee Hits ${Chat.dash} &f${statsPlayer.meleeHits}")
                    .addLore(" ")
                    .make()
                val arena = ItemBuilder(Material.IRON_SWORD)
                    .noAttributes()
                    .name(" &4&lArena")
                    .addLore(" ")
                    .addLore(" &7Kills ${Chat.dash} &f${statsPlayer.arenaKills}")
                    .addLore(" &7Deaths ${Chat.dash}&f ${statsPlayer.arenaDeaths}")
                    .addLore(" &7Highest Killstreak ${Chat.dash}&f ${statsPlayer.highestArenaKs}")
                    .addLore(" ")
                    .make()
                val misc = ItemBuilder(Material.WORKBENCH)
                    .name(" &4&lMisc.")
                    .addLore(" ")
                    .addLore(" &7Times Enchanted ${Chat.dash} &f${statsPlayer.timesEnchanted}")
                    .addLore(" &7Times Crafted  ${Chat.dash} &f${statsPlayer.timesCrafted}")
                    .addLore(" &7Nether Travels  ${Chat.dash} &f${statsPlayer.timesNether}")

                    .addLore(" &7Gapples Eaten ${Chat.dash} &f${statsPlayer.gapplesEaten}")
                    .addLore(" &7Gapples Crafted ${Chat.dash} &f${statsPlayer.gapplesEaten}")
                    .addLore(" ")
                    .make()

                val skull = ItemBuilder(Material.SKULL_ITEM)
                    .name("&f${statsPlayer.player.name}")
                    .addLore("&7The statistics for &f${statsPlayer.player.name}&7.")
                    .toSkull()
                    .setOwner(statsPlayer.player.name)
                    .make()

                val staff = ItemBuilder(Material.IRON_BLOCK)
                    .name(" &4&lStaff")
                    .addLore(" ")
                    .addLore(" &7Time Spectated ${Chat.dash} &f${timeToString(round(statsPlayer.timeSpectated.toDouble() / 1000).toLong())} ")
                    .addLore(" ")
                    .make()
                gui.item(0, skull).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(2, general).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(3, misc).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(4, ores).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(5, pvp).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(6, arena).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(7, staff).onClick runnable@ {
                    it.isCancelled = true
                }
                sender.openInventory(gui.make())
            }

        return true
    }

}