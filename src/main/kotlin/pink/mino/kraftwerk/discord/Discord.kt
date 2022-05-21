package pink.mino.kraftwerk.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import pink.mino.kraftwerk.discord.listeners.MemberJoin
import pink.mino.kraftwerk.discord.listeners.MemberJoinVC
import pink.mino.kraftwerk.discord.listeners.MemberLeaveVC
import pink.mino.kraftwerk.discord.listeners.SlashCommand
import pink.mino.kraftwerk.features.SettingsFeature
import javax.security.auth.login.LoginException


class Discord : ListenerAdapter() {
    var instance: JDA
    init {
        if (SettingsFeature.instance.data!!.getString("discord.token") == null) {
            throw(LoginException("No token found in config.yml"))
        }
        val jda = JDABuilder.createLight(
            SettingsFeature.instance.data!!.getString("discord.token"),
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_VOICE_STATES
        )
            .enableCache(CacheFlag.VOICE_STATE)
            .addEventListeners(SlashCommand())
            .addEventListeners(MemberJoin())
            .addEventListeners(MemberJoinVC())
            .addEventListeners(MemberLeaveVC())
            .build()

        if (SettingsFeature.instance.data!!.getString("server.region") == "NA") {
            jda.presence.activity = Activity.playing("na.applejuice.bar")
        } else {
            jda.presence.activity = Activity.playing("eu.applejuice.bar")
        }
        val commands = jda.updateCommands()

        commands.addCommands(
            CommandData("online", "View how many players are online on the server.")
        )
        commands.addCommands(
            CommandData("ip", "View the IP for the server.")
        )
        commands.addCommands(
            CommandData("togglealerts", "Removes/adds the Notify role in the Discord server.")
        )
        commands.addCommands(
            CommandData("scenarios", "Sends a list of scenarios available on the server.")
        )
        commands.addCommands(
            CommandData("wl", "Attempts to whitelist yourself on the server if the conditions are met.")
                .addOption(OptionType.STRING, "ign", "The player you want to be whitelisted.", true)
        )
        commands.addCommands(
            CommandData("stats", "View the stats of another player on the server.")
                .addOption(OptionType.STRING, "player", "The player you want to view the stats for.", true)
                .setDefaultEnabled(false)
        )
        commands.addCommands(
            CommandData("limit", "Change the limit on your personal voice channel.")
                .addOption(OptionType.INTEGER, "size", "The size you want to set the limit to.", true)
        )
        commands.queue()
        instance = jda
    }
}