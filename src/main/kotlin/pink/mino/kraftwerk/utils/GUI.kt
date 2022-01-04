package pink.mino.kraftwerk.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.util.function.Consumer

class GuiBuilder : Listener {
    private var name: String
    private var rows: Int
    private val items: HashMap<Int, ItemStack>
    private val runnableHashMap: HashMap<Int, Consumer<InventoryClickEvent>>
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
        items[slot] = item
        this.slot = slot
        return this
    }

    fun item(slot: Int, item: ItemStack, consumer: Consumer<InventoryClickEvent>): GuiBuilder {
        items[slot] = item
        this.slot = slot
        runnableHashMap[slot] = consumer
        return this
    }

    fun onClick(runnable: Consumer<InventoryClickEvent>) {
        runnableHashMap[slot] = runnable
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.currentItem != null) {
            if (e.currentItem.type != Material.AIR) {
                if (ChatColor.stripColor(e.inventory.name)
                        .equals(ChatColor.stripColor(name), ignoreCase = true)
                ) {
                    val slot = e.slot
                    if (runnableHashMap[slot] != null) {
                        runnableHashMap[slot]!!.accept(e)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerClose(event: InventoryCloseEvent) {
        if (ChatColor.stripColor(event.inventory.name)
                .equals(ChatColor.stripColor(name), ignoreCase = true)
        ) {
            HandlerList.unregisterAll(this)
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

    init {
        Bukkit.getPluginManager().registerEvents(this, getPlugin(Kraftwerk::class.java))
        name = "Inventory"
        rows = 1
        items = HashMap()
        runnableHashMap = HashMap()
    }
}