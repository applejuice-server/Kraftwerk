package pink.mino.kraftwerk.features

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent


class TabFeature : Listener {
    fun sendTablist(p: Player) {
        val craftplayer = p as CraftPlayer
        val connection = craftplayer.handle.playerConnection
        val header = ChatSerializer.a("{\"text\": \" \"}")
        val footer = ChatSerializer.a("{\"text\": \" \"}")
        val packet = PacketPlayOutPlayerListHeaderFooter()
        try {
            val headerField = packet.javaClass.getDeclaredField("a")
            headerField.isAccessible = true
            headerField.set(packet, header)
            headerField.isAccessible = !headerField.isAccessible
            val footerField = packet.javaClass.getDeclaredField("b")
            footerField.isAccessible = true
            footerField.set(packet, footer)
            footerField.isAccessible = !footerField.isAccessible
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
        connection.sendPacket(packet)
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        sendTablist(e.player)
    }
}