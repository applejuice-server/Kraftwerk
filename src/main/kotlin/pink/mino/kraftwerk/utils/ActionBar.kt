package pink.mino.kraftwerk.utils

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.jetbrains.annotations.NotNull

/**
 * =======================
 * Cheers blok601 <3
 * =======================
 */

class ActionBar {

    companion object {

        private val PENDING: MutableMap<Player, BukkitTask?> = HashMap()

        /**
         * Sends a message to the player's action bar.
         *
         *
         * The message will appear above the player's hot bar for 2 seconds and then fade away over 1 second.
         *
         * @param bukkitPlayer the player to send the message to.
         * @param message the message to send.
         */

        fun sendActionBarMessage(@NotNull bukkitPlayer: Player, @NotNull message: String) {
            sendRawActionBarMessage(bukkitPlayer, "{\"text\": \"${Chat.colored(message)}\"}")
        }

        /**
         * Sends a raw message (JSON format) to the player's action bar. Note: while the action bar accepts raw messages
         * it is currently only capable of displaying text.
         *
         *
         * The message will appear above the player's hot bar for 2 seconds and then fade away over 1 second.
         *
         * @param bukkitPlayer the player to send the message to.
         * @param rawMessage the json format message to send.
         */
        fun sendRawActionBarMessage(@NotNull bukkitPlayer: Player, @NotNull rawMessage: String?) {
            val player = bukkitPlayer as CraftPlayer
            val chatBaseComponent = ChatSerializer.a(rawMessage)
            val packetPlayOutChat = PacketPlayOutChat(chatBaseComponent, 2.toByte())
            player.handle.playerConnection.sendPacket(packetPlayOutChat)
        }

        /**
         * Sends a message to the player's action bar that lasts for an extended duration.
         *
         *
         * The message will appear above the player's hot bar for the specified duration and fade away during the last
         * second of the duration.
         *
         *
         * Only one long duration message can be sent at a time per player. If a new message is sent via this message
         * any previous messages still being displayed will be replaced.
         *
         * @param bukkitPlayer the player to send the message to.
         * @param message the message to send.
         * @param duration the duration the message should be visible for in seconds.
         * @param plugin the plugin sending the message.
         */
        fun sendActionBarMessage(@NotNull bukkitPlayer: Player, @NotNull message: String,
                                 @NotNull duration: Int, @NotNull plugin: Plugin?) {
            cancelPendingMessages(bukkitPlayer)
            val messageTask = object : BukkitRunnable() {
                private var count = 0
                override fun run() {
                    if (count >= duration - 3) {
                        cancel()
                    }
                    sendActionBarMessage(bukkitPlayer, ChatColor.translateAlternateColorCodes('&', message))
                    count++
                }
            }.runTaskTimer(plugin, 0L, 20L)
            PENDING[bukkitPlayer] = messageTask
        }

        private fun cancelPendingMessages(@NotNull bukkitPlayer: Player) {
            if (PENDING.containsKey(bukkitPlayer)) {
                PENDING[bukkitPlayer]!!.cancel()
            }
        }
    }

}