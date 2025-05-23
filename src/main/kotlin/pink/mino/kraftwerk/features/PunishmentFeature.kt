package pink.mino.kraftwerk.features

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import com.mongodb.client.model.Sorts
import me.lucko.helper.Schedulers
import net.dv8tion.jda.api.EmbedBuilder
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.utils.Chat
import java.util.*

enum class PunishmentType {
    BAN,
    MUTE,
    HELPOP_MUTE,
    DISQUALIFICATION,
    WARN,
    KICK
}

class Punishment(
    val uuid: UUID,
    val punisherUuid: UUID,
    val type: PunishmentType,
    val expiresAt: Long,
    val reason: String,
    val silent: Boolean, // Will not announce the punishment in public, will say the reason to the player in question.
    val punishedAt: Long,
    val revoked: Boolean
)

class PunishmentFeature {
    companion object {
        fun timeToString(milliseconds: Long): String {
            var secondsTotal = milliseconds / 1000
            val months = (secondsTotal / (30L * 24 * 3600)).toInt()
            secondsTotal %= 30L * 24 * 3600

            val weeks = (secondsTotal / (7L * 24 * 3600)).toInt()
            secondsTotal %= 7L * 24 * 3600

            val days = (secondsTotal / (24 * 3600)).toInt()
            secondsTotal %= 24 * 3600

            val hours = (secondsTotal / 3600).toInt()
            secondsTotal %= 3600

            val minutes = (secondsTotal / 60).toInt()
            val seconds = (secondsTotal % 60).toInt()

            val parts = mutableListOf<String>()
            if (months > 0) parts.add("${months}mo")
            if (weeks > 0) parts.add("${weeks}w")
            if (days > 0) parts.add("${days}d")
            if (hours > 0) parts.add("${hours}h")
            if (minutes > 0) parts.add("${minutes}m")
            if (parts.isEmpty() || seconds > 0) parts.add("${seconds}s")

            return parts.joinToString(" ")
        }

        fun parseDurationToMillis(input: String): Long? {
            val regex = Regex("""(\d+)(mo|[mhdw])""", RegexOption.IGNORE_CASE)
            val match = regex.matchEntire(input.trim()) ?: return null

            val (numberStr, unit) = match.destructured
            val number = numberStr.toLongOrNull() ?: return null

            val millisPerUnit = when (unit.lowercase()) {
                "m", "min"  -> 60L * 1000L                  // minutes to millis
                "h", "hrs", "hr"  -> 60L * 60L * 1000L           // hours to millis
                "d", "day", "days"  -> 24L * 60L * 60L * 1000L    // days to millis
                "w"  -> 7L * 24L * 60L * 60L * 1000L // weeks to millis
                "mo", "mon", "month" -> 30L * 24L * 60L * 60L * 1000L // months (30 days)
                else -> return null
            }

            return number * millisPerUnit
        }

        fun revokePunishment(punishmentUuid: UUID, type: PunishmentType): Boolean {
            val plugin = JavaPlugin.getPlugin(Kraftwerk::class.java)
            val collection = plugin.dataSource.getDatabase("applejuice").getCollection("punishments")

            val filter = Filters.and(
                Filters.eq("playerUniqueId", punishmentUuid),
                Filters.eq("type", type.toString()),
                Filters.eq("revoked", false)
            )

            val newestPunishment = collection.find(filter)
                .sort(Sorts.descending("punishedAt"))
                .firstOrNull()

            if (newestPunishment != null) {
                Bukkit.getLogger().info(newestPunishment.toString())
                val updateResult = collection.updateOne(
                    Filters.eq("uuid", newestPunishment.get("uuid", UUID::class.java)),
                    Document("\$set", Document("revoked", true))
                )
                Bukkit.getLogger().info("${updateResult.modifiedCount > 0}")
                return updateResult.modifiedCount > 0
            }
            Bukkit.getLogger().info("nope")
            return false
        }

        fun getActivePunishment(player: OfflinePlayer, punishmentType: PunishmentType): Punishment? {
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("punishments")) {
                val filter = Filters.and(
                    Filters.eq("playerUniqueId", player.uniqueId),
                    Filters.eq("type", punishmentType.toString()),
                    Filters.eq("revoked", false)
                )
                val sort = Sorts.descending("punishedAt")

                val document = this.find(filter).sort(sort).first() ?: return null
                val expiresAt = document["expiresAt"] as Long
                if (expiresAt <= System.currentTimeMillis()) return null

                return Punishment(
                    document["playerUniqueId"] as UUID,
                    document["punisherUniqueId"] as UUID,
                    PunishmentType.valueOf(document["type"] as String),
                    expiresAt,
                    document["reason"] as String,
                    document["silent"] as Boolean,
                    document["punishedAt"] as Long,
                    document["revoked"] as Boolean
                )
            }
        }

