package ru.example.consumables;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class AdminCommand implements CommandExecutor, TabCompleter {
    private final ConsumablesPlugin plugin;

    public AdminCommand(ConsumablesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эту команду может использовать только игрок.");
            return true;
        }

        if (!player.hasPermission("consumables.admin")) {
            player.sendMessage(ColorUtil.component("&cНет прав."));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("menu")) {
            plugin.getGui().openPlayerMenu(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getConsumableManager().reload();
            player.sendMessage(ColorUtil.component("&aКонфигурация перезагружена."));
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 4) {
                player.sendMessage(ColorUtil.component("&cИспользование: /consumables give <игрок> <тип> <id> [количество]"));
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage(ColorUtil.component("&cИгрок не найден или не в сети."));
                return true;
            }

            ConsumableType type;
            try {
                type = ConsumableType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage(ColorUtil.component("&cТип: alcohol, vitamins, smoking"));
                return true;
            }

            Consumable consumable = plugin.getConsumableManager().get(type, args[3]);
            if (consumable == null) {
                player.sendMessage(ColorUtil.component("&cПредмет не найден."));
                return true;
            }

            int amount = 1;
            if (args.length >= 5) {
                try {
                    amount = Math.max(1, Integer.parseInt(args[4]));
                } catch (NumberFormatException e) {
                    player.sendMessage(ColorUtil.component("&cКоличество должно быть числом."));
                    return true;
                }
            }

            ItemStack stack = consumable.createItem(amount);
            target.getInventory().addItem(stack);

            player.sendMessage(ColorUtil.component("&aВыдано &f" + amount + "x &a" + args[3] + " игроку &f" + target.getName()));
            return true;
        }

        player.sendMessage(ColorUtil.component("&cИспользование: /consumables menu"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return partial(args[0], List.of("menu", "give", "reload"));
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return partial(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return partial(args[2], List.of("alcohol", "vitamins", "smoking"));
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            try {
                ConsumableType type = ConsumableType.valueOf(args[2].toUpperCase());
                return partial(args[3], plugin.getConsumableManager().getAll(type).stream().map(Consumable::id).toList());
            } catch (IllegalArgumentException ignored) {}
        }
        return List.of();
    }

    private List<String> partial(String input, List<String> values) {
        String lower = input.toLowerCase();
        return values.stream().filter(s -> s.toLowerCase().startsWith(lower)).toList();
    }
}
