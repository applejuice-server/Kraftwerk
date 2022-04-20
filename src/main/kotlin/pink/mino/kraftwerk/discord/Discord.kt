package pink.mino.kraftwerk.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege
import net.dv8tion.jda.api.requests.GatewayIntent
import pink.mino.kraftwerk.discord.listeners.MemberJoin
import pink.mino.kraftwerk.discord.listeners.SlashCommand
import pink.mino.kraftwerk.features.SettingsFeature
import javax.security.auth.login.LoginException


class Discord : ListenerAdapter() {
    companion object {
        var instance: JDA? = null

        @Throws(LoginException::class)
        @JvmStatic
        fun main() {
            val jda = JDABuilder.createLight(
                SettingsFeature.instance.data!!.getString("discord.token"),
                GatewayIntent.GUILD_MEMBERS
            )
                .addEventListeners(SlashCommand())
                .addEventListeners(MemberJoin())
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

            val tale = CommandPrivilege(
                CommandPrivilege.Type.ROLE,
                true,
                789529302449782795L
            )
            val myth = CommandPrivilege(
                CommandPrivilege.Type.ROLE,
                true,
                789529360054353961
            )
            val legend = CommandPrivilege(
                CommandPrivilege.Type.ROLE,
                true,
                789529441047019520
            )

            commands.addCommands(
                CommandData("stats", "View the stats of another player on the server.")
                    .addOption(OptionType.STRING, "player", "The player you want to view the stats for.", true)
                    .setDefaultEnabled(false)
            )
            commands.queue()
            instance = jda
        }
    }
}