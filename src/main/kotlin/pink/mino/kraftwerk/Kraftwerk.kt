package pink.mino.kraftwerk

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.commands.*
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.HardcoreHearts
import pink.mino.kraftwerk.features.Settings
import pink.mino.kraftwerk.features.Teams
import pink.mino.kraftwerk.features.options.ConfigOptionHandler
import pink.mino.kraftwerk.listeners.*
import pink.mino.kraftwerk.utils.GameState


class Kraftwerk : JavaPlugin() {

    private var protocolManager: ProtocolManager? = null

    override fun onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun onEnable() {
        /* Registering listeners */
        Bukkit.getServer().pluginManager.registerEvents(ServerListPing(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerJoin(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerQuit(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerDeath(), this)
        Bukkit.getServer().pluginManager.registerEvents(Command(), this)
        Bukkit.getServer().pluginManager.registerEvents(WorldInitialize(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeatherChange(), this)
        Bukkit.getServer().pluginManager.registerEvents(FoodChange(), this)
        Bukkit.getServer().pluginManager.registerEvents(EntityHealthRegain(), this)

        /* Registering commands */
        getCommand("clear").executor = ClearInventoryCommand()
        getCommand("cleareffects").executor = ClearPotionEffectsCommand()
        getCommand("feed").executor = FeedCommand()
        getCommand("heal").executor = HealCommand()
        getCommand("fly").executor = FlyCommand()
        getCommand("pregen").executor = PregenCommand()
        getCommand("config").executor = ConfigCommand()
        getCommand("editconfig").executor = EditConfigCommand()

        getCommand("whitelist").executor = WhitelistCommand()

        getCommand("gm").executor = GamemodeCommand()
        getCommand("gamemode").executor = GamemodeCommand()
        getCommand("gma").executor = GamemodeCommand()
        getCommand("gms").executor = GamemodeCommand()
        getCommand("gmsp").executor = GamemodeCommand()
        getCommand("gmc").executor = GamemodeCommand()

        getCommand("msg").executor = MessageCommand()
        getCommand("reply").executor = ReplyCommand()
        getCommand("team").executor = TeamCommand()
        getCommand("health").executor = HealthCommand()
        getCommand("pm").executor = PMCommand()
        getCommand("pmc").executor = PMCCommand()

        /* ProtocolLib stuff */
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            println("You need ProtocolLib in order to use this plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        /* Discord */
        Discord.main()

        /* This just enables Hardcore Hearts */
        protocolManager?.addPacketListener(HardcoreHearts())

        Settings.instance.setup(this)
        Teams.manager.setupTeams()
        ConfigOptionHandler.setup()
        addRecipes()

        if (Settings.instance.data!!.contains("game.state")) {
            GameState.setState(GameState.valueOf(Settings.instance.data!!.getString("game.state")))
            Bukkit.getLogger().info("Game state set to ${Settings.instance.data!!.getString("game.state")}.")
        } else {
            GameState.setState(GameState.LOBBY)
            Bukkit.getLogger().info("Game state set to Lobby.")
        }

        Bukkit.getLogger().info("Kraftwerk enabled.")
    }

    override fun onDisable() {
        Bukkit.getLogger().info("Kraftwerk disabled.")
    }

    fun addRecipes() {
        val mater = MaterialData(Material.SKULL_ITEM)
        mater.data = 3.toByte()
        val head = ItemStack(Material.GOLDEN_APPLE)
        val meta: ItemMeta = head.itemMeta
        meta.displayName = ChatColor.GOLD.toString() + "Golden Head"
        meta.lore = listOf(ChatColor.DARK_PURPLE.toString() + "Some say consuming the head of a", ChatColor.DARK_PURPLE.toString() + "fallen foe strengthens the blood.")
        head.itemMeta = meta
        val goldenHead: ShapedRecipe = ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', mater)
        Bukkit.getServer().addRecipe(goldenHead)
    }

}