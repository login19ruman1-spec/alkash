package ru.example.consumables;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class ConsumableListener implements Listener {
    private final ConsumablesPlugin plugin;

    public ConsumableListener(ConsumablesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Consumable consumable = plugin.getConsumableManager().fromItem(item);

        if (consumable == null) return;

        event.setCancelled(true);
        consumable.consume(player);

        if (player.getGameMode() != GameMode.CREATIVE) {
            int amount = item.getAmount();
            if (amount <= 1) {
                player.getInventory().setItemInMainHand(null);
            } else {
                item.setAmount(amount - 1);
                player.getInventory().setItemInMainHand(item);
            }
        }

        player.updateInventory();
    }
}
