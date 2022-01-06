package pink.mino.kraftwerk.features

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk

class HardcoreHearts : PacketAdapter(JavaPlugin.getPlugin(Kraftwerk::class.java), ListenerPriority.NORMAL, PacketType.Play.Server.LOGIN) {
    override fun onPacketSending(e: PacketEvent) {
        if(e.packetType.equals(PacketType.Play.Server.LOGIN)) {
            e.packet.booleans.write(0, true)
        }
    }
}