package pink.mino.kraftwerk.utils

import org.bukkit.Location
import org.bukkit.block.BlockFace

class BlockRotation {
    companion object {
        fun getBlockFaceDirection(l: Location): BlockFace? {
            var rotation: Float = (l.yaw + 180) % 360
            if (rotation < 0) {
                rotation += 360.0F
            }
            return if (0 <= rotation && rotation < 11.25) {
                BlockFace.NORTH
            } else if (11.25 <= rotation && rotation < 33.75) {
                BlockFace.NORTH_NORTH_EAST
            } else if (33.75 <= rotation && rotation < 56.25) {
                BlockFace.NORTH_EAST
            } else if (56.25 <= rotation && rotation < 78.75) {
                BlockFace.EAST_NORTH_EAST
            } else if (78.75 <= rotation && rotation < 101.25) {
                BlockFace.EAST
            } else if (101.25 <= rotation && rotation < 123.75) {
                BlockFace.EAST_SOUTH_EAST
            } else if (123.75 <= rotation && rotation < 146.25) {
                BlockFace.SOUTH_EAST
            } else if (146.25 <= rotation && rotation < 168.75) {
                BlockFace.SOUTH_SOUTH_EAST
            } else if (168.75 <= rotation && rotation < 191.25) {
                BlockFace.SOUTH
            } else if (191.25 <= rotation && rotation < 213.75) {
                BlockFace.SOUTH_SOUTH_WEST
            } else if (213.75 <= rotation && rotation < 236.25) {
                BlockFace.SOUTH_WEST
            } else if (236.25 <= rotation && rotation < 258.75) {
                BlockFace.WEST_SOUTH_WEST
            } else if (258.75 <= rotation && rotation < 281.25) {
                BlockFace.WEST
            } else if (281.25 <= rotation && rotation < 303.75) {
                BlockFace.WEST_NORTH_WEST
            } else if (303.75 <= rotation && rotation < 326.25) {
                BlockFace.NORTH_WEST
            } else if (326.25 <= rotation && rotation < 348.75) {
                BlockFace.NORTH_NORTH_WEST
            } else if (348.75 <= rotation && rotation < 360.0) {
                BlockFace.NORTH
            } else {
                null
            }
        }
    }

}