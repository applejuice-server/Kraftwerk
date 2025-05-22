package pink.mino.kraftwerk.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import pink.mino.kraftwerk.discord.listeners.MemberJoin
import pink.mino.kraftwerk.discord.listeners.SlashCommand
import pink.mino.kraftwerk.features.ConfigFeature
import javax.security.auth.login.LoginException


class Discord : ListenerAdapter() {
    companion object {
        var instance: JDA? = null

        fun main() {
            if (ConfigFeature.instance.config!!.getString("discord.token") == null) {
                throw(LoginException("No token found in config.yml"))
            }
            val jda = JDABuilder.createLight(
                ConfigFeature.instance.config!!.getString("discord.token"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES
            )
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(MemberJoin())
                .addEventListeners(SlashCommand())
                .build()
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
            instance = jda
            commands.queue()
        }
    }
}