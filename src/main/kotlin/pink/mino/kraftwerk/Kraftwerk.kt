package pink.mino.kraftwerk

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.commands.*
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.HardcoreHearts
import pink.mino.kraftwerk.features.Teams
import pink.mino.kraftwerk.listeners.*
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.Settings


class Kraftwerk : JavaPlugin() {

    private var protocolManager: ProtocolManager? = null

    override fun onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager()
    }


    override fun onEnable() {
        /* Registering listeners */
        Bukkit.getServer().pluginManager.registerEvents(ServerListPing(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerJoin(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerDeath(), this)
        Bukkit.getServer().pluginManager.registerEvents(Command(), this)
        Bukkit.getServer().pluginManager.registerEvents(WorldInitialize(), this)

        /* Registering commands */
        getCommand("clear").executor = ClearInventoryCommand()
        getCommand("cleareffects").executor = ClearPotionEffectsCommand()
        getCommand("feed").executor = FeedCommand()
        getCommand("heal").executor = HealCommand()
        getCommand("fly").executor = FlyCommand()
        getCommand("pregen").executor = PregenCommand()

        getCommand("gm").executor = GamemodeCommand()
        getCommand("gamemode").executor = GamemodeCommand()
        getCommand("gma").executor = GamemodeCommand()
        getCommand("gms").executor = GamemodeCommand()
        getCommand("gmsp").executor = GamemodeCommand()
        getCommand("gmc").executor = GamemodeCommand()

        getCommand("msg").executor = MessageCommand()
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

        if (Settings.instance.data!!.contains("game.state")) {
            GameState.setState(GameState.valueOf(Settings.instance.data!!.getString("game.state")))
            Bukkit.getLogger().info("Game state set to ${Settings.instance.data!!.getString("game.state")}.")
        } else {
            GameState.setState(GameState.LOBBY)
            Bukkit.getLogger().info("Game state set to Lobby.")
        }

        Bukkit.getLogger().info("Kraftwerk enabled")
    }
    override fun onDisable() {
        Bukkit.getLogger().info("Kraftwerk disabled")
    }

}