        fun hasActivePunishment(player: OfflinePlayer, punishmentType: PunishmentType): Boolean {
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("punishments")) {
                val filter = Filters.and(
                    Filters.eq("playerUniqueId", player.uniqueId),
                    Filters.eq("type", punishmentType.toString()),
                    Filters.eq("revoked", false)
                )
                val sort = Sorts.descending("punishedAt")
                val document = this.find(filter).sort(sort).first() ?: return false
                val expiresAt = document["expiresAt"] as Long
                return expiresAt > System.currentTimeMillis()
            }
        }

        fun punish(player: OfflinePlayer, punishment: Punishment) {
            if (player.isOnline) {
                if (punishment.type == PunishmentType.KICK) {
                    val punisher = Bukkit.getOfflinePlayer(punishment.punisherUuid)
                    (player as Player).kickPlayer(Chat.colored("${Chat.primaryColor}${Chat.scoreboardTitle}\n${Chat.line}\n\n&7You've been kicked from the server by ${Chat.secondaryColor}${punisher.name}&7.\n&7Reason: ${Chat.secondaryColor}${punishment.reason}\n\n${Chat.line}"))
                }
                if (punishment.type == PunishmentType.WARN) {
                    (player as Player).sendTitle(Chat.colored("&4WARNING!"), Chat.colored("&7${punishment.reason}"))
                    Chat.sendMessage(player, Chat.line)
                    Chat.sendCenteredMessage(player, "&4&lWARNING!")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7You've been warned by ${Chat.secondaryColor}${Bukkit.getOfflinePlayer(punishment.punisherUuid).name}&7!")
                    Chat.sendCenteredMessage(player, "&7Further warnings may result in further action.")
                    Chat.sendCenteredMessage(player, "&7Reason: ${Chat.secondaryColor}${punishment.reason}")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.ANVIL_LAND, 1f, 1f)
                }
                if (punishment.type == PunishmentType.DISQUALIFICATION) {
                    (player as Player).sendTitle(Chat.colored("&4DISQUALIFIED!"), Chat.colored("&7${punishment.reason})"))
                    Chat.sendMessage(player, Chat.line)
                    Chat.sendCenteredMessage(player, "&4&lDISQUALIFIED!")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7You've been disqualified by ${Chat.secondaryColor}${Bukkit.getOfflinePlayer(punishment.punisherUuid).name}&7!")
                    Chat.sendCenteredMessage(player, "&7Reason: ${Chat.secondaryColor}${punishment.reason}")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.WITHER_DEATH, 1f, 1f)
                    player.damage(player.maxHealth * 9999.0)
                }
                if (punishment.type == PunishmentType.HELPOP_MUTE) {
                    Chat.sendMessage((player as Player), Chat.line)
                    Chat.sendCenteredMessage(player, "&4&lHELPOP MUTED!")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7You've been helpop muted by ${Chat.secondaryColor}${Bukkit.getOfflinePlayer(punishment.punisherUuid).name}&7!")
                    Chat.sendCenteredMessage(player, "&7You may no longer use ${Chat.secondaryColor}/helpop&7 to ask for help from Staff.")
                    Chat.sendCenteredMessage(player, "&7This punishment expires in: ${Chat.secondaryColor}${timeToString(punishment.expiresAt - System.currentTimeMillis())}&7.")
                    Chat.sendCenteredMessage(player, "&7Reason: ${Chat.secondaryColor}${punishment.reason}")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.NOTE_BASS, 1f, 1f)
                }
                if (punishment.type == PunishmentType.MUTE) {
                    Chat.sendMessage((player as Player), Chat.line)
                    Chat.sendCenteredMessage(player, "&4&lMUTED!")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7You've been muted by ${Chat.secondaryColor}${Bukkit.getOfflinePlayer(punishment.punisherUuid).name}&7!")
                    Chat.sendCenteredMessage(player, "&7You may no longer talk in chat.")
                    Chat.sendCenteredMessage(player, "&7This punishment expires in: ${Chat.secondaryColor}${timeToString(punishment.expiresAt - System.currentTimeMillis())}&7.")
                    Chat.sendCenteredMessage(player, "&7Reason: ${Chat.secondaryColor}${punishment.reason}")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.NOTE_BASS, 1f, 1f)
                }
                if (punishment.type == PunishmentType.BAN) {
                    val punisher = Bukkit.getOfflinePlayer(punishment.punisherUuid)
                    (player as Player).kickPlayer(Chat.colored("${Chat.primaryColor}${Chat.scoreboardTitle}\n${Chat.line}\n\n&7You've been banned from the server by ${Chat.secondaryColor}${punisher.name}&7.\n&7Your ban expires in ${Chat.secondaryColor}${timeToString(punishment.expiresAt - System.currentTimeMillis())}&7.\n&7Reason: ${Chat.secondaryColor}${punishment.reason}\n\n${Chat.line}"))
                }
            }
            Schedulers.async().supply {
                try {
                    with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("punishments")) {
                        val uuid = UUID.randomUUID()
                        val document = Document("uuid", uuid)
                            .append("playerUniqueId", punishment.uuid)
                            .append("punisherUniqueId", punishment.punisherUuid)
                            .append("type", punishment.type.toString())
                            .append("expiresAt", punishment.expiresAt)
                            .append("reason", punishment.reason)
                            .append("silent", punishment.silent)
                            .append("punishedAt", punishment.punishedAt)
                            .append("revoked", false)
                        this.findOneAndReplace(Filters.eq("uuid", uuid), document, FindOneAndReplaceOptions().upsert(true))
                        val embed = EmbedBuilder()
                        val punishedName = Bukkit.getOfflinePlayer(punishment.uuid).name ?: "Unknown"
                        val punisherName = Bukkit.getOfflinePlayer(punishment.punisherUuid).name ?: "Unknown"
                        val expiresIn = timeToString(punishment.expiresAt - System.currentTimeMillis())
                        val date = java.time.Instant.ofEpochMilli(punishment.punishedAt)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

                        embed.setThumbnail("https://visage.surgeplay.com/face/512/${punishedName}")
                        embed.setTitle("$punishedName's ${punishment.type}")

                        embed.setDescription("""
                            Type: **${punishment.type}**
                            Player: **$punishedName**
                            Punisher: **$punisherName**
                            Reason: **${punishment.reason}**
                            Silent: **${if (punishment.silent) "Yes" else "No"}**
                            Expires in: **$expiresIn**
                            Issued at: **$date**
                            """.trimIndent()
                        )

                        embed.setColor(
                            when (punishment.type) {
                                PunishmentType.BAN -> java.awt.Color.RED
                                PunishmentType.MUTE -> java.awt.Color.YELLOW
                                PunishmentType.HELPOP_MUTE -> java.awt.Color.ORANGE
                                else -> java.awt.Color.GRAY
                            }
                        )

                        embed.setFooter("UUID: ${punishment.uuid}")

                        Kraftwerk.instance.punishmentChannelId!!.let { channelId ->
                            Discord.instance!!.getTextChannelById(channelId)!!.sendMessageEmbeds(embed.build()).queue()
                        }
                    }
                } catch (e: MongoException) {
                    e.printStackTrace()
                }
                return@supply null
            }
        }
    }
}