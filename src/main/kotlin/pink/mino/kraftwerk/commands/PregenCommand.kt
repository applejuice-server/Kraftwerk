package pink.mino.kraftwerk.commands

import com.wimbli.WorldBorder.Config
import me.lucko.helper.utils.Log
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.BlockUtil
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

enum class PregenerationGenerationTypes {
    NONE,
    CITY_WORLD
}

open class PregenConfig(val player: OfflinePlayer, val name: String) {
    var type = World.Environment.NORMAL
    var generator = PregenerationGenerationTypes.NONE
    var border: Int = 1000
    var clearTrees: Boolean = true
    var clearWater: Boolean = true
    var diamondore: Int = 0
    var goldore: Int = 0
    var canerate: Int = 25
    var oresOutsideCaves: Boolean = true
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
                if (file.name.lowercase() == pregenConfig.name.lowercase()) {
                    Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach { it.delete() }
                    file.delete()
                    Log.info("Deleted world file for ${pregenConfig.name}.")
                }
            }
        }
        if (pregenConfig.player.isOnline) Chat.sendMessage(pregenConfig.player as Player, "${Chat.prefix} &7Creating world &8'${Chat.secondaryColor}${pregenConfig.name}&8'...")

        val wc = WorldCreator(pregenConfig.name)
        wc.environment(pregenConfig.type)
        if (pregenConfig.type === World.Environment.NETHER) {
            ConfigFeature.instance.data!!.set("game.nether.nether", true)
            ConfigFeature.instance.saveData()
        }
        wc.type(WorldType.NORMAL)

        if (pregenConfig.generator == PregenerationGenerationTypes.CITY_WORLD) {
            wc.generator("CityWorld")
        }
        val world = wc.createWorld()
        world.difficulty = Difficulty.HARD
        Log.info("Created world ${pregenConfig.name}.")
        if (pregenConfig.type != World.Environment.NETHER && pregenConfig.type != World.Environment.THE_END) ConfigFeature.instance.data!!.set("pregen.world", world.name)
        ConfigFeature.instance.worlds!!.set("${world.name}.name", world.name)
        ConfigFeature.instance.worlds!!.set("${world.name}.madeby", pregenConfig.player.uniqueId.toString())
        ConfigFeature.instance.worlds!!.set("${world.name}.date", Date().toString())
        ConfigFeature.instance.worlds!!.set("${world.name}.type", pregenConfig.type.toString().uppercase())
        ConfigFeature.instance.worlds!!.set("${world.name}.orerates.gold", pregenConfig.goldore)
        ConfigFeature.instance.worlds!!.set("${world.name}.orerates.diamond", pregenConfig.diamondore)
        ConfigFeature.instance.worlds!!.set("${world.name}.canerate", pregenConfig.canerate)
        ConfigFeature.instance.worlds!!.set("${world.name}.oresOutsideCaves", pregenConfig.oresOutsideCaves)
        ConfigFeature.instance.saveWorlds()

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb shape rectangular"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb ${pregenConfig.name} setcorners ${pregenConfig.border} ${pregenConfig.border} -${pregenConfig.border} -${pregenConfig.border}"
        )

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            val border = Bukkit.getWorld(pregenConfig.name).worldBorder
            border.size = pregenConfig.border.toDouble() * 2
            border.setCenter(0.0, 0.0)

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "wb ${pregenConfig.name} fill 250 208 true"
            )
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "wb fill confirm"
            )
        }, 5L)

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "${Chat.prefix} &7Pregeneration started in &8'${Chat.secondaryColor}${pregenConfig.name}&8'&7."))
        //PregenActionBarFeature().runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
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
        var list = ConfigFeature.instance.data!!.getStringList("world.list")
        if (list == null) list = ArrayList<String>()
        list.add(pregenConfig.name)
        PregenConfigHandler.removeConfig(pregenConfig.player)
        ConfigFeature.instance.data!!.set("pregen.border", pregenConfig.border)
        ConfigFeature.instance.data!!.set("world.list", list)
        ConfigFeature.instance.saveData()
        if (pregenConfig.player.isOnline) Chat.sendMessage(pregenConfig.player as Player, "${Chat.prefix} &7Your world has been set as the default UHC world, to change this, use ${Chat.secondaryColor}/w worlds&7.")

    }

    override fun onCommand(sender: CommandSender, command: Command, label: String?, args: Array<String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.pregen")) {
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} &7Usage: ${Chat.primaryColor}/pregen <name>&7.")
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
            } else if (args[0] == "pause") {
                if (Config.fillTask.valid()) {
                    if (Config.fillTask.isPaused) {
                        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &a&oResuming&7 the pregeneration task."))
                        Config.fillTask.pause(false)
                    } else {
                        Chat.sendMessage(sender, "${Chat.prefix} &c&lPausing&7 the pregeneration task.")
                        Config.fillTask.pause(true)
                    }
                } else {
                    Chat.sendMessage(sender, "${Chat.prefix} There is no valid pregeneration task running.")
                }
            } else {
                val gui = GuiBuilder().name("&4Pregeneration Config").rows(1).owner(sender as Player)
                Chat.sendMessage(sender, "${Chat.prefix} &7Opening pregeneration config for &7'${Chat.secondaryColor}${args[0]}&7'...")
                val player = sender
                val pregenConfig = PregenConfigHandler.addConfig(player, PregenConfig(player, args[0]))
                val config = ItemBuilder(Material.GRASS)
                    .name("${Chat.primaryColor}Configuration")
                    .addLore(Chat.guiLine)
                    .addLore("&7Name: '${Chat.primaryColor}${pregenConfig.name}&7'")
                    .addLore("&7Type: ${Chat.primaryColor}${pregenConfig.type.name.uppercase()}")
                    .addLore("&7Generator: ${Chat.primaryColor}${pregenConfig.generator.name.uppercase()}")
                    .addLore(" ")
                    .addLore("&7Border: ${Chat.primaryColor}±${pregenConfig.border}")
                    .addLore(" ")
                    .addLore("&7Clear Water:${Chat.primaryColor}${if (pregenConfig.clearWater) "&aEnabled" else "&cDisabled"}")
                    .addLore("&7Clear Trees: ${Chat.primaryColor}${if (pregenConfig.clearTrees) "&aEnabled" else "&cDisabled"}")
                    .addLore("&7Ores Outside Caves: ${Chat.primaryColor}${if (pregenConfig.oresOutsideCaves) "&aEnabled" else "&cDisabled"}")
                    .addLore("&7Rates: ")
                    .addLore(" ${Chat.dot} &6Gold Ore: ${Chat.primaryColor}${pregenConfig.goldore}% Removed")
                    .addLore(" ${Chat.dot} &bDiamond Ore: ${Chat.primaryColor}${pregenConfig.diamondore}% Removed")
                    .addLore(" ${Chat.dot} &aSugar Cane: ${Chat.primaryColor}${pregenConfig.canerate}% Increased")
                    .addLore(Chat.guiLine)
                    .make()
                val submit = ItemBuilder(Material.EMERALD)
                    .name("&aCreate")
                    .addLore("&7Submit your configuration & generate")
                    .addLore("&7a new world based on its settings.")
                    .make()
                val changeGeneration = ItemBuilder(Material.REDSTONE)
                    .name("${Chat.primaryColor}Change Generation")
                    .addLore("&7Change the generation type of this world.")
                    .make()
                val changeBorder = ItemBuilder(Material.IRON_INGOT)
                    .name("${Chat.primaryColor}Change Border")
                    .addLore("&7Change the border size of this world.")
                    .make()
                val changeVarious = ItemBuilder(Material.PAPER)
                    .name("${Chat.primaryColor}Change Settings")
                    .addLore("&7Change various settings of this world.")
                    .make()
                gui.item(3, changeGeneration).onClick {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "ep generation")
                }
                gui.item(4, changeBorder).onClick {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "ep border")
                }
                gui.item(5, changeVarious).onClick {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "ep settings")
                }
                gui.item(8, submit).onClick {
                    it.isCancelled = true
                    sender.closeInventory()
                    createWorld(pregenConfig)
                }
                gui.item(0, config).onClick {
                    it.isCancelled = true
                }
                sender.openInventory(gui.make())
            }
        }
        return true
    }

}