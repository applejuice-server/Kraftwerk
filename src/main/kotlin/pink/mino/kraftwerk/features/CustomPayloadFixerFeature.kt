package pink.mino.kraftwerk.features

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.utility.StreamSerializer
import com.comphenix.protocol.wrappers.nbt.NbtCompound
import com.comphenix.protocol.wrappers.nbt.NbtFactory
import io.netty.buffer.ByteBuf
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level

class CustomPayloadFixerFeature(plugin: Kraftwerk) {
    init {
        val manager = ProtocolLibrary.getProtocolManager()
        manager.addPacketListener(object : PacketAdapter(plugin, PacketType.Play.Client.CUSTOM_PAYLOAD) {
            override fun onPacketReceiving(event: PacketEvent) {
                checkPacket(event)
            }
        })
        manager.addPacketListener(object :
            PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.POSITION) {
            override fun onPacketReceiving(event: PacketEvent) {
                if (event.packetType != PacketType.Play.Client.POSITION) {
                    return
                }
                try {
                    val before = event.player.location.blockY
                    val next = event.packet.doubles.read(1).toInt()
                    if (before + 200 < next) {
                        event.isCancelled = true
                    }
                } catch (ignored: Exception) {
                }
            }
        })
        plugin.server.scheduler.runTaskTimer(plugin, {
            val iterator: MutableIterator<Map.Entry<Player, Long>> = PACKET_USAGE.entries.iterator()
            while (iterator.hasNext()) {
                val player = iterator.next().key
                try {
                    if (!player.isOnline || !player.isValid) {
                        iterator.remove()
                    }
                } catch (ignored: Exception) {
                }
            }
        }, 20L, 20L)
    }

    private fun checkPacket(event: PacketEvent) {
        val player = event.player ?: return
        val lastPacket = PACKET_USAGE.getOrDefault(player, -1L)

        // This fucker is already detected as an exploiter
        if (lastPacket == -2L) {
            event.isCancelled = true
            return
        }
        val name = event.packet.strings.readSafely(0)
        if ("MC|BSign" != name && "MC|BEdit" != name && "REGISTER" != name) {
            return
        }
        try {
            if ("REGISTER" == name) {
                checkChannels(event)
            } else {
                if (elapsed(lastPacket)) {
                    PACKET_USAGE[player] = System.currentTimeMillis()
                } else {
                    throw IOException("Packet flood")
                }
                checkNbtTags(event)
            }
        } catch (ex: Throwable) {
            // Set last packet usage to -2 so we wouldn't mind checking him again
            PACKET_USAGE[player] = -2L
            event.isCancelled = true
            if (player.name.startsWith("UNKNOWN[")) {
                return
            }
            Bukkit.getServer().scheduler.runTask(
                JavaPlugin.getPlugin(Kraftwerk::class.java)
            ) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).server.dispatchCommand(
                    JavaPlugin.getPlugin(Kraftwerk::class.java).server.consoleSender,
                    "ban " + player.name + " crash_glitch"
                )
            }
            JavaPlugin.getPlugin(Kraftwerk::class.java).server.logger
                .log(Level.WARNING, player.name + " tried to exploit CustomPayload: " + ex.message, ex)
        }
    }

    @Throws(IOException::class)
    private fun checkNbtTags(event: PacketEvent) {
        val container = event.packet
        val buffer = container.getSpecificModifier(ByteBuf::class.java).read(0).copy()
        val bytes = ByteArray(buffer.readableBytes())
        buffer.readBytes(bytes)
        try {
            DataInputStream(ByteArrayInputStream(bytes)).use { inputStream ->
                val itemStack = StreamSerializer.getDefault().deserializeItemStack(inputStream)
                    ?: throw IOException("Unable to deserialize ItemStack")
                val root = NbtFactory.fromItemTag(itemStack) as NbtCompound
                if (root == null) {
                    throw IOException("No NBT tag?!")
                } else if (!root.containsKey("pages")) {
                    throw IOException("No 'pages' NBT compound was found")
                } else {
                    val pages = root.getList<String>("pages")
                    if (pages.size() > 50) {
                        throw IOException("Too many pages")
                    }
                }
            }
        } finally {
            buffer.release()
        }
    }

    @Throws(Exception::class)
    private fun checkChannels(event: PacketEvent) {
        var channelsSize: Int
        channelsSize = try {
            event.player.listeningPluginChannels.size
        } catch (ex: UnsupportedOperationException) {
            0
        }
        val container = event.packet
        val buffer = container.getSpecificModifier(ByteBuf::class.java).read(0).copy()
        try {
            for (i in buffer.toString(Charsets.UTF_8).split("\u0000").toTypedArray().indices) {
                if (++channelsSize > 124) {
                    throw IOException("Too many channels")
                }
            }
        } finally {
            buffer.release()
        }
    }

    private fun elapsed(from: Long): Boolean {
        return from == -1L || System.currentTimeMillis() - from > 100L
    }

    companion object {
        // private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
        private val PACKET_USAGE: MutableMap<Player, Long> = ConcurrentHashMap()
    }
}