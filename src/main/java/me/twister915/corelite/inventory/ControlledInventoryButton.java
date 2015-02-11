package me.twister915.corelite.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ControlledInventoryButton {
    protected void onUse(Player player) {}
    protected abstract ItemStack getStack(Player player);
}
