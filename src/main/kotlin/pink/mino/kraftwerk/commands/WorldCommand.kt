package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class WorldCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.whitelist")) {
                sender.sendMessage(Chat.colored("${Chat.prefix} You do not have permission to use this command."))
                return false
            }
        }
        val player = sender as Player
        if (args.isEmpty()) {
            Chat.sendMessage(player, Chat.line)
            Chat.sendCenteredMessage(player, "&c&lWorld Help")
            Chat.sendMessage(player, "${Chat.prefix} &f/world tp <world> &8- &7Teleport to the provided world.")
            Chat.sendMessage(player, "${Chat.prefix} &f/world create <name> <env> &8- &7Creates the provided world with the provided arguments.")
            Chat.sendMessage(player, "${Chat.prefix} &f/world delete <world> &8- &7Deletes the provided world.")
            Chat.sendMessage(player, "${Chat.prefix} &f/world list &8- &7List all worlds.")
            Chat.sendMessage(player, Chat.line)
            return false
        } else if (args[0].lowercase() == "list") {
            Chat.sendMessage(player, Chat.line)
            Chat.sendCenteredMessage(player, "&c&lWorld List")
            for (world in Bukkit.getServer().worlds) {
                when (world.environment) {
                    World.Environment.NORMAL -> {
                        Chat.sendMessage(player, "&8• &a${world.name} &8- &f${world.players.size} players")
                    }
                    World.Environment.NETHER -> {
                        Chat.sendMessage(player, "&8• &c${world.name} &8- &f${world.players.size} players")
                    }
                    World.Environment.THE_END -> {
                        Chat.sendMessage(player, "&8• &e${world.name} &8- &f${world.players.size} players")
                    }
                }
            }
            Chat.sendMessage(player, Chat.line)
        } else if (args[0].lowercase() == "tp") {
            if (args.size == 1) {
                Chat.sendMessage(player, "&cYou need to provide a world.")
                return false
            }
            if (Bukkit.getWorld(args[1]) == null) {
                Chat.sendMessage(player, "&cYou need to provide a valid world.")
                return false
            }
            val world = Bukkit.getWorld(args[1])
            player.teleport(world.spawnLocation)
            Chat.sendMessage(player, "${Chat.prefix} Teleported to &c${world.name}&7's spawn.")
        }

        return true
    }

}