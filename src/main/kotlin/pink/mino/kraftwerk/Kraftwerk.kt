package pink.mino.kraftwerk

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.signature.TwitterCredentials
import net.dv8tion.jda.api.entities.Activity
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.commands.*
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.*
import pink.mino.kraftwerk.listeners.*
import pink.mino.kraftwerk.listeners.lunar.PlayerRegisterListener
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.Scoreboard
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

/*
Dear weird person:
Only I and God know how this plugin works.
 */

class Kraftwerk : JavaPlugin() {

    private var protocolManager: ProtocolManager? = null
    lateinit var dataSource: DataSource
    lateinit var twitter: TwitterClient

    companion object {
        val instance = this
    }

    override fun onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun onEnable() {
        /* Registering listeners */
        Bukkit.getServer().pluginManager.registerEvents(ServerListPingListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerJoinListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerQuitListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerDeathListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(CommandListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(ChatListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(WorldInitializeListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeatherChangeListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(SaturationFixer(), this)
        Bukkit.getServer().pluginManager.registerEvents(EntityHealthRegainListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerConnectListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerConsumeListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(ArenaFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(SpawnFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(UHCFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(RatesFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(CombatLogFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(SpecFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(ShootListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerInteractListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerRegisterListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(TabFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(PortalListener(), this)
        //Bukkit.getServer().pluginManager.registerEvents(LiteBans(), this)
        Bukkit.getServer().pluginManager.registerEvents(WorldSwitchListener(), this)

        /* Registering commands */
        getCommand("clear").executor = ClearInventoryCommand()
        getCommand("cleareffects").executor = ClearPotionEffectsCommand()
        getCommand("feed").executor = FeedCommand()
        getCommand("heal").executor = HealCommand()
        getCommand("fly").executor = FlyCommand()
        getCommand("pregen").executor = PregenCommand()
        getCommand("config").executor = ConfigCommand()
        getCommand("editconfig").executor = EditConfigCommand()
        getCommand("world").executor = WorldCommand()
        getCommand("scatter").executor = ScatterCommand()
        getCommand("clearchat").executor = ClearChatCommand()
        getCommand("whitelist").executor = WhitelistCommand()
        getCommand("regenarena").executor = RegenArenaCommand()
        getCommand("start").executor = StartCommand()
        getCommand("border").executor = BorderCommand()
        getCommand("end").executor = EndGameCommand()
        getCommand("winner").executor = WinnerCommand()
        getCommand("latescatter").executor = LatescatterCommand()
        getCommand("matchpost").executor = MatchpostCommand()
        getCommand("scenariomanager").executor = ScenarioManagerCommand()
        getCommand("spectate").executor = SpectateCommand()
        getCommand("specchat").executor = SpecChatCommand()
        getCommand("helpop").executor = HelpOpCommand()
        getCommand("helpopreply").executor = HelpOpReplyCommand()
        getCommand("tppos").executor = TeleportPositionCommand()
        getCommand("tp").executor = TeleportCommand()
        getCommand("cancel").executor = CancelCommand()

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
        getCommand("arena").executor = ArenaCommand()
        getCommand("spawn").executor = SpawnCommand()
        getCommand("killtop").executor = KillTopCommand()
        getCommand("scenarios").executor = ScenarioCommand()
        getCommand("statistics").executor = StatsCommand()
        getCommand("discord").executor = DiscordCommand()
        getCommand("apply").executor = ApplyCommand()
        getCommand("teaminventory").executor = TeamInventoryCommand()
        getCommand("moles").executor = MolesCommand()
        getCommand("molekit").executor = MoleKitCommand()
        getCommand("molechat").executor = MoleChatCommand()
        getCommand("moleloc").executor = MoleLocationCommand()
        getCommand("molelist").executor = MolesListCommand()

        /* ProtocolLib stuff */
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            println("You need ProtocolLib in order to use this plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        /* Discord */
        Discord.main()

        /* This just enables Hardcore Hearts */
        protocolManager?.addPacketListener(HardcoreHeartsFeature())
        // TODO("Finish this") Events.get().register(LiteBans())
        CustomPayloadFixerFeature(this)

        /* Sets up misc features */
        SettingsFeature.instance.setup(this)
        TeamsFeature.manager.setupTeams()
        Scoreboard.kills!!.unregister()
        Scoreboard.setup()
        ConfigOptionHandler.setup()
        ScenarioHandler.setup()
        addRecipes()

        setupDataSource()
        setupTwitter()

        if (!SettingsFeature.instance.data!!.getBoolean("matchpost.posted")) SettingsFeature.instance.data!!.set("whitelist.requests", false)
        SettingsFeature.instance.saveData()
        if (!SettingsFeature.instance.data!!.getBoolean("matchpost.cancelled")) {
            if (SettingsFeature.instance.data!!.getString("matchpost.opens") != null) {
                ScheduleBroadcast(SettingsFeature.instance.data!!.getString("matchpost.opens")).runTaskTimer(this, 0L, 300L)
                ScheduleOpening(SettingsFeature.instance.data!!.getString("matchpost.opens")).runTaskTimer(this, 0L, 300L)
            }
            if (SettingsFeature.instance.data!!.getString("matchpost.host") == null) Discord.instance!!.presence.activity = Activity.playing("na.applejuice.bar")
            else Discord.instance!!.presence.activity = Activity.playing(SettingsFeature.instance.data!!.getString("matchpost.host"))
        } else {
            Discord.instance!!.presence.activity = Activity.playing("na.applejuice.bar")
            SettingsFeature.instance.data!!.set("matchpost.cancelled", null)
            SettingsFeature.instance.saveData()
        }

        GameState.setState(GameState.LOBBY)
        Bukkit.getLogger().info("Game state set to Lobby.")
        for (world in Bukkit.getWorlds()) {
            world.pvp = true
        }
        InfoFeature().runTaskTimerAsynchronously(this, 0L, 6000L)
        Bukkit.getLogger().info("Kraftwerk enabled.")
    }

    fun setupTwitter() {
        this.twitter = TwitterClient(
            TwitterCredentials.builder()
                .accessToken("1498385359121657864-vL64dNrkXfoF9jCOAQfVfSCBzhI5cf")
                .accessTokenSecret("9S8KMk5SiSWD71BlklxQ1pCpnKhSV2p98PTjvfnPXeLW8")
                .bearerToken("AAAAAAAAAAAAAAAAAAAAAFTvZgEAAAAAx%2FlXSH6jVLANz9JfjDspD94jdDA%3DP1VqgNG9N38xOPDyz9Kd1mjSPNwxsTgJjB2XRaBSp8J3TXeqKO")
                .apiKey("0FrHNXc5kgVMBILgwwpZW3k7r")
                .apiSecretKey("O40NzNFgVmcVuHxIlx8MgI0yO5qlnyncS9D2q0PkxGHIyYYpPS")
                .build()
        )
    }

    fun setupDataSource() {
        val host = SettingsFeature.instance.data!!.getString("database.host")
        val port = SettingsFeature.instance.data!!.getInt("database.port")
        val database = SettingsFeature.instance.data!!.getString("database.database")
        val user = SettingsFeature.instance.data!!.getString("database.user")
        val password = SettingsFeature.instance.data!!.getString("database.password")

        val props = Properties()
        props.setProperty("dataSourceClassName", "org.mariadb.jdbc.MariaDbDataSource")
        props.setProperty("dataSource.serverName", host)
        props.setProperty("dataSource.portNumber", port.toString())
        props.setProperty("dataSource.user", user)
        props.setProperty("dataSource.password", password)
        props.setProperty("dataSource.databaseName", database)

        val config = HikariConfig(props)
        config.maximumPoolSize = 10
        testDataSource(dataSource)

        this.dataSource = HikariDataSource(config)
    }

    @Throws(SQLException::class)
    private fun testDataSource(dataSource: DataSource) {
        val conn = dataSource.connection
        if (!conn.isValid(1)) {
            throw SQLException("Could not establish database connection.")
        }
    }


    override fun onDisable() {
        Bukkit.getLogger().info("Kraftwerk disabled.")
        SettingsFeature.instance.data!!.set("game.winners", ArrayList<String>())
        SettingsFeature.instance.data!!.set("game.list", ArrayList<String>())
        SettingsFeature.instance.data!!.set("game.kills", null)
        SettingsFeature.instance.saveData()
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