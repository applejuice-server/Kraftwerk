package pink.mino.kraftwerk

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.mongodb.MongoClient
import com.mongodb.MongoClientException
import com.mongodb.MongoClientURI
import me.lucko.helper.plugin.ExtendedJavaPlugin
import me.lucko.helper.utils.Log
import me.lucko.spark.api.Spark
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import org.bukkit.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
import pink.mino.kraftwerk.commands.*
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.*
import pink.mino.kraftwerk.listeners.*
import pink.mino.kraftwerk.listeners.donator.CowboyFeature
import pink.mino.kraftwerk.listeners.donator.MobEggsListener
import pink.mino.kraftwerk.listeners.lunar.PlayerRegisterListener
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.security.auth.login.LoginException


/*
Dear weird person:
Only I and God know how this plugin works.

not anymore :)))))))))
 */

class Kraftwerk : ExtendedJavaPlugin() {

    private var protocolManager: ProtocolManager? = null
    var vote: Vote? = null
    var game: UHCTask? = null
    var database: Boolean = false
    var discord: Boolean = false
    var arena: Boolean = true
    var buildMode: HashMap<UUID, Boolean> = hashMapOf()

    val fullbright: MutableSet<String> = mutableSetOf()

    var scatterLocs: HashMap<String, Location> = HashMap()
    var scattering = false

    lateinit var discordInstance: JDA
    lateinit var statsHandler: StatsHandler
    lateinit var dataSource: MongoClient
    lateinit var spark: Spark
    lateinit var profileHandler: ProfileService

    var welcomeChannelId: Long? = null
    var alertsRoleId: Long? = null
    var gameAlertsChannelId: Long? = null
    var winnersChannelId: Long? = null
    var preWhitelistChannelId: Long? = null
    var gameLogsChannelId: Long? = null
    var punishmentChannelId: Long? = null

    companion object {
        lateinit var instance: Kraftwerk
    }

