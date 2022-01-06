package pink.mino.kraftwerk.commands

import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.HealthChatColorer
import kotlin.math.floor

class HealthCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            val player = sender as Player
            val el: EntityLiving = (player as CraftPlayer).handle
            val health = floor(player.health / 2 * 10 + el.absorptionHearts / 2 * 10)
            val color = HealthChatColorer.returnHealth(health)
            sender.sendMessage("${Chat.prefix} ${ChatColor.WHITE}${player.displayName}${ChatColor.GRAY} is at ${color}${health}%${ChatColor.GRAY}.")
            return true
        } else {
            val target = Bukkit.getServer().getPlayer(args[0])
            if (target == null) {
                sender.sendMessage("${ChatColor.RED}That player is not online or has never logged onto the server.")
            }
            val el: EntityLiving = (target as CraftPlayer).handle
            val health = floor(target.health / 2 * 10 + el.absorptionHearts / 2 * 10)
            val color = HealthChatColorer.returnHealth(health)
            sender.sendMessage("${Chat.prefix} ${ChatColor.WHITE}${target.displayName}${ChatColor.GRAY} is at ${color}${health}%${ChatColor.GRAY}.")
            return true
        }
    }
}