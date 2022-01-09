package pink.mino.kraftwerk.utils

import java.util.*

class ReplyTo {
    companion object {
        private val replied = HashMap<UUID, UUID>()

        fun setRepliedTo(u1: UUID, u2: UUID) {
            replied[u1] = u2
        }

        fun getRepliedTo(uuid: UUID): UUID? {
            return (replied[uuid])
        }
    }
}