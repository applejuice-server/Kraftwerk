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
        if (!sender.hasPermission("uhc.staff.ci")) {
            Chat.sendMessage(sender as Player, "&cYou don't have permission to use this command.")
            return false
        }
        if (args.isEmpty()) {
            val player = sender as Player
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

            Chat.sendMessage(player, "&7You've cleared your own inventory.")
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
                    Chat.sendMessage(online, "&7Your inventory has been cleared by §c${sender.name}§7.")
                }
                Chat.sendMessage(sender as Player, "&7You've cleared all players' inventories.")
                return true
            } else {
                val target = Bukkit.getServer().getPlayer(args[0])
                if (target == null) {
                    Chat.sendMessage(sender as Player, "&7That player is not online or has never logged onto the server.")
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

                Chat.sendMessage(target, "&7Your inventory has been cleared by &c${sender.name}&7.")
                Chat.sendMessage(sender as Player, "&7Cleared &c${target.name}'s&7 inventory.")
                return true
            }
        }

        return true
    }

}