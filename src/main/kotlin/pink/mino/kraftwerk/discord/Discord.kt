package pink.mino.kraftwerk.discord

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import pink.mino.kraftwerk.discord.listeners.MemberJoin
import pink.mino.kraftwerk.discord.listeners.SlashCommand
import java.util.*
import javax.security.auth.login.LoginException


class Discord : ListenerAdapter() {
    companion object {
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

            commands.queue()
        }
    }
}