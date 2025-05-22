package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.PlayerUtils
import java.util.*

class GenieScenario : Scenario(
    "Genie",
    "You are given 3 wishes and you cannot get more. Based on the number of kills you have in the game is what you can wish for. (If you have 0 kills, you can get the following: Golden Apple, Diamond Sword, Anvil. If you have 1 Kill, you can get the following: Player Head, Speed 1 Potion, Strength 1 Potion. If you have 2 Kills, you can get the following: Enchantment Table, Brewing Stand, 5 Diamond Ore. If you have 4 Kills, you can get the following: Instant Health 2 Potion Not Splashable, 128 Bottles of Enchanting, 1 Glowstone Block, 1 Blaze Rod. If you have 5+ Kills, you can get the following: 64 Obsidian, 8 Gold Ingots, 4 Soul Sand, 3 Wither Skeleton Heads.)",
    "genie",
    Material.EXP_BOTTLE
), CommandExecutor {
    val prefix = Chat.colored("&8[${Chat.primaryColor}Genie&8]&7")
    val wishes = hashMapOf<UUID, Int>()

    init {
        JavaPlugin.getPlugin(Kraftwerk::class.java).getCommand("genie").executor = this
    }

    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.isSpec(player)) {
                wishes[player.uniqueId] = 3
                Chat.sendMessage(player, "$prefix You have ${Chat.secondaryColor}3 wishes&7! Use ${Chat.secondaryColor}/genie&7 to redeem them now or wait until you have kills.")
            }
        }
    }

    override fun givePlayer(player: Player) {
        wishes[player.uniqueId] = 3
        Chat.sendMessage(player, "$prefix You have ${Chat.secondaryColor}3 wishes&7! Use ${Chat.secondaryColor}/genie&7 to redeem them now or wait until you have kills.")
    }

    fun calculateRewards(player: Player): ArrayList<ItemStack> {
        val rewards = arrayListOf<ItemStack>()
        if (ConfigFeature.instance.data!!.getInt("game.kills.${player.name}") == null || ConfigFeature.instance.data!!.getInt("game.kills.${player.name}") == 0) {
            rewards.addAll(
                arrayListOf(
                    ItemStack(Material.GOLDEN_APPLE, 1),
                    ItemStack(Material.DIAMOND_SWORD, 1),
                    ItemStack(Material.ANVIL, 1) // 3
                )
            )
        }
        if (ConfigFeature.instance.data!!.getInt("game.kills.${player.name}") >= 1) {
            rewards.addAll(
                arrayListOf(
                    ItemBuilder(Material.SKULL_ITEM)
                        .toSkull()
                        .setOwner(player.name)
                        .make(),
                    Potion(PotionType.SPEED).toItemStack(1),
                    Potion(PotionType.STRENGTH).toItemStack(1) // 3
                )
            )
        }
        if (ConfigFeature.instance.data!!.getInt("game.kills.${player.name}") >= 2) {
            rewards.addAll(
                arrayListOf(
                    ItemStack(Material.ENCHANTMENT_TABLE, 1),
                    ItemStack(Material.BREWING_STAND, 1),
                    ItemStack(Material.DIAMOND_ORE, 5) // 3
                )
            )
        }
        if (ConfigFeature.instance.data!!.getInt("game.kills.${player.name}") >= 4) {
            rewards.addAll(
                arrayListOf(
                    Potion(PotionType.INSTANT_HEAL, 2, false).toItemStack(1),
                    ItemStack(Material.EXP_BOTTLE, 128),
                    ItemStack(Material.GLOWSTONE, 1),
                    ItemStack(Material.BLAZE_ROD, 1) // 3
                )
            )
        }
        if (ConfigFeature.instance.data!!.getInt("game.kills.${player.name}") >= 5) {
            rewards.addAll(
                arrayListOf(
                    ItemStack(Material.OBSIDIAN, 64),
                    ItemStack(Material.GOLD_INGOT, 8),
                    ItemStack(Material.SOUL_SAND, 4),
                    ItemStack(Material.SKULL_ITEM, 1, 1) // 4
                )
            )
        }
        return rewards
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command!")
            return true
        }
        if (!enabled) {
            Chat.sendMessage(sender, "$prefix &cThis scenario is not enabled!")
            return true
        }
        if (wishes[sender.uniqueId] == null) {
            Chat.sendMessage(sender, "$prefix You do not have any wishes!")
            return true
        }
        if (wishes[sender.uniqueId] == 0) {
            Chat.sendMessage(sender, "$prefix You do not have any wishes!")
            return true
        }
        val gui = GuiBuilder().name("${Chat.primaryColor}Genie Menu").rows(2).owner(sender)
        val rewards = calculateRewards(sender)
        for ((index, reward) in rewards.withIndex()) {
            gui.item(index, reward).onClick {
                wishes[sender.uniqueId] = wishes[sender.uniqueId]!! - 1
                PlayerUtils.bulkItems(sender, arrayListOf(reward))
                Chat.sendMessage(sender, "$prefix You have redeemed a wish! You now have ${Chat.secondaryColor}${wishes[sender.uniqueId]}&7 wishes left!")
                sender.closeInventory()
            }
        }
        Chat.sendMessage(sender, "$prefix Opening the Genie menu...")
        sender.openInventory(gui.make())
        return true
    }
}