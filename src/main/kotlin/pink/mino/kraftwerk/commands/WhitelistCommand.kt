package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.events.WhitelistStateChangeEvent
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat

class WhitelistCommand : CommandExecutor {

    fun addWhitelist(p: String) {
        var list = ArrayList<String>()
        if (ConfigFeature.instance.data!!.getList("whitelist.list") == null) {
            ConfigFeature.instance.data!!.set("whitelist.list", list)
        } else {
            list = ConfigFeature.instance.data!!.getList("whitelist.list") as ArrayList<String>
        }
        if (!list.contains(p.lowercase())) {
            list.add(p.lowercase())
        }
        ConfigFeature.instance.data!!.set("whitelist.list", list)
        ConfigFeature.instance.saveData()
    }

    fun removeWhitelist(p: String) {
        var list = ArrayList<String>()
        if (ConfigFeature.instance.data!!.getList("whitelist.list") == null) {
            ConfigFeature.instance.data!!.set("whitelist.list", list)
        } else {
            list = ConfigFeature.instance.data!!.getList("whitelist.list") as ArrayList<String>
        }
        list.remove(p.lowercase())
        ConfigFeature.instance.data!!.set("whitelist.list", list)
        ConfigFeature.instance.saveData()
    }

    fun isWhitelisted(p: String): Boolean {
        var list = ArrayList<String>()
        if (ConfigFeature.instance.data!!.getList("whitelist.list") == null) {
            ConfigFeature.instance.data!!.set("whitelist.list", list)
        } else {
            list = ConfigFeature.instance.data!!.getList("whitelist.list") as ArrayList<String>
        }
        return list.contains(p.lowercase())
    }

    fun getWhitelisted(): ArrayList<String> {
        var list = ArrayList<String>()
        if (ConfigFeature.instance.data!!.getList("whitelist.list") == null) {
            ConfigFeature.instance.data!!.set("whitelist.list", list)
        } else {
            list = ConfigFeature.instance.data!!.getList("whitelist.list") as ArrayList<String>
        }
        return list
    }

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.whitelist")) {
                sender.sendMessage(Chat.colored("${Chat.prefix} You do not have permission to use this command."))
                return false
            }
        }

        if (args.isEmpty()) {
            sender.sendMessage(Chat.line)
            Chat.sendMessage(sender as Player,"${Chat.dash} Invalid usage: ${Chat.secondaryColor}/wl <remove/add> <player>")
            Chat.sendMessage(sender,"${Chat.dash} Invalid usage: ${Chat.secondaryColor}/wl <all/clear/off/on/list>")
            sender.sendMessage(Chat.line)
            return false
        }

        when {
            args[0] == "add" -> {
                if (args.size == 1) {
                    sender.sendMessage("${ChatColor.RED}Usage: /wl add <player>")
                    return false
                }
                val target = Bukkit.getServer().getPlayer(args[1])
                val offline = Bukkit.getServer().getOfflinePlayer(args[1])
                if (target == null) {
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${Chat.secondaryColor}${offline.name}${ChatColor.GRAY} has been added to the whitelist."))
                    addWhitelist(offline.name.lowercase())
                    return true
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${Chat.secondaryColor}${target.name}${ChatColor.GRAY} has been added to the whitelist."))
                addWhitelist(target.name.lowercase())
            }
            args[0] == "list" -> {
                Chat.sendMessage(sender, "${Chat.prefix} Whitelist list: ${Chat.secondaryColor}${ConfigFeature.instance.data!!.getStringList("whitelist.list").joinToString(", ")}&7.")
            }
            args[0] == "remove" -> {
                if (args.size == 1) {
                    sender.sendMessage("${ChatColor.RED}Usage: /wl remove <player>")
                    return false
                }
                val target = Bukkit.getServer().getPlayer(args[1])
                val offline = Bukkit.getServer().getOfflinePlayer(args[1])
                if (target == null) {
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${Chat.secondaryColor}${offline.name}${ChatColor.GRAY} has been removed from the whitelist."))
                    removeWhitelist(offline.name.lowercase())
                    return true
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${Chat.secondaryColor}${target.name}${ChatColor.GRAY} has been removed from the whitelist."))
                removeWhitelist(target.name.lowercase())
            }
            args[0] == "clear" -> {
                ConfigFeature.instance.data!!.set("whitelist.list", ArrayList<String>())
                ConfigFeature.instance.saveData()
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Whitelist cleared."))
            }
            args[0] == "all" -> {
                for (online in Bukkit.getOnlinePlayers()) {
                    addWhitelist(online.name.lowercase())
                    online.sendMessage(Chat.colored("${Chat.prefix} All players whitelisted."))
                }
            }
            args[0] == "off" -> {
                ConfigFeature.instance.data!!.set("whitelist.enabled", false)
                ConfigFeature.instance.saveData()
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} The whitelist is now ${ChatColor.RED}disabled${ChatColor.WHITE}."))
                Bukkit.getPluginManager().callEvent(WhitelistStateChangeEvent(false))
            }
            args[0] == "on" -> {
                ConfigFeature.instance.data!!.set("whitelist.enabled", true)
                ConfigFeature.instance.saveData()
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} The whitelist is now ${ChatColor.GREEN}enabled${ChatColor.WHITE}."))
                Bukkit.getPluginManager().callEvent(WhitelistStateChangeEvent(true))
            }
        }


        return true
    }

}