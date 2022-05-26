package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.BlockUtil
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max

class AuctionScenario : Scenario(
    "Auction",
    "8 players are chosen to be \"buyers\". The \"buyers\" will receive an X amount of diamonds for bidding. The command to bid on players is /bid <# of diamonds>. After the bidding, the buyers keep the diamonds they still own.",
    "auction",
    Material.GOLD_BLOCK
), CommandExecutor {
    val owners: MutableList<String> = mutableListOf()
    val diamondAmount: HashMap<Player, Int> = hashMapOf() // stores owners and their diamonds
    val prefix = "&8[&4Auction&8]&7"
    var maxDiamonds = 50
    var highestBid = -1
    var highestBidder: Player? = null
    var currentPlayer: Player? = null
    var biddingInProgress = false
    var bidTime = 0

    val purchasedBy: HashMap<String, String> = hashMapOf()
    val cost: HashMap<String, Int> = hashMapOf()

    val areas: Map<Int, Location> = mapOf(1 to Location(Bukkit.getWorld("Spawn"), -227.0, 115.0, 10.0), 2 to Location(Bukkit.getWorld("Spawn"), -237.0, 115.0, 35.0), 3 to Location(Bukkit.getWorld("Spawn"), -262.0, 115.0, 45.0), 4 to Location(Bukkit.getWorld("Spawn"), -287.0, 115.0, 35.0), 5 to Location(Bukkit.getWorld("Spawn"), -297.0, 115.0, 10.0), 6 to Location(Bukkit.getWorld("Spawn"), -287.0, 115.0, -14.0), 7 to Location(Bukkit.getWorld("Spawn"), -262.0, 115.0, -25.0), 8 to Location(Bukkit.getWorld("Spawn"), -237.0, 115.0, -15.0))
    val playerBuying = Location(Bukkit.getWorld("Spawn"), -258.0, 96.0, 8.0)

    var task: BukkitRunnable? = null

    override fun onStart() {
        if (enabled) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "auction diamonds give")
        }
    }

    override fun onCommand(
        sender: CommandSender,
        cmd: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (!enabled) {
            Chat.sendMessage(sender, prefix + "Auction is currently disabled.")
            return false
        }
        if (cmd!!.name.equals("auction", true)) {
            if (!sender.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, prefix + "You do not have permission to use this command.")
                return false
            }
            if (args.size == 0) {
                Chat.sendMessage(sender, prefix + "Usage: &c/auction <add/remove/diamonds/refund/reset/start/stop>")
                return false
            }
            when (args[0]) {
                "add" -> {
                    if (args.size < 2) {
                        Chat.sendMessage(sender, prefix + "Usage: &c/auction add <player>")
                        return false
                    }

                    val player = Bukkit.getPlayer(args[1])
                    if (player == null) {
                        Chat.sendMessage(sender, prefix + "&cPlayer not found.")
                        return false
                    }

                    if (owners.contains(player.name.lowercase())) {
                        Chat.sendMessage(sender, prefix + "&c${player.name} &7is already an owner.")
                        return false
                    }

                    if (owners.size >= 8) {
                        Chat.sendMessage(sender, prefix + "&cThere are already 8 owners.")
                        return false
                    }

                    owners.add(player.name.lowercase())
                    player.teleport(areas[owners.size])
                    diamondAmount[player] = maxDiamonds
                    TeamsFeature.manager.createTeam(player)
                    Bukkit.broadcastMessage(prefix + "${TeamsFeature.manager.getTeam(player)!!.prefix}${player.name} &7has been made the bidder for ${TeamsFeature.manager.getTeam(player)!!.displayName}&7.")
                }
                "remove" -> {
                    if (args.size < 2) {
                        Chat.sendMessage(sender, prefix + "Usage: &c/auction remove <player>")
                        return false
                    }
                    if (!owners.contains(args[1].lowercase())) {
                        Chat.sendMessage(sender, prefix + "&c${args[1]} &7is not an owner.")
                        return false
                    }
                    owners.remove(args[1].lowercase())
                    diamondAmount.remove(Bukkit.getPlayer(args[1]))
                    Chat.sendMessage(sender, prefix + "&c${args[1]} &7is no longer an owner.")
                    val offlinePlayer = Bukkit.getOfflinePlayer(args[1])
                    TeamsFeature.manager.deleteTeam(TeamsFeature.manager.getTeam(offlinePlayer)!!)
                }
                "diamonds" -> {
                    if (args.size < 2) {
                        Chat.sendMessage(sender, prefix + "Usage: &c/auction diamonds <amount>")
                        return false
                    }
                    if (args[1].equals("give", true)) {
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (owners.contains(player.name.lowercase())) {
                                PlayerUtils.bulkItems(
                                    player,
                                    arrayListOf(ItemStack(Material.DIAMOND, diamondAmount[player]!!))
                                )
                            }
                        }
                        Chat.sendMessage(sender, prefix + "&7All buyers have been given their diamonds.")
                        return true
                    }
                    if (biddingInProgress) {
                        Chat.sendMessage(sender, prefix + "&cBidding is in progress. Should've changed this before bidding started.")
                        return false
                    }
                    try {
                        maxDiamonds = args[1].toInt()
                        if (maxDiamonds < 1) {
                            maxDiamonds = 1
                        }
                        Bukkit.broadcastMessage(Chat.colored(prefix + "The amount of diamonds has been set to &c$maxDiamonds&7."))
                    } catch (e: NumberFormatException) {
                        Chat.sendMessage(sender, prefix + "&c${args[1]} &7is not a valid number.")
                    }
                }
                "refund" -> {
                    if (args.size < 2) {
                        Chat.sendMessage(sender, prefix + "Usage: &c/auction refund <player>")
                        return false
                    }
                    if (!purchasedBy.contains(args[1].lowercase())) {
                        Chat.sendMessage(sender, prefix + "&c${args[1]} has not been purchased.")
                        return false
                    }
                    val refund = args[1]
                    val playerCost = cost[args[1].lowercase()]!!
                    val buyer = purchasedBy[args[1].lowercase()]!!

                    val player = Bukkit.getPlayer(buyer)
                    if (player == null) {
                        Chat.sendMessage(sender, prefix + "&cThe buyer '${buyer}' &7is not online to receive the refund.")
                        return false
                    }
                    PlayerUtils.bulkItems(player, arrayListOf(ItemStack(Material.DIAMOND, playerCost)))
                    Chat.sendMessage(sender, prefix + "&7The buyer '${buyer}' &7has been refunded &c$playerCost &7diamonds.")
                    Chat.sendMessage(player, prefix + "&7You have been refunded &c$playerCost &7diamonds for &c$refund&7.")
                }
                "reset" -> {
                    owners.clear()
                    diamondAmount.clear()
                    maxDiamonds = 50
                    highestBid = -1
                    highestBidder = null
                    currentPlayer = null
                    biddingInProgress = false
                    bidTime = 8
                    Chat.sendMessage(sender, prefix + "&7Auction has been reset.")
                }
                "start" -> {
                    if (task != null && Bukkit.getScheduler().isCurrentlyRunning(task!!.taskId)) {
                        Chat.sendMessage(sender, prefix + "&cAuction is already in progress.")
                        return false
                    }
                    if (biddingInProgress) {
                        Chat.sendMessage(sender, prefix + "&cBidding is already in progress..")
                        return false
                    }
                    object : BukkitRunnable() {
                        var timeLeft = 3
                        override fun run() {
                            bidTime = -1

                            if (timeLeft == 0) {
                                task = object: BukkitRunnable() {
                                    override fun run() {
                                        if (highestBid >= 0) {
                                            bidTime--
                                        }
                                        if (bidTime == 0) {
                                            val winner = highestBidder

                                            if (winner == null) {
                                                Bukkit.broadcastMessage(prefix + "&cNo one has bid on the auction, restarting...")
                                                return
                                            }

                                            val slave = currentPlayer

                                            if (slave == null) {
                                                Bukkit.broadcastMessage(prefix + "&cCouldn't find the player, restarting...")
                                                return
                                            }

                                            val team = TeamsFeature.manager.getTeam(winner)

                                            if (team == null) {
                                                Chat.sendMessage(sender, prefix + "Could not join team.")
                                                return
                                            }
                                            Bukkit.broadcastMessage(Chat.colored(prefix + "&c${slave.name} &7was sold to &c${winner.name} &7for &c${highestBid} &7diamonds."))
                                            diamondAmount[winner] = diamondAmount[winner]!! - highestBid
                                            TeamsFeature.manager.joinTeam(TeamsFeature.manager.getTeam(winner)!!.name, slave)
                                            cost[slave.name.lowercase()] = highestBid
                                            purchasedBy[slave.name.lowercase()] = winner.name.lowercase()
                                            return
                                        }
                                        if (bidTime == -1) {
                                            val list: ArrayList<Player> = arrayListOf()

                                            for (player in Bukkit.getOnlinePlayers()) {
                                                if (TeamsFeature.manager.getTeam(player) == null && !SpecFeature.instance.isSpec(player)) {
                                                    list.add(player)
                                                }
                                            }

                                            if (list.isEmpty()) {
                                                task!!.cancel()
                                                task = null
                                                Bukkit.broadcastMessage(Chat.colored(prefix + "No more players to bid on."))
                                                return
                                            }
                                            Collections.shuffle(list)

                                            currentPlayer = list[Random().nextInt(list.size)]
                                            highestBid = -1
                                            highestBidder = null
                                            bidTime = 8
                                            Bukkit.broadcastMessage(Chat.colored(prefix + "&c${currentPlayer!!.name} &7is now up for auction! Use &c/bid&7."))
                                            currentPlayer!!.teleport(playerBuying)
                                            return
                                        }
                                        if (bidTime < 4) {
                                            Bukkit.broadcastMessage(Chat.colored(prefix + "Bidding ends in &c${bidTime} &7seconds."))
                                        }
                                    }
                                }
                                task!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0, 20)
                                cancel()
                                return
                            }
                            Bukkit.broadcastMessage(Chat.colored(prefix + "Bidding starts in &c${timeLeft} &7seconds."))
                            timeLeft--
                        }
                    }.runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), 20)
                }
                "stop" -> {
                    if (task == null || !Bukkit.getScheduler().isCurrentlyRunning(task!!.taskId)) {
                        Chat.sendMessage(sender, prefix + "&cBidding is not running.")
                        return false
                    }
                    if (!biddingInProgress) {
                        Chat.sendMessage(sender, prefix + "&cBidding is not running.")
                        return false
                    }
                    task!!.cancel()
                    task = null
                    biddingInProgress = false
                    highestBid = -1
                    highestBidder = null
                    SpawnFeature.instance.send(currentPlayer!!)
                    currentPlayer = null
                    Bukkit.broadcastMessage(Chat.colored(prefix + "Bidding has been stopped!"))
                }
            }
            return true
        } else if (cmd.name.equals("bid", true)) {
            if (!biddingInProgress) {
                Chat.sendMessage(sender, prefix + "There is no auction in progress.")
                return false
            }

            if (currentPlayer == null) {
                Chat.sendMessage(sender, prefix + "There is no one to bid on.")
                return false
            }

            if (sender !is Player) {
                Chat.sendMessage(sender, prefix + "You must be a player to bid.")
                return false
            }
            if (!owners.contains(sender.name.lowercase())) {
                Chat.sendMessage(sender, prefix + "You are not a bidder.")
                return false
            }
            if (args.size != 1) {
                Chat.sendMessage(sender, prefix + "Usage: /bid <amount>")
                return false
            }
            val amount = args[0].toIntOrNull()
            if (amount == null) {
                Chat.sendMessage(sender, prefix + "Usage: /bid <amount>")
                return false
            }
            if (amount < 0) {
                Chat.sendMessage(sender, prefix + "You cannot bid a negative amount.")
                return false
            }
            if (amount > diamondAmount[sender]!!) {
                Chat.sendMessage(sender, prefix + "You cannot bid more than you have. You have ${diamondAmount[sender]!!} diamonds.")
                return false
            }

            if (amount <= highestBid) {
                Chat.sendMessage(sender, prefix + "You cannot bid less than the current highest bid. The current highest bid is ${highestBid} diamonds.")
                return false
            }

            highestBid = amount
            highestBidder = sender
            Bukkit.broadcastMessage(Chat.colored(prefix + "${TeamsFeature.manager.getTeam(sender)!!.prefix}${sender.name} &7has bid &c${amount} &7diamonds."))
            if (bidTime <= 5) {
                bidTime += 3
            }
            return true
        }
        return true
    }


}