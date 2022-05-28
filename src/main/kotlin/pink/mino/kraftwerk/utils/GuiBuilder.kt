package pink.mino.kraftwerk.utils

import java.util.HashMap
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.HandlerList
import org.bukkit.inventory.Inventory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import ws.rlns.gui
import java.util.function.Consumer

class GuiBuilder : Listener {
    private var name: String
    private var rows: Int
    private val items: HashMap<Int, ItemStack>
    private val runnableHashMap: HashMap<Int, Consumer<InventoryClickEvent>?>
    private var owner: Player? = null
    private var slot = 0
    fun rows(newRows: Int): GuiBuilder {
        rows = newRows
        return this
    }

    fun name(newName: String?): GuiBuilder {
        name = ChatColor.translateAlternateColorCodes('&', newName)
        return this
    }

    fun item(slot: Int, item: ItemStack): GuiBuilder {
        items.put(slot, item)
        this.slot = slot
        return this
    }

    fun item(slot: Int, item: ItemStack, consumer: Consumer<InventoryClickEvent>?): GuiBuilder {
        items.put(slot, item)
        this.slot = slot
        runnableHashMap.put(slot, consumer)
        return this
    }

    fun onClick(runnable: Consumer<InventoryClickEvent>?) {
        runnableHashMap.put(slot, runnable)
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked is Player) {
            val clicker = e.whoClicked as Player
            if (clicker.uniqueId.toString() == owner!!.uniqueId.toString()) {
                if (e.currentItem != null) {
                    if (e.currentItem.type != Material.AIR) {
                        if (ChatColor.stripColor(e.inventory.name).equals(ChatColor.stripColor(name), ignoreCase = true)) {
                            val slot = e.slot
                            if (runnableHashMap[slot] != null) {
                                runnableHashMap[slot]!!.accept(e)
                            }
                        }
                    }
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onPlayerClose(event: InventoryCloseEvent) {
        if (event.player is Player) {
            if (event.player.uniqueId.toString() == owner!!.uniqueId.toString()) {
                if (ChatColor.stripColor(event.inventory.name).equals(ChatColor.stripColor(name), ignoreCase = true)) {
                    HandlerList.unregisterAll(this)
                }
            }
        }
    }

    fun make(): Inventory {
        require(rows * 9 <= 54) { "Too many rows in the created inventory!" }
        val inv = Bukkit.createInventory(null, rows * 9, name)
        for (f in items.keys) {
            inv.setItem(f, items[f])
        }
        return inv
    }

    fun setOwner(owner: Player?) {
        this.owner = owner
    }

    init {
        Bukkit.getPluginManager().registerEvents(this, gui.getPlugin<gui>(gui::class.java))
        name = "Inventory"
        rows = 1
        items = HashMap()
        runnableHashMap = HashMap()
    }
}
