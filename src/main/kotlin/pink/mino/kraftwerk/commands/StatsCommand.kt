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
import kotlin.math.round

class StatsCommand : CommandExecutor {
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
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.lookupStatsPlayer(target)!!
            }
            .thenAcceptSync { statsPlayer ->
                val ores = ItemBuilder(Material.DIAMOND_ORE)
                    .name("&4Ores")
                    .addLore(Chat.guiLine)
                    .addLore(" ")
                    .addLore("&7Diamonds Mined ${Chat.dash} &c${statsPlayer.diamondsMined}")
                    .addLore("&7Gold Mined ${Chat.dash} &c${statsPlayer.goldMined}")
                    .addLore("&7Iron Mined ${Chat.dash} &c${statsPlayer.ironMined}")
                    .addLore(" ")
                    .addLore(Chat.guiLine)
                    .make()
                val general = ItemBuilder(Material.DIAMOND_SWORD)
                    .name("&4General")
                    .addLore(Chat.guiLine)
                    .addLore(" ")
                    .addLore("&7Kills ${Chat.dash} &c${statsPlayer.kills}")
                    .addLore("&7Deaths ${Chat.dash} &c${statsPlayer.deaths}")
                    .addLore("&7Wins ${Chat.dash} &c${statsPlayer.wins}")
                    .addLore("&7KDR ${Chat.dash} &c${round((statsPlayer.kills.toDouble() / statsPlayer.deaths.toDouble()))}")
                    .addLore("&7Games Played ${Chat.dash} &c${statsPlayer.gamesPlayed}")
                    .addLore(" ")
                    .addLore(Chat.guiLine)
                    .noAttributes()
                    .make()
                val misc = ItemBuilder(Material.WATCH)
                    .name("&4Misc.")
                    .addLore(Chat.guiLine)
                    .addLore(" ")
                    .addLore("&7Times Enchanted ${Chat.dash} &c${statsPlayer.timesEnchanted}")
                    .addLore("&7Times Crafted  ${Chat.dash} &c${statsPlayer.timesCrafted}")
                    .addLore("&7Gapples Eaten ${Chat.dash} &c${statsPlayer.gapplesEaten}")
                    .addLore(" ")
                    .addLore(Chat.guiLine)
                    .make()

                val skull = ItemBuilder(Material.SKULL_ITEM)
                    .name("&f${target.name}")
                    .addLore("&7The statistics for &f${target.name}&7.")
                    .toSkull()
                    .setOwner(target.name)
                    .make()

                gui.item(0, skull).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(3, general).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(4, misc).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(5, ores).onClick runnable@ {
                    it.isCancelled = true
                }
                sender.openInventory(gui.make())
            }

        return true
    }

}