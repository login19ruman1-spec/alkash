package ru.example.consumables;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class ConsumableManager {
    private final ConsumablesPlugin plugin;
    private final Map<ConsumableType, Map<String, Consumable>> consumables = new EnumMap<>(ConsumableType.class);

    public ConsumableManager(ConsumablesPlugin plugin) {
        this.plugin = plugin;
        for (ConsumableType type : ConsumableType.values()) {
            consumables.put(type, new HashMap<>());
        }
    }

    public void reload() {
        for (Map<String, Consumable> map : consumables.values()) {
            map.clear();
        }

        ConfigurationSection root = plugin.getConfig().getConfigurationSection("items");
        if (root == null) return;

        for (ConsumableType type : ConsumableType.values()) {
            ConfigurationSection section = root.getConfigurationSection(type.configKey());
            if (section == null) continue;

            for (String id : section.getKeys(false)) {
                ConfigurationSection itemSection = section.getConfigurationSection(id);
                if (itemSection == null) continue;

                Consumable consumable = new Consumable(plugin, type, id, itemSection);
                consumables.get(type).put(id.toLowerCase(), consumable);
            }
        }
    }

    public Consumable get(ConsumableType type, String id) {
        return consumables.get(type).get(id.toLowerCase());
    }

    public Collection<Consumable> getAll(ConsumableType type) {
        return consumables.get(type).values();
    }

    public Consumable fromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        String value = item.getItemMeta().getPersistentDataContainer().get(
                plugin.getConsumableKey(),
                PersistentDataType.STRING
        );

        if (value == null || !value.contains(":")) return null;

        String[] parts = value.split(":", 2);
        ConsumableType type = null;

        for (ConsumableType candidate : ConsumableType.values()) {
            if (candidate.configKey().equalsIgnoreCase(parts[0])) {
                type = candidate;
                break;
            }
        }

        return type == null ? null : get(type, parts[1]);
    }
}
