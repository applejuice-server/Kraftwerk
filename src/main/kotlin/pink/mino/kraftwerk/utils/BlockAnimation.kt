package pink.mino.kraftwerk.utils

import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation
import org.apache.commons.lang.Validate
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import kotlin.random.Random


class BlockAnimation {
    fun blockBreakAnimation(player: Player?, block: Block) {
        Validate.notNull(block, "Block cannot be null.")
        val blockPosition = BlockPosition(block.x, block.y, block.z)
        val worldServer = (block.world as CraftWorld).handle
        val blockData = worldServer.getType(blockPosition)
        worldServer.a(
            if (player == null) null else (player as CraftPlayer).handle,
            2001,
            blockPosition,
            net.minecraft.server.v1_8_R3.Block.getCombinedId(blockData)
        )
    }

    fun blockCrackAnimation(p: Player?, block: Block, stage: Int) {
        val packet = PacketPlayOutBlockBreakAnimation(Random.nextInt(1000), BlockPosition(block.x, block.y, block.z), stage)
        val dimension = (p?.world as CraftWorld).handle.dimension
        (p.server as CraftServer).handle.sendPacketNearby(
            block.x.toDouble(),
            block.y.toDouble(), block.z.toDouble(), 120.0, dimension, packet
        )
    }
}