package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.utils.Chat


class ClearInventoryCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.ci")) {
                Chat.sendMessage(sender, "&cYou don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            if (sender is Player) {
                val player = sender
                val inv = player.inventory

                // clear main inventory
                inv.clear()

                // clear armour slots
                inv.armorContents = null

                player.itemOnCursor = ItemStack(Material.AIR)

                val openInventory = player.openInventory
                if (openInventory.type == InventoryType.CRAFTING) {
                    openInventory.topInventory.clear()
                }

                Chat.sendMessage(player, "${Chat.prefix} &7You've cleared your own inventory.")
            } else {
                sender.sendMessage("You can't use this command as you aren't technically a player.")
            }
        } else {
            if (args[0] == "*") {
                for (online in ArrayList(Bukkit.getServer().onlinePlayers)) {
                    val inv = online.inventory

                    // clear main inventory
                    inv.clear()

                    // clear armour slots
                    inv.armorContents = null

                    online.itemOnCursor = ItemStack(Material.AIR)

                    val openInventory = online.openInventory
                    if (openInventory.type == InventoryType.CRAFTING) {
                        openInventory.topInventory.clear()
                    }
                    Chat.sendMessage(online, "${Chat.prefix} &7Your inventory has been cleared by ${Chat.primaryColor}${sender.name}§7.")
                }
                Chat.sendMessage(sender as Player, "${Chat.prefix} &7You've cleared all players' inventories.")
                return true
            } else {
                val target = Bukkit.getServer().getPlayer(args[0])
                if (target == null) {
                    Chat.sendMessage(sender as Player, "${Chat.prefix} &7That player is not online or has never logged onto the server.")
                }
                val inv = target.inventory

                // clear main inventory
                inv.clear()

                // clear armour slots
                inv.armorContents = null

                target.itemOnCursor = ItemStack(Material.AIR)

                val openInventory = target.openInventory
                if (openInventory.type == InventoryType.CRAFTING) {
                    openInventory.topInventory.clear()
                }

                Chat.sendMessage(target, "${Chat.prefix} &7Your inventory has been cleared by ${Chat.primaryColor}${sender.name}&7.")
                Chat.sendMessage(sender as Player, "${Chat.prefix} &7Cleared ${Chat.primaryColor}${target.name}'s&7 inventory.")
                return true
            }
        }

        return true
    }

}