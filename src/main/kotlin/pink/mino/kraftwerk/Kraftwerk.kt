package pink.mino.kraftwerk

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.signature.TwitterCredentials
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.dv8tion.jda.api.entities.Activity
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
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
import javax.sql.DataSource


/*
Dear weird person:
Only I and God know how this plugin works.
 */

class Kraftwerk : ExtendedJavaPlugin() {

    private var protocolManager: ProtocolManager? = null
    var vote: Vote? = null
    var game: UHCTask? = null
    lateinit var dataSource: DataSource
    lateinit var twitter: TwitterClient

    companion object {
        val instance = this
    }

    override fun load() {
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun enable() {
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
        Bukkit.getServer().pluginManager.registerEvents(WorldSwitchListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(StatsFeature(), this)

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
        getCommand("near").executor = NearbyCommand()
        getCommand("startvote").executor = StartVoteCommand()
        getCommand("force").executor = ForceCommand()

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
        getCommand("discord").executor = DiscordCommand()
        getCommand("apply").executor = ApplyCommand()
        getCommand("teaminventory").executor = TeamInventoryCommand()
        getCommand("moles").executor = MolesCommand()
        getCommand("molekit").executor = MoleKitCommand()
        getCommand("molechat").executor = MoleChatCommand()
        getCommand("moleloc").executor = MoleLocationCommand()
        getCommand("molelist").executor = MolesListCommand()
        getCommand("store").executor = StoreCommand()
        getCommand("rules").executor = RulesCommand()
        getCommand("statistics").executor = StatsCommand()
        getCommand("voteyes").executor = VoteYesCommand()
        getCommand("voteno").executor = VoteNoCommand()

        /* ProtocolLib stuff */
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            println("You need ProtocolLib in order to use this plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

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

        /* Discord */
        Discord.main()

        if (!SettingsFeature.instance.data!!.getBoolean("matchpost.posted")) SettingsFeature.instance.data!!.set("whitelist.requests", false)
        SettingsFeature.instance.saveData()
        if (!SettingsFeature.instance.data!!.getBoolean("matchpost.cancelled")) {
            if (SettingsFeature.instance.data!!.getString("matchpost.opens") != null) {
                ScheduleBroadcast(SettingsFeature.instance.data!!.getString("matchpost.opens")).runTaskTimer(this, 0L, 300L)
                ScheduleOpening(SettingsFeature.instance.data!!.getString("matchpost.opens")).runTaskTimer(this, 0L, 300L)
            }
            if (SettingsFeature.instance.data!!.getString("matchpost.host") == null) {
                if (SettingsFeature.instance.data!!.getString("server.region") == "NA") {
                    Discord.instance!!.presence.activity = Activity.playing("na.applejuice.bar")
                } else {
                    Discord.instance!!.presence.activity = Activity.playing("eu.applejuice.bar")
                }
            }
            else Discord.instance!!.presence.activity = Activity.playing(SettingsFeature.instance.data!!.getString("matchpost.host"))
        } else {
            if (SettingsFeature.instance.data!!.getString("server.region") == "NA") {
                Discord.instance!!.presence.activity = Activity.playing("na.applejuice.bar")
            } else {
                Discord.instance!!.presence.activity = Activity.playing("eu.applejuice.bar")
            }
            SettingsFeature.instance.data!!.set("matchpost.cancelled", null)
            SettingsFeature.instance.saveData()
        }

        GameState.setState(GameState.LOBBY)
        Bukkit.getLogger().info("Game state set to Lobby.")
        for (world in Bukkit.getWorlds()) {
            world.pvp = true
        }
        InfoFeature().runTaskTimerAsynchronously(this, 0L, 6000L)

        for (hologram in HologramsAPI.getHolograms(this)) {
            hologram.delete()
        }

        /*
        val gamesPlayed = HologramsAPI.createHologram(this, Location(Bukkit.getWorld("Spawn"), -230.5, 101.0, -131.5))
        val wins = HologramsAPI.createHologram(this, Location(Bukkit.getWorld("Spawn"), -230.5, 101.0, -149.5))
        val kills = HologramsAPI.createHologram(this, Location(Bukkit.getWorld("Spawn"), -212.5, 101.0, -149.5))
        val diamondsMined = HologramsAPI.createHologram(this, Location(Bukkit.getWorld("Spawn"), -212.5, 101.0, -131.5))

        gamesPlayed.appendTextLine(Chat.colored("&c&lGames Played"))
        gamesPlayed.appendTextLine(Chat.guiLine)

        wins.appendTextLine(Chat.colored("&c&lWins"))
        wins.appendTextLine(Chat.guiLine)

        kills.appendTextLine(Chat.colored("&c&lKills"))
        kills.appendTextLine(Chat.guiLine)

        diamondsMined.appendTextLine(Chat.colored("&c&lDiamonds Mined"))
        diamondsMined.appendTextLine(Chat.guiLine)

        StatsHandler.getTopValues("games_played", gamesPlayed)
        StatsHandler.getTopValues("wins", wins)
        StatsHandler.getTopValues("kills", kills)
        StatsHandler.getTopValues("diamonds_mined", diamondsMined)

         */
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

        val dataSource: MysqlDataSource = MysqlConnectionPoolDataSource()

        dataSource.serverName = host
        dataSource.port = port
        dataSource.databaseName = database
        dataSource.user = user
        dataSource.setPassword(password)

        testDataSource(dataSource)

        this.dataSource = dataSource
    }

    @Throws(SQLException::class)
    private fun testDataSource(dataSource: DataSource) {
        val conn = dataSource.connection
        if (!conn.isValid(1)) {
            throw SQLException("Could not establish database connection.")
        }
    }


    override fun disable() {
        SettingsFeature.instance.data!!.set("game.winners", ArrayList<String>())
        SettingsFeature.instance.data!!.set("game.list", ArrayList<String>())
        SettingsFeature.instance.data!!.set("game.kills", null)
        SettingsFeature.instance.saveData()
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