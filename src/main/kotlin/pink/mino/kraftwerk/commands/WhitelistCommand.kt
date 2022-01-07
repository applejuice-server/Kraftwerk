package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class WhitelistCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("uhc.command.whitelist")) {
            sender.sendMessage(Chat.colored("${Chat.prefix} You do not have permission to use this command."))
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage(Chat.line)
            Chat.sendMessage(sender as Player,"${Chat.prefix} Invalid usage: ${ChatColor.WHITE}/wl <remove/add> <player>")
            Chat.sendMessage(sender,"${Chat.prefix} Invalid usage: ${ChatColor.WHITE}/wl <all/clear/off/on>")
            sender.sendMessage(Chat.line)
            return false
        }

        when {
            args[0] == "add" -> {
                if (args[1].isEmpty()) {
                    sender.sendMessage("${ChatColor.RED} Usage: /wl add <player>")
                    return false
                }
                val target = Bukkit.getServer().getPlayer(args[1])
                val offline = Bukkit.getServer().getOfflinePlayer(args[1])
                if (target == null) {
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${ChatColor.WHITE}${offline.name}${ChatColor.GRAY} has been added to the whitelist."))
                    offline.isWhitelisted = true
                    return true
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${ChatColor.WHITE}${target.name}${ChatColor.GRAY} has been added to the whitelist."))
                target.isWhitelisted = true
            }
            args[0] == "remove" -> {
                if (args[1].isEmpty()) {
                    sender.sendMessage("${ChatColor.RED} Usage: /wl remove <player>")
                    return false
                }
                val target = Bukkit.getServer().getPlayer(args[1])
                val offline = Bukkit.getServer().getOfflinePlayer(args[1])
                if (target == null) {
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${ChatColor.WHITE}${offline.name}${ChatColor.GRAY} has been removed from the whitelist."))
                    offline.isWhitelisted = false
                    return true
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${ChatColor.WHITE}${target.name}${ChatColor.GRAY} has been removed from the whitelist."))
                target.isWhitelisted = false
            }
            args[0] == "clear" -> {
                for (whitelisted in Bukkit.getWhitelistedPlayers()) {
                    whitelisted.isWhitelisted = false
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Whitelist cleared."))
            }
            args[0] == "all" -> {
                for (online in Bukkit.getOnlinePlayers()) {
                    online.isWhitelisted = true
                    online.sendMessage(Chat.colored("${Chat.prefix} All players whitelisted."))
                }
            }
            args[0] == "off" -> {
                Bukkit.setWhitelist(false)
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} The whitelist is now ${ChatColor.RED}disabled${ChatColor.WHITE}."))
            }
            args[0] == "on" -> {
                Bukkit.setWhitelist(true)
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} The whitelist is now ${ChatColor.GREEN}enabled${ChatColor.WHITE}."))
            }
        }


        return true
    }

}