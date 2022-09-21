package pink.mino.kraftwerk.config.options

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOption
import pink.mino.kraftwerk.utils.BlockAnimation

class MeetupBlockDecayOption : ConfigOption(
    "Block Decay @ Meetup",
    "Blocks will automatically be removed after a while at meetup.",
    "specials",
    "meetupblockdecay",
    Material.COAL_BLOCK
) {
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (!enabled) return
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game == null) return
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.timer <= JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.meetup) return
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            BlockAnimation().blockCrackAnimation(e.player, e.block, 1)
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                BlockAnimation().blockCrackAnimation(e.player, e.block, 2)
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    BlockAnimation().blockCrackAnimation(e.player, e.block, 3)
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        BlockAnimation().blockCrackAnimation(e.player, e.block, 4)
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            BlockAnimation().blockCrackAnimation(e.player, e.block, 5)
                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                BlockAnimation().blockCrackAnimation(e.player, e.block, 6)
                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                    BlockAnimation().blockCrackAnimation(e.player, e.block, 7)
                                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                        BlockAnimation().blockCrackAnimation(e.player, e.block, 8)
                                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                            BlockAnimation().blockCrackAnimation(e.player, e.block, 9)
                                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                BlockAnimation().blockBreakAnimation(null, e.block)
                                                e.block.type = Material.AIR
                                            }, 20L)
                                        }, 20L)
                                    }, 20L)
                                }, 20L)
                            }, 20L)
                        }, 20L)
                    }, 20L)
                }, 20L)
            }, 20L)
        }, 20L)
    }
}