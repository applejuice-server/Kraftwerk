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
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.StatsHandler

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
            val player = StatsHandler.getStatsPlayer(sender)
            val ores = ItemStack(Material.DIAMOND_ORE)
            val oreMeta = ores.itemMeta
            oreMeta.displayName = Chat.colored("&4Ores")
            oreMeta.lore = listOf(
                Chat.colored(Chat.guiLine),
                "",
                Chat.colored("&7Diamonds Mined ${Chat.dash} &c${player.diamondsMined}"),
                Chat.colored("&7Gold Mined ${Chat.dash} &c${player.goldMined}"),
                Chat.colored("&7Iron Mined ${Chat.dash} &c${player.diamondsMined}"),
                "",
                Chat.colored(Chat.guiLine)
            )
            ores.itemMeta = oreMeta

            val general = ItemStack(Material.DIAMOND_SWORD)
            val generalMeta = general.itemMeta
            generalMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            generalMeta.displayName = Chat.colored("&4General")
            generalMeta.lore = listOf(
                Chat.colored(Chat.guiLine),
                "",
                Chat.colored("&7Kills ${Chat.dash} &c${player.kills}"),
                Chat.colored("&7Wins ${Chat.dash} &c${player.wins}"),
                Chat.colored("&7Deaths ${Chat.dash} &c${player.deaths}"),
                Chat.colored("&7Games Played ${Chat.dash} &c${player.gamesPlayed}"),
                "",
                Chat.colored(Chat.guiLine)
            )
            val misc = ItemBuilder(Material.WATCH)
                .name("&4Misc.")
                .addLore(Chat.guiLine)
                .addLore(" ")
                .addLore("&7Times Enchanted ${Chat.dash} &c${player.timesEnchanted}")
                .addLore("&7Times Crafted  ${Chat.dash} &c${player.timesCrafted}")
                .addLore("&7Gapples Eaten ${Chat.dash} &c${player.gapplesEaten}")
                .addLore(" ")
                .addLore(Chat.guiLine)
                .make()
            general.itemMeta = generalMeta

            gui.item(12, general).onClick runnable@ {
                it.isCancelled = true
            }
            gui.item(13, misc).onClick runnable@ {
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
            val player = StatsHandler.getStatsPlayer(target as Player)
            val ores = ItemStack(Material.DIAMOND_ORE)
            val oreMeta = ores.itemMeta
            oreMeta.displayName = Chat.colored("&4Ores")
            oreMeta.lore = listOf(
                Chat.colored(Chat.guiLine),
                "",
                Chat.colored("&7Diamonds Mined ${Chat.dash} &c${player.diamondsMined}"),
                Chat.colored("&7Gold Mined ${Chat.dash} &c${player.goldMined}"),
                Chat.colored("&7Iron Mined ${Chat.dash} &c${player.ironMined}"),
                "",
                Chat.colored(Chat.guiLine)
            )
            ores.itemMeta = oreMeta

            val general = ItemStack(Material.DIAMOND_SWORD)
            val generalMeta = general.itemMeta
            generalMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            generalMeta.displayName = Chat.colored("&4General")
            generalMeta.lore = listOf(
                Chat.colored(Chat.guiLine),
                "",
                Chat.colored("&7Kills ${Chat.dash} &c${player.kills}"),
                Chat.colored("&7Wins ${Chat.dash} &c${player.wins}"),
                Chat.colored("&7Deaths ${Chat.dash} &c${player.deaths}"),
                Chat.colored("&7Games Played ${Chat.dash} &c${player.gamesPlayed}"),
                "",
                Chat.colored(Chat.guiLine)
            )
            val misc = ItemBuilder(Material.WATCH)
                .name("&4Misc.")
                .addLore(Chat.guiLine)
                .addLore(" ")
                .addLore("&7Times Enchanted ${Chat.dash} &c${player.timesEnchanted}")
                .addLore("&7Times Crafted  ${Chat.dash} &c${player.timesCrafted}")
                .addLore("&7Gapples Eaten ${Chat.dash} &c${player.gapplesEaten}")
                .addLore(" ")
                .addLore(Chat.guiLine)
                .make()
            general.itemMeta = generalMeta

            gui.item(12, general).onClick runnable@ {
                it.isCancelled = true
            }
            gui.item(13, misc).onClick runnable@ {
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