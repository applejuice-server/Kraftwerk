package pink.mino.kraftwerk.discord.listeners

import me.lucko.helper.Schedulers
import me.lucko.helper.profiles.ProfileRepository
import me.lucko.helper.promise.Promise
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.utils.MarkdownSanitizer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.StatsHandler
import java.awt.Color
import java.util.concurrent.TimeUnit

class SlashCommand : ListenerAdapter() {

    val profileService = JavaPlugin.getPlugin(Kraftwerk::class.java).getService(ProfileRepository::class.java)
    override fun onSlashCommand(event: SlashCommandEvent) {
        if (event.guild == null) return
        val member = event.member
        val guild = event.guild
        print("Handling command ${event.name} for ${event.member!!.user.name}#${event.member!!.user.discriminator} in the Discord server.")
        when (event.name) {
            "online" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — Online players", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                embed.setDescription("There are currently **${Bukkit.getServer().onlinePlayers.size} players** online.")
                event.replyEmbeds(embed.build()).setEphemeral(false).queue()
            }
            "ip" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — IP Address", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                if (SettingsFeature.instance.data!!.getString("server.region") == "EU") {
                    embed.setDescription("The IP address to the server is :beverage_box: `eu.applejuice.bar`.")
                } else if (SettingsFeature.instance.data!!.getString("server.region") == "NA") {
                    embed.setDescription("The IP address to the server is :beverage_box: `na.applejuice.bar`.")
                }
                event.replyEmbeds(embed.build()).setEphemeral(false).queue()
            }
            "togglealerts" -> {
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — Toggle alerts", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                if (member!!.roles.contains(event.jda.getRoleById(793406242013839381))) {
                    if (guild != null) {
                        guild.getRoleById(793406242013839381)?.let { guild.removeRoleFromMember(member.id, it) }?.queue()
                    }
                    embed.setDescription("You have disabled matchpost notifications.")
                } else {
                    if (guild != null) {
                        guild.getRoleById(793406242013839381)?.let { guild.addRoleToMember(member.id, it) }?.queue()
                    }
                    embed.setDescription("You have enabled matchpost notifications.")
                }
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            }
            "wl" -> {
                val player = event.getOption("ign")!!.asString
                val target = Bukkit.getOfflinePlayer(player)
                if (target == null) event.reply("Invalid player!").setEphemeral(true).queue()
                if (SettingsFeature.instance.data!!.getBoolean("whitelist.requests")) {
                    Schedulers.async().run runnable@ {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl add $player")
                    }
                    if (SettingsFeature.instance.data!!.getString("server.region") == "EU") {
                        event.reply("**${MarkdownSanitizer.escape(player)}** has been whitelisted on the server, connect using `eu.applejuice.bar`.").queue()
                    } else if (SettingsFeature.instance.data!!.getString("server.region") == "NA") {
                        event.reply("**${MarkdownSanitizer.escape(player)}** has been whitelisted on the server, connect using `na.applejuice.bar`.").queue()
                    }
                } else {
                    event.reply("Sorry, but whitelists are not available at this time!").setEphemeral(true).queue()
                }
            }
            "scenarios" -> {
                val list = ArrayList<String>()
                for (scenario in ScenarioHandler.scenarios) {
                    list.add(scenario.name)
                }
                val embed = EmbedBuilder()
                embed.setColor(Color(255, 61, 61))
                embed.setAuthor("applejuice — Scenario List", "https://dsc.gg/apple-juice", event.jda.selfUser.avatarUrl)
                embed.setDescription("Scenarios: `${list.joinToString(", ")}`")
                event.replyEmbeds(embed.build()).setEphemeral(false).queue()
            }
            "stats" -> {
                val player = event.getOption("player")!!.asString
                profileService.lookupProfile(player)
                    .thenAcceptSync { it ->
                        if (!it.isPresent) {
                            return@thenAcceptSync event.reply("That player has never joined the server or is invalid.").queue()
                        }
                        val profile = it.get()
                        val actual = Bukkit.getOfflinePlayer(profile.uniqueId)
                        Promise.start()
                            .thenApplyAsync { StatsHandler.getStatsPlayer(actual) }
                            .thenAcceptSync { statsPlayer ->
                                val embed = EmbedBuilder()
                                val kdr = if (statsPlayer.kills != 0 && statsPlayer.deaths != 0) statsPlayer.kills / statsPlayer.deaths else "0.0"
                                embed.setColor(Color(255, 61, 61))
                                embed.setTitle("${MarkdownSanitizer.escape(actual.name)}'s stats")
                                embed.addField("PvP", "Kills: **${statsPlayer.kills}**\nDeaths: **${statsPlayer.deaths}**\nKDR: **${kdr}**\nWins: **${statsPlayer.wins}**\nGames Played: **${statsPlayer.gamesPlayed}**", true)
                                embed.addField("Misc.", "Times Enchanted: **${statsPlayer.timesEnchanted}**\nTimes Crafted: **${statsPlayer.timesCrafted}**\nGapples Eaten: **${statsPlayer.gapplesEaten}**", true)
                                embed.addField("Ores Mined", "Diamonds Mined: **${statsPlayer.diamondsMined}**\nGold Mined: **${statsPlayer.goldMined}**\nIron Mined: **${statsPlayer.ironMined}**", true)
                                embed.setThumbnail("https://crafatar.com/avatars/${profile.uniqueId}?size=128&overlay")
                                event.replyEmbeds(embed.build()).setEphemeral(false).queue()
                            }
                    }
            }
            "limit" -> {
                if (event.channel.idLong != 937811643818201098L) {
                    event.reply("Please use <#937811643818201098> for bot commands!").queue runnable@{
                        it.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
                    }
                    return
                }
                if (member == null) {
                    return
                }

                val size = event.getOption("size")

                var foundChannel = false
                var isUsersChannel = false
                var vc: VoiceChannel? = null

                for (channel in guild!!.voiceChannels) {
                    if (!channel.members.contains(member)) continue
                    foundChannel = true
                    if (channel.name.contains(member.effectiveName + member.user.discriminator, true)) isUsersChannel = true
                    vc = channel
                    break
                }
                if (!foundChannel) {
                    event.deferReply().setContent("You are not connected to a voice channel!").queue runnable@ {
                        it.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
                    }
                    return
                }

                if (!isUsersChannel) {
                    event.deferReply().setContent("You are not the owner of the VC you're connected to.").queue runnable@ {
                        it.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
                    }
                    return
                }

                var cmd: Int
                try {
                    cmd = Integer.parseInt(size.toString())
                } catch (e: NumberFormatException) {
                    event.deferReply().setContent("You did not input a valid size.").queue runnable@ {
                        it.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
                    }
                    return
                }

                if (cmd > 99) {
                    cmd = 0
                }

                vc!!.manager.setUserLimit(cmd).queue()
                if (cmd == 0) {
                    event.deferReply().setContent("You have set the user limit to `unlimited`.").queue()
                } else {
                    event.deferReply().setContent("You have changed your channel limit to `${cmd}`.").queue()
                }
            }
            else -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue()
        }
    }
}