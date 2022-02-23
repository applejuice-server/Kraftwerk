package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.Stats

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
        val gui = GuiBuilder().rows(3).name(ChatColor.translateAlternateColorCodes('&', "&4Stats"))

        if (args.isEmpty()) {
            val ores = ItemStack(Material.DIAMOND_ORE)
            val oreMeta = ores.itemMeta
            oreMeta.displayName = Chat.colored("&4Ores")
            oreMeta.lore = listOf(
                Chat.colored(Chat.line),
                "",
                Chat.colored("&7Diamonds Mined ${Chat.dash} &c${Stats.getDiamondsMined(sender)}"),
                Chat.colored("&7Gold Mined ${Chat.dash} &c${Stats.getGoldMined(sender)}"),
                Chat.colored("&7Iron Mined ${Chat.dash} &c${Stats.getIronMined(sender)}"),
                "",
                Chat.colored(Chat.line)
            )
            ores.itemMeta = oreMeta

            val general = ItemStack(Material.DIAMOND_SWORD)
            val generalMeta = ores.itemMeta
            generalMeta.displayName = Chat.colored("&4Ores")
            generalMeta.lore = listOf(
                Chat.colored(Chat.line),
                "",
                Chat.colored("&7Kills ${Chat.dash} &c${Stats.getKills(sender)}"),
                Chat.colored("&7Wins ${Chat.dash} &c${Stats.getWins(sender)}"),
                Chat.colored("&7Deaths ${Chat.dash} &c${Stats.getDeaths(sender)}"),
                Chat.colored("&7Games Played ${Chat.dash} &c${Stats.getGamesPlayed(sender)}"),
                "",
                Chat.colored(Chat.line)
            )
            general.itemMeta = generalMeta

            gui.item(12, general).onClick runnable@ {
                it.isCancelled = true
            }
            gui.item(14, ores).onClick runnable@ {
                it.isCancelled = true
            }
            sender.openInventory(gui.make())
        } else {
            val target = Bukkit.getOfflinePlayer(args[0])
            if (target == null) {
                Chat.sendMessage(sender, "&cInvalid player!")
                return false
            }
            val ores = ItemStack(Material.DIAMOND_ORE)
            val oreMeta = ores.itemMeta
            oreMeta.displayName = Chat.colored("&4Ores")
            oreMeta.lore = listOf(
                Chat.colored(Chat.line),
                "",
                Chat.colored("&7Diamonds Mined ${Chat.dash} &c${Stats.getDiamondsMined(target)}"),
                Chat.colored("&7Gold Mined ${Chat.dash} &c${Stats.getGoldMined(target)}"),
                Chat.colored("&7Iron Mined ${Chat.dash} &c${Stats.getIronMined(target)}"),
                "",
                Chat.colored(Chat.line)
            )
            ores.itemMeta = oreMeta

            val general = ItemStack(Material.DIAMOND_SWORD)
            val generalMeta = ores.itemMeta
            generalMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            generalMeta.displayName = Chat.colored("&4Ores")
            generalMeta.lore = listOf(
                Chat.colored(Chat.line),
                "",
                Chat.colored("&7Kills ${Chat.dash} &c${Stats.getKills(target)}"),
                Chat.colored("&7Wins ${Chat.dash} &c${Stats.getWins(target)}"),
                Chat.colored("&7Deaths ${Chat.dash} &c${Stats.getDeaths(target)}"),
                Chat.colored("&7Games Played ${Chat.dash} &c${Stats.getGamesPlayed(target)}"),
                "",
                Chat.colored(Chat.line)
            )
            general.itemMeta = generalMeta

            gui.item(12, general).onClick runnable@ {
                it.isCancelled = true
            }
            gui.item(14, ores).onClick runnable@ {
                it.isCancelled = true
            }
            sender.openInventory(gui.make())
        }
        return true
    }

}