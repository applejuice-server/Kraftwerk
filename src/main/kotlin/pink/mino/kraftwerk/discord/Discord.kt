package pink.mino.kraftwerk.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import pink.mino.kraftwerk.discord.listeners.MemberJoin
import pink.mino.kraftwerk.discord.listeners.SlashCommand
import java.util.*
import javax.security.auth.login.LoginException


class Discord : ListenerAdapter() {
    companion object {
        var instance: JDA? = null

        @Throws(LoginException::class)
        @JvmStatic
        fun main() {
            val jda = JDABuilder.createLight(
                "NzI1MTM0MzcwNzI1MTY3MTg0.XvKUAg.OeFOCpV887CJJbyNUqYBv5uMIeI",
                EnumSet.noneOf(GatewayIntent::class.java)
            )
                .addEventListeners(SlashCommand())
                .addEventListeners(MemberJoin())
                .build()

            jda.presence.activity = Activity.playing("applejuice.bar")
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
                CommandData("stats", "View the stats of a player.")
                    .addOption(OptionType.STRING, "player", "The player you want to view the stats for.", true)
            )
            commands.addCommands(
                CommandData("spectime", "View the spectating time of a player.")
                    .addOption(OptionType.STRING, "player", "The player you want to view the spectating time for.", true)
            )

            commands.queue()
            instance = jda
        }
    }
}