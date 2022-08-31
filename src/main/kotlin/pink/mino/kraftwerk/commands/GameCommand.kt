package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.MiscUtils

class GameCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command!")
            return true
        }
        if (!sender.hasPermission("uhc.staff.game")) {
            Chat.sendMessage(sender, "&cYou do not have permission to use this command!")
            return true
        }
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game == null) {
            Chat.sendMessage(sender, "&cThere is no game running!")
            return true
        }
        val gui = GuiBuilder().name("&cGame Manager").owner(sender).rows(3)
        var pause = ItemBuilder(Material.TRIPWIRE_HOOK)
            .name("&cPause Game")
            .addLore("&7Click here to temporarily pause the game.")
            .addLore("&7If the game is already paused, this will unpause it.")
            .make()
        var cancel = ItemBuilder(Material.BARRIER)
            .name("&cCancel Game")
            .addLore("&7Click here to cancel the game.")
            .addLore("&7This will stop the game task, but it will not kick any players.")
            .make()
        var timer = ItemBuilder(Material.WATCH)
            .name("&cGame Information")
            .addLore("&7Time Elapsed: &f${MiscUtils.timeToString(JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.timer.toLong())}")
            .addLore("&7Current Event: &f${JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.currentEvent.name}")
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.paused) {
            timer.addLore("&c&lTHE GAME IS PAUSED")
        }
        gui.item(10, pause).onClick runnable@ {
            it.isCancelled = true
            if (JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.paused) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.paused = false
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} The game has been &aunpaused&7."))
            } else {
                JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.paused = true
                Bukkit.broadcastMessage(Chat.colored("${Chat.dash} The game has been &cpaused&7."))
            }
        }
        gui.item(16, cancel).onClick runnable@ {
            it.isCancelled = true
            sender.closeInventory()
            JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.cancel()
            JavaPlugin.getPlugin(Kraftwerk::class.java).game = null
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} The game has been &ccancelled&7."))
        }
        gui.item(13, timer.make()).onClick runnable@ {
            it.isCancelled = true
        }
        Chat.sendMessage(sender, "${Chat.dash} Opening the game manager...")
        sender.openInventory(gui.make())
        object : BukkitRunnable() {
            override fun run() {
                if (JavaPlugin.getPlugin(Kraftwerk::class.java).game == null) {
                    cancel()
                    return
                }
                if (sender.openInventory.title != "&cGame Manager") {
                    cancel()
                    return
                }
                pause = ItemBuilder(Material.TRIPWIRE_HOOK)
                    .name("&cPause Game")
                    .addLore("&7Click here to temporarily pause the game.")
                    .addLore("&7If the game is already paused, this will unpause it.")
                    .make()
                cancel = ItemBuilder(Material.BARRIER)
                    .name("&cCancel Game")
                    .addLore("&7Click here to cancel the game.")
                    .addLore("&7This will stop the game task, but it will not kick any players.")
                    .make()
                timer = ItemBuilder(Material.WATCH)
                    .name("&cGame Information")
                    .addLore("&7Time Elapsed: &f${MiscUtils.timeToString(JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.timer.toLong())}")
                    .addLore("&7Current Event: &f${JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.currentEvent.name}")
                if (JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.paused) {
                    timer.addLore("&c&lTHE GAME IS PAUSED")
                }
                sender.openInventory.topInventory.setItem(10, pause)
                sender.openInventory.topInventory.setItem(16, cancel)
                sender.openInventory.topInventory.setItem(13, timer.make())
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        return true
    }
}