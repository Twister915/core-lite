package me.twister915.corelite;

import lombok.Data;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Data
//TODO lots todo
public final class CLPlayerKnife {
    public static CLPlayerKnife $(Player player) {
        return new CLPlayerKnife(player);
    }

    private final Player player;

    public CLPlayerKnife playSoundForPlayer(Sound sound) {
        player.playSound(player.getLocation(), sound, 20F, 0f);
        return this;
    }
}
