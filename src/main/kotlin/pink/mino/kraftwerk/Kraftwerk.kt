package pink.mino.kraftwerk

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.commands.*
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.listeners.*
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

        getCommand("msg").executor = MessageCommand()

        getCommand("gm").executor = GamemodeCommand()
        getCommand("gamemode").executor = GamemodeCommand()
        getCommand("gma").executor = GamemodeCommand()
        getCommand("gms").executor = GamemodeCommand()
        getCommand("gmsp").executor = GamemodeCommand()
        getCommand("gmc").executor = GamemodeCommand()

        getCommand("pregen").executor = PregenCommand()



        /* ProtocolLib stuff */
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            println("You need ProtocolLib in order to use this plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        /* Discord */
        Discord.main()

        /* This just enables Hardcore Hearts */
        protocolManager?.addPacketListener(object : PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN) {
            override fun onPacketSending(e: PacketEvent) {
                if(e.packetType.equals(PacketType.Play.Server.LOGIN)) {
                    e.packet.booleans.write(0, true)
                }
            }
        })

        Settings.instance.setup(this)

        Bukkit.getLogger().info("Kraftwerk enabled")
    }
    override fun onDisable() {
        Bukkit.getLogger().info("Kraftwerk disabled")
    }

}