package me.twister915.corelite;

import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

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

    public CLPlayerKnife resetPlayer() {
        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setFoodLevel(20);
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.setExp(0);
        player.setLevel(0);
        if (player.getAllowFlight()) player.setFlying(false);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());
        return this;
    }
}
