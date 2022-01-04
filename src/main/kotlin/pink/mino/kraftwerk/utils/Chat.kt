package pink.mino.kraftwerk.utils

import DefaultFontInfo
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class Chat {
    companion object {
        private val CENTER_PX = 154

        fun centerMotd(message: String): String {
            var message = message
            message = ChatColor.translateAlternateColorCodes('&', message)
            var messagePxSize = 0
            var previousCode = false
            var isBold = false
            var charIndex = 0
            var lastSpaceIndex = 0
            var toSendAfter: String? = null
            var recentColorCode = ""
            for (c in message.toCharArray()) {
                if (c == '§') {
                    previousCode = true
                    continue
                } else if (previousCode) {
                    previousCode = false
                    recentColorCode = "§$c"
                    if (c == 'l' || c == 'L') {
                        isBold = true
                        continue
                    } else {
                        isBold = false
                    }
                } else if (c == ' ') {
                    lastSpaceIndex = charIndex
                } else {
                    val dFI: DefaultFontInfo = DefaultFontInfo.getDefaultFontInfo(c)
                    messagePxSize += if (isBold) dFI.boldLength else dFI.length
                    messagePxSize++
                }
                if (messagePxSize >= 240) {
                    toSendAfter = recentColorCode + message.substring(lastSpaceIndex + 1, message.length)
                    message = message.substring(0, lastSpaceIndex + 1)
                    break
                }
                charIndex++
            }
            val halvedMessageSize = messagePxSize / 2
            val toCompensate = CENTER_PX - halvedMessageSize
            val spaceLength = DefaultFontInfo.SPACE.length + 1
            var compensated = 0
            val sb = StringBuilder()
            while (compensated < toCompensate) {
                sb.append(" ")
                compensated += spaceLength
            }
            if (toSendAfter != null) {
                centerMotd(toSendAfter)
            }
            return sb.toString() + message
        }

        fun sendCenteredMessage(player: Player, message: String?) {
            var message = message
            if (message == null || message == "") player.sendMessage("")
            message = ChatColor.translateAlternateColorCodes('&', message)
            var messagePxSize = 0
            var previousCode = false
            var isBold = false
            for (c in message.toCharArray()) {
                if (c == '§') {
                    previousCode = true
                    continue
                } else if (previousCode) {
                    previousCode = false
                    if (c == 'l' || c == 'L') {
                        isBold = true
                        continue
                    } else isBold = false
                } else {
                    val dFI: DefaultFontInfo = DefaultFontInfo.getDefaultFontInfo(c)
                    messagePxSize += if (isBold) dFI.boldLength else dFI.length
                    messagePxSize++
                }
            }
            val halvedMessageSize = messagePxSize / 2
            val toCompensate = CENTER_PX - halvedMessageSize
            val spaceLength = DefaultFontInfo.SPACE.length + 1
            var compensated = 0
            val sb = StringBuilder()
            while (compensated < toCompensate) {
                sb.append(" ")
                compensated += spaceLength
            }
            player.sendMessage(sb.toString() + message)
        }
    }
}