package me.twister915.corelite.inventory;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.twister915.corelite.CLPlayerKnife;
import me.twister915.corelite.command.EmptyHandlerException;
import me.twister915.corelite.plugin.CLPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class CLInventoryGUI implements Listener {
    protected final List<Player> observers = new LinkedList<Player>();
    protected final String title;
    protected Inventory inventory;
    @Getter(AccessLevel.NONE) protected final Map<Integer, CLInventoryButton> inventoryButtons = new HashMap<Integer, CLInventoryButton>();
    @Setter(AccessLevel.NONE) protected Set<Integer> updatedSlots = new HashSet<Integer>();

    private final CLPlugin plugin;
    private boolean registered = false;

    public CLInventoryGUI(Integer size, String title, CLPlugin plugin) {
        if (size % 9 != 0) throw new IllegalArgumentException("The size of an inventory must be divisible by 9 evenly.");
        this.title = title;
        inventory = Bukkit.createInventory(null, size, title.substring(0, Math.min(32, title.length())));
        this.plugin = plugin;
    }

    public void open(Player player) {
        if (observers.contains(player)) return;
        observers.add(player);
        if (observers.size() == 1 && !registered) plugin.registerListener(this);
        registered = true;
        player.openInventory(inventory);
    }

    public void close(Player player) {
        if (!observers.contains(player)) return;
        observers.remove(player);
        if (observers.size() == 0 && registered) {
            HandlerList.unregisterAll(this);
            registered = false;
        }
        player.closeInventory();
        onClose(player);
    }

    public void open(Iterable<Player> players) {
        for (Player player : players) open(player);
    }

    public void close(Iterable<Player> players) {
        for (Player player : players) close(player);
    }

    public ImmutableList<Player> getCurrentObservers() {
        return ImmutableList.copyOf(observers);
    }

    /**
     * Adds a button, will replace default to the next available slot. Will throw an {@link java.lang.IllegalStateException} if there is no room remaining.
     */
    public void addButton(CLInventoryButton button) {
        Integer nextOpenSlot = getNextOpenSlot();
        if (nextOpenSlot == null) throw new IllegalStateException("Unable to place the button in the inventory, no room remains!");
        addButton(button, nextOpenSlot);
    }

    /**
     * Adds a button to the GUI at a specific location and will overwrite the current button at that location.
     * @param slot The slot to place that button at.
     */
    public void addButton(CLInventoryButton button, Integer slot) {
        inventoryButtons.put(slot, button);
        markForUpdate(slot);
    }

    /**
     *
     * @param button
     * @param slot
     */
    public void moveButton(CLInventoryButton button, Integer slot) {
        removeButton(button);
        addButton(button, slot);
    }

    /**
     *
     * @param button
     */
    public void markForUpdate(CLInventoryButton button) {
        markForUpdate(getSlotFor(button));
    }

    /**
     *
     * @param slot
     */
    public void markForUpdate(Integer slot) {
        updatedSlots.add(slot);
    }

    /**
     *
     * @param button
     * @return
     */
    public Integer getSlotFor(CLInventoryButton button) {
        for (Map.Entry<Integer, CLInventoryButton> integerInventoryButtonEntry : inventoryButtons.entrySet()) {
            if (integerInventoryButtonEntry.getValue().equals(button)) return integerInventoryButtonEntry.getKey();
        }
        return -1;
    }

    /**
     *
     * @param button
     */
    public void removeButton(CLInventoryButton button) {
        clearSlot(getSlotFor(button));
    }

    /**
     *
     * @param slot
     */
    public void clearSlot(Integer slot) {
        inventoryButtons.remove(slot);
        markForUpdate(slot);
    }

    public void onClose(Player onlinePlayer) {}

    public boolean isFilled(Integer slot) {return inventoryButtons.containsKey(slot);}
    /**
     *
     */
    public void updateInventory() {
        for (int x = 0; x < inventory.getSize(); x++) {
            CLInventoryButton inventoryButton = inventoryButtons.get(x);
            if (inventoryButton == null && inventory.getItem(x) != null) {
                inventory.setItem(x, null);
                continue;
            }
            if ((inventory.getItem(x) == null && inventoryButton != null) || updatedSlots.contains(x)) {
                assert inventoryButton != null;
                inventory.setItem(x, inventoryButton.getStack());
            }
        }
        for (Player observer : observers) {
            //noinspection deprecation
            observer.updateInventory();
        }
        updatedSlots = new HashSet<Integer>();
    }

    private Integer getNextOpenSlot() {
        Integer nextSlot = 0;
        for (Integer integer : inventoryButtons.keySet()) {
            if (integer.equals(nextSlot)) nextSlot = integer+1;
        }
        return nextSlot >= inventory.getSize() ? null : nextSlot;
    }

    /* Event Handlers */
    @EventHandler(priority = EventPriority.HIGH)
    public final void onPlayerLeave(PlayerQuitEvent event) {
        Player onlinePlayer = event.getPlayer();
        if (observers.contains(onlinePlayer)) this.observers.remove(onlinePlayer);
    }

    @EventHandler
    public final void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getInventory().equals(inventory)) return;
        Player player = (Player) event.getPlayer();
        this.observers.remove(player);
        onClose(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().equals(inventory))) return;
        Player player = (Player) event.getWhoClicked();
        CLInventoryButton CLInventoryButton = inventoryButtons.get(event.getSlot());
        if (player == null)
            throw new IllegalStateException("Somehow, someone who was null clicked on a slot that was null or had no button...");
        if (CLInventoryButton == null) return;
        try {
            CLInventoryButton.onPlayerClick(player, getActionTypeFor(event.getClick()));
        } catch (EmptyHandlerException e) {
            CLPlayerKnife.$(player).playSoundForPlayer(Sound.NOTE_PLING);
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerInventoryMove(InventoryMoveItemEvent event) {
        if (!event.getDestination().equals(inventory)) return;
        event.setCancelled(true);
    }

    public ImmutableList<CLInventoryButton> getButtons() {
        return ImmutableList.copyOf(inventoryButtons.values());
    }

    private static ClickAction getActionTypeFor(ClickType click) {
        switch (click) {
            case RIGHT:
            case SHIFT_RIGHT:
                return ClickAction.RIGHT_CLICK;
            default:
                return ClickAction.LEFT_CLICK;
        }
    }
}
