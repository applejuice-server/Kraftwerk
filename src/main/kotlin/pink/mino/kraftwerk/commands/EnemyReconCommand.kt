package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.EnemyReconScenario
import pink.mino.kraftwerk.utils.*
import kotlin.math.round
import kotlin.random.Random

class EnemyReconCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("enemyrecon"))) {
            Chat.sendMessage(sender, "${Chat.prefix} Enemy Recon isn't enabled.")
            return false
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "${Chat.prefix} Enemy Recon isn't available at this time.")
            return false
        }
        if (sender !is Player) {
            sender.sendMessage("You can't use this command.")
            return false
        }
        val reports = EnemyReconScenario.instance.recons[sender.uniqueId]
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Use &f/er <player>&7 to view a recon report on that player, there is a 30% chance you'll be caught by doing this however.")
            Chat.sendMessage(sender, "${Chat.prefix} You have &f${reports}&7 reports left.")
            return true
        }
        if (reports == 0) {
            Chat.sendMessage(sender, "${Chat.prefix} You have no reports left.")
            return false
        }
        val target = Bukkit.getPlayer(args[0])
        if (target == null) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid player.")
            return false
        }
        val chance = Random.nextInt(100)
        if (chance < 30) {
            Bukkit.broadcastMessage(Chat.colored("${EnemyReconScenario.instance.prefix} &f${sender.name}&7 has been caught spying on &f${target.name}&7."))
        }
        EnemyReconScenario.instance.recons[sender.uniqueId] = EnemyReconScenario.instance.recons[sender.uniqueId]!! - 1
        val gui = GuiBuilder().rows(5).name(ChatColor.translateAlternateColorCodes('&', "${target.name}'s Inventory"))
        sender.openInventory(gui.make())
        for ((index, item) in target.inventory.contents.withIndex()) {
            if (item == null) {
                sender.openInventory.topInventory.setItem(index, ItemStack(Material.AIR))
            } else {
                sender.openInventory.topInventory.setItem(index, item)
            }
        }

        if (target.inventory.helmet != null) sender.openInventory.topInventory.setItem(38, target.inventory.helmet)
        if (target.inventory.chestplate != null) sender.openInventory.topInventory.setItem(39, target.inventory.chestplate)
        if (target.inventory.leggings != null) sender.openInventory.topInventory.setItem(41, target.inventory.leggings)
        if (target.inventory.boots != null) sender.openInventory.topInventory.setItem(42, target.inventory.boots)

        val info = ItemBuilder(Material.BOOK)
            .name("&cPlayer Info")
            .addLore(" ")
            .addLore("&cStatistics: ")
            .addLore(" ${Chat.dot} Health ${Chat.dash} ${PlayerUtils.getHealth(target)}")
            .addLore(" ${Chat.dot} Hunger ${Chat.dash} &c${target.foodLevel / 2}")
            .addLore(" ${Chat.dot} XP Level ${Chat.dash} &c${target.level} &8(&c${round(target.exp * 100)}%&8)")
            .addLore(" ${Chat.dot} Location ${Chat.dash} &c${target.location.blockX}, ${target.location.blockY}, ${target.location.blockZ}")
            .addLore(" ")
            .addLore("&cPotion Effects: ")
        if (target.activePotionEffects.isEmpty()) {
            info.addLore(" ${Chat.dot} &cNone.")
        } else {
            for (eff in target.activePotionEffects) {
                info.addLore(" ${Chat.dot} &c${InvseeUtils().getPotionName(eff.type).uppercase()} ${InvseeUtils().integerToRoman(eff.amplifier + 1)} &8(&c${InvseeUtils().potionDurationToString(eff.duration / 20)}&8)")
            }
        }
        info.addLore(" ")
        val actualBook = info.make()
        sender.openInventory.topInventory.setItem(40, actualBook)
        return true
    }

}