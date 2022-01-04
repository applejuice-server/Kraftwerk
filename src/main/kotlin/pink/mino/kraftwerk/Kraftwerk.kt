package pink.mino.kraftwerk

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.discord.Main
import pink.mino.kraftwerk.listeners.*


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

        /* ProtocolLib stuff */
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            println("You need ProtocolLib in order to use this plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        /* Discord */
        Main.main()

        /* This just enables Hardcore Hearts */
        protocolManager?.addPacketListener(object : PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN) {
            override fun onPacketSending(e: PacketEvent) {
                if(e.packetType.equals(PacketType.Play.Server.LOGIN)) {
                    e.packet.booleans.write(0, true)
                }
            }
        })


        Bukkit.getLogger().info("Kraftwerk enabled")
    }
    override fun onDisable() {
        Bukkit.getLogger().info("Kraftwerk disabled")
    }

}