    override fun load() {
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun enable() {
        instance = this
        ConfigFeature.instance.setup(this)

        /* Registering listeners */
        Bukkit.getServer().pluginManager.registerEvents(ServerListPingListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerJoinListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerQuitListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerDeathListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerRespawnListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(CommandListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(ChatListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(WorldInitializeListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeatherChangeListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(SaturationFixer(), this)
        Bukkit.getServer().pluginManager.registerEvents(EntityHealthRegainListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerConnectListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerConsumeListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(SpawnFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(UHCFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(RatesFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(CombatLogFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(SpecFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(ShootListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerRegisterListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PortalListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(WorldSwitchListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(StatsFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(RespawnFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(PickupFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(ChunkPopulateListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(OreLimiterListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PregenListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(CanePopulatorFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(TeamsFeature.manager, this)
        Bukkit.getServer().pluginManager.registerEvents(OpenedMatchesListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(MobEggsListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(CowboyFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(ArenaFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(OrganizedFights.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(XpFeature(), this)
        //Bukkit.getServer().pluginManager.registerEvents(MLGFeature(), this)

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
        getCommand("respawn").executor = RespawnCommand()
        getCommand("editpregen").executor = EditPregenCommand()
        getCommand("generate").executor = GenerateCommand()
        getCommand("giveitems").executor = GiveItemsCommand()
        getCommand("invsee").executor = InvseeCommand()
        getCommand("helpoplist").executor = HelpopListCommand()
        getCommand("game").executor = GameCommand()
        getCommand("setspawn").executor = SetSpawnCommand()
        getCommand("orgs").executor = OrganizedFightsCommand()

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
        getCommand("pmores").executor = PMOresCommand()
        getCommand("pmminedores").executor = PMMOresCommand()
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
        getCommand("ping").executor = PingCommand()
        getCommand("voteyes").executor = VoteYesCommand()
        getCommand("voteno").executor = VoteNoCommand()
        getCommand("donator").executor = DonatorCommand()
        getCommand("fullbright").executor = FullbrightCommand()
        getCommand("timers").executor = TimersCommand()
        getCommand("arena").executor = ArenaCommand()
        getCommand("deathloc").executor = DeathLocCommand()
        getCommand("media").executor = MediaCommand()
        getCommand("enemyrecon").executor = EnemyReconCommand()
        getCommand("ignore").executor = IgnoreCommand()
        getCommand("profile").executor = ProfileCommand()
        getCommand("portalloc").executor = PortalPosCommand()
        getCommand("chat").executor = ChatCommand()
        getCommand("staffchat").executor = StaffChatCommand()
        getCommand("emotes").executor = EmotesCommand()
        getCommand("redstone").executor = RedstoneCommand()
        getCommand("lapis").executor = LapisCommand()
        getCommand("granttag").executor = GrantTagCommand()
        getCommand("thanks").executor = ThanksCommand()
        getCommand("fight").executor = FightCommand()
        getCommand("resethealth").executor = ResetHealthCommand()
        getCommand("buildmode").executor = BuildModeCommand()
        getCommand("setthing").executor = SetThingCommand()
        getCommand("alts").executor = AltsCommand()
        getCommand("ban").executor = BanCommand()
        getCommand("kick").executor = KickCommand()
        getCommand("mute").executor = MuteCommand()
        getCommand("helpopmute").executor = HelpopMuteCommand()
        getCommand("disqualify").executor = DisqualifyCommand()
        getCommand("warn").executor = WarnCommand()
        getCommand("unban").executor = UnbanCommand()
        getCommand("unhelpopmute").executor = UnHelpopMuteCommand()
        getCommand("unmute").executor = UnmuteCommand()
        //getCommand("hotbar").executor = HotbarCommand()

        /* ProtocolLib stuff */
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            println("You need ProtocolLib in order to use this plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        /* This just enables Hardcore Hearts */
        protocolManager?.addPacketListener(HardcoreHeartsFeature())
        protocolManager!!.addPacketListener(SpecClickFeature())
        CustomPayloadFixerFeature(this)

        /* Sets up misc features */
        setupDataSource()
        TeamsFeature.manager.setupColors()
        Scoreboard.setup()
        if (Scoreboard.sb.getObjective("killboard") != null) {
            Scoreboard.kills!!.unregister()
            Scoreboard.setup()
        }
        ConfigOptionHandler.setup()
        ScenarioHandler.setup()
        addRecipes()

        statsHandler = StatsHandler()
        profileHandler = ProfileService()

        val provider = Bukkit.getServicesManager().getRegistration(
            Spark::class.java
        )
        if (provider != null) {
            spark = provider.provider
        }

        /* Discord */
        try {
            Discord.main()
            if (!ConfigFeature.instance.data!!.getBoolean("matchpost.posted")) ConfigFeature.instance.data!!.set("whitelist.requests", false)
            ConfigFeature.instance.saveData()
            if (!ConfigFeature.instance.data!!.getBoolean("matchpost.cancelled")) {
                if (ConfigFeature.instance.data!!.getString("matchpost.opens") != null) {
                    ScheduleBroadcast(ConfigFeature.instance.data!!.getString("matchpost.opens")).runTaskTimer(this, 0L, 300L)
                    ScheduleOpening(ConfigFeature.instance.data!!.getString("matchpost.opens")).runTaskTimer(this, 0L, 300L)
                }
                if (ConfigFeature.instance.data!!.getString("matchpost.host") == null) {
                    Discord.instance!!.presence.activity = Activity.playing(if (ConfigFeature.instance.config!!.getString("chat.serverIp") != null) ConfigFeature.instance.config!!.getString("chat.serverIp") else "no server ip setup in config tough tits")
                }
                else Discord.instance!!.presence.activity = Activity.playing(ConfigFeature.instance.data!!.getString("matchpost.host"))
            } else {
                Discord.instance!!.presence.activity = Activity.playing(if (ConfigFeature.instance.config!!.getString("chat.serverIp") != null) ConfigFeature.instance.config!!.getString("chat.serverIp") else "no server ip setup in config tough tits")
                ConfigFeature.instance.data!!.set("matchpost.cancelled", null)
                ConfigFeature.instance.saveData()
            }
        } catch (e: LoginException) {
            Log.severe("Failed to login to discord: " + e.message)
        }

        if (ConfigFeature.instance.config!!.getLong("discord.welcomeChannelId") != null) {
            welcomeChannelId = ConfigFeature.instance.config!!.getLong("discord.welcomeChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.punishmentChannelId") != null) {
            punishmentChannelId = ConfigFeature.instance.config!!.getLong("discord.punishmentChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.alertsRoleId") != null) {
            alertsRoleId = ConfigFeature.instance.config!!.getLong("discord.alertsRoleId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.gameAlertsChannelId") != null) {
            gameAlertsChannelId = ConfigFeature.instance.config!!.getLong("discord.gameAlertsChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.winnersChannelId") != null) {
            winnersChannelId = ConfigFeature.instance.config!!.getLong("discord.winnersChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.preWhitelistChannelId") != null) {
            preWhitelistChannelId = ConfigFeature.instance.config!!.getLong("discord.preWhitelistChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.gameLogsChannelId") != null) {
            gameLogsChannelId = ConfigFeature.instance.config!!.getLong("discord.gameLogsChannelId")
        }
        //twitterInstance.updateStatus("test")


        GameState.setState(GameState.LOBBY)
        Log.info("Game state set to Lobby.")
        for (world in Bukkit.getWorldContainer().list()!!) {
            if (world == "Spawn" || world == "Arena") {
                server.createWorld(WorldCreator(world))
            } else {
                val wc = WorldCreator(world)
                if (ConfigFeature.instance.worlds!!.getString("${world}.type").lowercase() == "normal") {
                    wc.environment(World.Environment.NORMAL)
                } else if (ConfigFeature.instance.worlds!!.getString("${world}.type").lowercase() == "nether") {
                    wc.environment(World.Environment.NETHER)
                } else if (ConfigFeature.instance.worlds!!.getString("${world}.type").lowercase() == "end") {
                    wc.environment(World.Environment.THE_END)
                } else {
                    wc.environment(World.Environment.NORMAL)
                }
                server.createWorld(wc)
            }

            Log.info("World $world loaded.")
        }
        for (world in Bukkit.getWorlds()) {
            world.pvp = true
        }

        //Discord.instance!!.getTextChannelById(756953696038027425)!!.sendMessage("test")
        //UpdateLeaderboards().runTaskTimer(this, 0L, 20L)
        InfoFeature().runTaskTimerAsynchronously(this, 0L, 6000L)
        TabFeature().runTaskTimer(this, 0L, 20L)

        ConfigFeature.instance.data!!.set("whitelist.enabled", ConfigFeature.instance.config!!.getBoolean("options.whitelist-after-restart"))
        ConfigFeature.instance.data!!.set("game.list", listOf<Any>())
        ConfigFeature.instance.saveData()
        Leaderboards().runTaskTimer(this, 0L, 20L)
        Bukkit.getLogger().info("Kraftwerk enabled.")
    }

    fun setupDataSource() {
        val uri = ConfigFeature.instance.config!!.getString("database.uri")
        if (uri == null) {
            Log.severe("No database URI set. Please set it in the config.")
            return
        }
        var client: MongoClient? = null
        try {
            client = MongoClient(MongoClientURI(uri))
        } catch (e: MongoClientException) {
            e.printStackTrace()
        }

        if (client != null) {
            this.dataSource = client
        }
    }

    override fun disable() {
        ConfigFeature.instance.data!!.set("game.winners", ArrayList<String>())
        ConfigFeature.instance.data!!.set("game.list", ArrayList<String>())
        ConfigFeature.instance.data!!.set("game.kills", null)
        ConfigFeature.instance.saveData()
        Bukkit.getWorldContainer().listFiles()!!.forEach { file ->
            if (file.name == "Spawn") {
                Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach {
                    if (it.isDirectory) {
                        if (it.name == "stats" || it.name == "playerdata") {
                            it.listFiles()?.forEach { file ->
                                file.delete()
                            }
                        }
                    }
                }
            }
        }
        for (team in TeamsFeature.manager.sb.teams) {
            team.unregister()
        }
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

        val wools = ArrayList(
            listOf(
                ItemStack(Material.WOOL, 1, 0),
                ItemStack(Material.WOOL, 1, 1),
                ItemStack(Material.WOOL, 1, 2),
                ItemStack(Material.WOOL, 1, 3),
                ItemStack(Material.WOOL, 1, 4),
                ItemStack(Material.WOOL, 1, 5),
                ItemStack(Material.WOOL, 1, 6),
                ItemStack(Material.WOOL, 1, 7),
                ItemStack(Material.WOOL, 1, 8),
                ItemStack(Material.WOOL, 1, 9),
                ItemStack(Material.WOOL, 1, 10),
                ItemStack(Material.WOOL, 1, 11),
                ItemStack(Material.WOOL, 1, 12),
                ItemStack(Material.WOOL, 1, 13),
                ItemStack(Material.WOOL, 1, 14),
                ItemStack(Material.WOOL, 1, 15)
            )
        )

        for (wool in wools) {
            val recipe = ShapedRecipe(ItemStack(Material.STRING))
                .shape("AA", "AA")
                .setIngredient('A', wool.data)
            Bukkit.addRecipe(recipe)
        }
    }

}