package pink.mino.kraftwerk.listeners

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import me.lucko.helper.utils.Log
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.events.PvPEnableEvent
import pink.mino.kraftwerk.events.WhitelistStateChangeEvent
import pink.mino.kraftwerk.features.ConfigFeature

/**
 * @author mrcsm
 * 2022-06-16
 */
class OpenedMatchesListener : Listener {

    @EventHandler
    fun onWhitelistChange(e: WhitelistStateChangeEvent) {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("opened_matches")) {
                val match = this.find().filter(Filters.eq("id", ConfigFeature.instance.data!!.getInt("matchpost.id"))).first() ?: return
                match["whitelist"] = ConfigFeature.instance.data!!.getBoolean("whitelist.enabled")
                this.findOneAndReplace(Filters.eq("id", ConfigFeature.instance.data!!.getInt("matchpost.id")),
                match,
                FindOneAndReplaceOptions().upsert(true))
            }
        } catch (e: MongoException) {
            e.printStackTrace()
            Log.severe("Error occurred while updating game (Whitelist) in opened matches database.")
        }
    }

    @EventHandler
    fun onPvPEnable(e: PvPEnableEvent) {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getDatabase("applejuice").getCollection("opened_matches")) {
                val match = this.find().filter(Filters.eq("id", ConfigFeature.instance.data!!.getInt("matchpost.id"))).first() ?: return
                match["pvp"] = true
                this.findOneAndReplace(Filters.eq("id", ConfigFeature.instance.data!!.getInt("matchpost.id")),
                    match,
                    FindOneAndReplaceOptions().upsert(true))
            }
        } catch (e: MongoException) {
            e.printStackTrace()
            Log.severe("Error occurred while updating game (PvP) in opened matches database.")
        }
    }

}