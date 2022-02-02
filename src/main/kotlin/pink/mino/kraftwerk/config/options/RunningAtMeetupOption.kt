package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class RunningAtMeetupOption : ConfigOption(
    "Running at Meetup",
    "Allows people to run away from others at meetup.",
    "rules",
    "runningatmu",
    Material.FEATHER
)