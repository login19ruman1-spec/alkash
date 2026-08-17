package ru.example.consumables;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConsumablesGUI implements Listener {
    private final ConsumablesPlugin plugin;
    private final Map<Player, Player> selectedTarget = new HashMap<>();

    public ConsumablesGUI(ConsumablesPlugin plugin) {
        this.plugin = plugin;
    }

    public void openPlayerMenu(Player admin) {
        String title = plugin.getConfig().getString("gui.player-title", "&8Выберите игрока");
        Inventory inv = Bukkit.createInventory(null, 54, ColorUtil.component(title));

        int slot = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (slot >= 45) break;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = head.getItemMeta();
            meta.setDisplayName(ColorUtil.color("&e" + target.getName()));
            meta.setLore(List.of(ColorUtil.color("&7Нажмите, чтобы выбрать игрока")));
            if (meta instanceof org.bukkit.inventory.meta.SkullMeta skull) {
                skull.setOwningPlayer(target);
            }
            head.setItemMeta(meta);

            inv.setItem(slot++, head);
        }

        admin.openInventory(inv);
    }

    private void openCategoryMenu(Player admin, Player target) {
        selectedTarget.put(admin, target);

        String title = plugin.getConfig().getString("gui.title", "&8Расходники");
        Inventory inv = Bukkit.createInventory(null, 27, ColorUtil.component(title));

        inv.setItem(11, simple(Material.POTION, "&e🍺 Алкоголь"));
        inv.setItem(13, simple(Material.GOLD_NUGGET, "&a💊 Витамины"));
        inv.setItem(15, simple(Material.PAPER, "&7🚬 Курево"));

        admin.openInventory(inv);
    }

    private void openItemsMenu(Player admin, ConsumableType type) {
        Player target = selectedTarget.get(admin);
        if (target == null) {
            openPlayerMenu(admin);
            return;
        }

        String title = plugin.getConfig().getString("gui.item-title", "&8Выберите предмет");
        Inventory inv = Bukkit.createInventory(null, 54, ColorUtil.component(title));

        int slot = 0;
        for (Consumable consumable : plugin.getConsumableManager().getAll(type)) {
            if (slot >= 45) break;

            ItemStack item = consumable.createItem(1);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>(meta.getLore() == null ? List.of() : meta.getLore());
            lore.add("");
            lore.add(ColorUtil.color("&aНажмите, чтобы выдать игроку"));
            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot++, item);
        }

        admin.openInventory(inv);
    }

    private ItemStack simple(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.color(name));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin)) return;
        if (!(event.getView().title() instanceof net.kyori.adventure.text.Component)) return;

        String title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(event.getView().title());

        event.setCancelled(true);

        if (title.equals(strip(plugin.getConfig().getString("gui.player-title", "&8Выберите игрока")))) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;

            ItemMeta meta = clicked.getItemMeta();
            if (meta == null || meta.getDisplayName() == null) return;

            String name = meta.getDisplayName().replace("§e", "");
            Player target = Bukkit.getPlayerExact(name);
            if (target != null) openCategoryMenu(admin, target);
            return;
        }

        if (title.equals(strip(plugin.getConfig().getString("gui.title", "&8Расходники")))) {
            ConsumableType type = switch (event.getSlot()) {
                case 11 -> ConsumableType.ALCOHOL;
                case 13 -> ConsumableType.VITAMINS;
                case 15 -> ConsumableType.SMOKING;
                default -> null;
            };
            if (type != null) openItemsMenu(admin, type);
            return;
        }

        if (title.equals(strip(plugin.getConfig().getString("gui.item-title", "&8Выберите предмет")))) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null) return;

            Consumable consumable = plugin.getConsumableManager().fromItem(clicked);
            Player target = selectedTarget.get(admin);

            if (consumable == null || target == null) return;

            target.getInventory().addItem(consumable.createItem(1));
            admin.sendMessage(ColorUtil.component("&aВыдан предмет &f" + consumable.id()
                    + " &aигроку &f" + target.getName()));
        }
    }

    private String strip(String text) {
        return ColorUtil.color(text).replaceAll("§.", "");
    }
}
