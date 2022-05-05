package pink.mino.kraftwerk.commands

import com.wimbli.WorldBorder.Config
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.PregenActionBarFeature
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.BlockUtil
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder

enum class PregenerationGenerationTypes {
    NONE,
    SPAWNERS_GALORE,
    CITY_WORLD
}

open class PregenConfig(val player: OfflinePlayer, val name: String) {
    var type = World.Environment.NORMAL
    var generator = PregenerationGenerationTypes.NONE
    var border: Int = 1000
    var clearTrees: Boolean = true
    var clearWater: Boolean = true
}

class PregenConfigHandler {
    companion object {
        private val configs = hashMapOf<OfflinePlayer, PregenConfig>()

        fun addConfig(player: OfflinePlayer, config: PregenConfig) : PregenConfig {
            if (configs[player] == null) configs[player] = config
            print("Added pregeneration configuration for ${player.name}.")
            return configs[player]!!
        }

        fun removeConfig(player: OfflinePlayer) {
            if (configs[player] != null) configs.remove(player)
            print("Removed pregeneration configuration for ${player.name}.")
        }

        fun getConfig(player: OfflinePlayer) : PregenConfig? {
            return configs[player]
        }
    }
}

class PregenCommand : CommandExecutor {
    val blacklistNames: ArrayList<String> = arrayListOf("world", "world_nether", "world_the_end", "Spawn", "Arena")
    fun createWorld(pregenConfig: PregenConfig) {
        if (Bukkit.getWorld(pregenConfig.name) != null) {
            Bukkit.getServer().unloadWorld(pregenConfig.name, true)
            for (file in Bukkit.getServer().worldContainer.listFiles()!!) {
                if (file.name == pregenConfig.name) {
                    file.delete()
                    print("Deleted world file for ${pregenConfig.name}.")
                }
            }
        }
        if (pregenConfig.player.isOnline) Chat.sendMessage(pregenConfig.player as Player, "&7Creating world &8'&f${pregenConfig.name}&8'...")

        val wc = WorldCreator(pregenConfig.name)
        wc.environment(pregenConfig.type)
        wc.type(WorldType.NORMAL)

        if (pregenConfig.generator == PregenerationGenerationTypes.SPAWNERS_GALORE) {
            var defaultSettings = "{\"useMonuments\":false,\"graniteSize\":1,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":0,\"dioriteSize\":1,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":0,\"andesiteSize\":1,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":0"
            defaultSettings += ",\"dungeonChance\":100}"
            wc.generatorSettings(defaultSettings)
        } else if (pregenConfig.generator == PregenerationGenerationTypes.CITY_WORLD) {
            wc.generator("CityWorld")
        }
        val world = wc.createWorld()
        print("Created world ${pregenConfig.name}.")
        SettingsFeature.instance.data!!.set("pregen.world", world.name)

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            val border = Bukkit.getWorld(pregenConfig.name).worldBorder
            border.size = pregenConfig.border.toDouble() * 2
            border.setCenter(0.0, 0.0)

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "wb ${pregenConfig.border} clear"
            )
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "wb shape rectangular"
            )
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "wb ${pregenConfig.name} setcorners ${pregenConfig.border} ${pregenConfig.border} -${pregenConfig.border} -${pregenConfig.border}"
            )
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "wb ${pregenConfig.name} fill 200"
            )
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "wb fill confirm"
            )
        }, 5L)

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "${Chat.prefix} &7Pregeneration started in &8'&f${pregenConfig.name}&8'&7."))
        PregenActionBarFeature().runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        val blocks = BlockUtil().getBlocks(Bukkit.getWorld(pregenConfig.name).spawnLocation.block, 100)
        if (blocks != null) {
            for (block in blocks) {
                if (pregenConfig.clearTrees) {
                    if (block.type == Material.LEAVES || block.type == Material.LEAVES_2 || block.type == Material.LOG || block.type == Material.LOG_2) {
                        block.type = Material.AIR
                    }
                }
                if (pregenConfig.clearWater) {
                    if (block.type == Material.WATER || block.type == Material.STATIONARY_WATER) {
                        block.type = Material.STAINED_GLASS
                        block.data = 3.toByte()
                    }
                }
            }
        }
        var list = SettingsFeature.instance.data!!.getStringList("world.list")
        if (list == null) list = ArrayList<String>()
        list.add(pregenConfig.name)
        PregenConfigHandler.removeConfig(pregenConfig.player)
        SettingsFeature.instance.data!!.set("world.list", list)
        SettingsFeature.instance.saveData()
        if (pregenConfig.player.isOnline) Chat.sendMessage(pregenConfig.player as Player, "&7Your world has been set as the default UHC world, to change this, use &f/w worlds&7.")

    }

    override fun onCommand(sender: CommandSender, command: Command, label: String?, args: Array<String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.pregen")) {
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} &7Usage: &c/pregen <name>&7.")
            return false
        } else {
            if (blacklistNames.contains(args[0].lowercase())) {
                Chat.sendMessage(sender, "&cYou cannot use that as a world name.")
                return false
            }
            if (args[0] == "cancel") {
                if (Config.fillTask.valid()) {
                    Chat.sendMessage(sender, "${Chat.prefix} Cancelling the pregeneration task.")
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "wb fill cancel"
                    )
                } else {
                    Chat.sendMessage(sender, "${Chat.prefix} There is no valid pregeneration task running.")
                }
                return true
            } else {
                val gui = GuiBuilder().name("&4Pregeneration Config").rows(1)
                Chat.sendMessage(sender, "${Chat.prefix} &7Opening pregeneration config for &7'&f${args[0]}&7'...")
                val player = (sender as Player)
                val pregenConfig = PregenConfigHandler.addConfig(player, PregenConfig(player, args[0]))
                val config = ItemBuilder(Material.GRASS)
                    .name("&cConfiguration")
                    .addLore(Chat.guiLine)
                    .addLore("&7Name: '&c${pregenConfig.name}&7'")
                    .addLore("&7Type: &c${pregenConfig.type.name.uppercase()}")
                    .addLore("&7Generator: &c${pregenConfig.generator.name.uppercase()}")
                    .addLore(" ")
                    .addLore("&7Border: &c±${pregenConfig.border}")
                    .addLore(" ")
                    .addLore("&7Clear Water: &c${if (pregenConfig.clearWater) "&aEnabled" else "&cDisabled"}")
                    .addLore("&7Clear Trees: &c${if (pregenConfig.clearTrees) "&aEnabled" else "&cDisabled"}")
                    .addLore(Chat.guiLine)
                    .make()
                val submit = ItemBuilder(Material.EMERALD)
                    .name("&aCreate")
                    .addLore("&7Submit your configuration & generate")
                    .addLore("&7a new world based on its settings.")
                    .make()
                val changeGeneration = ItemBuilder(Material.REDSTONE)
                    .name("&cChange Generation")
                    .addLore("&7Change the generation type of this world.")
                    .make()
                val changeBorder = ItemBuilder(Material.IRON_INGOT)
                    .name("&cChange Border")
                    .addLore("&7Change the border size of this world.")
                    .make()
                val changeVarious = ItemBuilder(Material.PAPER)
                    .name("&cChange Settings")
                    .addLore("&7Change various settings of this world.")
                    .make()
                gui.item(3, changeGeneration).onClick {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "ep generation")
                    gui.close()
                }
                gui.item(4, changeBorder).onClick {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "ep border")
                    gui.close()
                }
                gui.item(5, changeVarious).onClick {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "ep settings")
                    gui.close()
                }
                gui.item(8, submit).onClick {
                    it.isCancelled = true
                    createWorld(pregenConfig)
                    gui.close()
                    sender.closeInventory()
                }
                gui.item(0, config).onClick {
                    it.isCancelled = true
                    gui.close()
                }
                sender.openInventory(gui.make())
            }
        }
        return true
    }

}