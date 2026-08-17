package ru.example.consumables;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class Consumable {
    private final ConsumablesPlugin plugin;
    private final ConsumableType type;
    private final String id;
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final List<EffectData> effects;
    private final List<ParticleData> particles;
    private final Sound sound;

    public Consumable(ConsumablesPlugin plugin, ConsumableType type, String id, ConfigurationSection section) {
        this.plugin = plugin;
        this.type = type;
        this.id = id;
        this.material = Material.matchMaterial(section.getString("material", "PAPER"));
        this.name = ColorUtil.color(section.getString("name", id));
        this.lore = section.getStringList("lore").stream().map(ColorUtil::color).toList();

        this.effects = new ArrayList<>();
        for (String raw : section.getStringList("effects")) {
            EffectData parsed = EffectData.parse(raw);
            if (parsed != null) effects.add(parsed);
        }

        this.particles = new ArrayList<>();
        for (String raw : section.getStringList("particles")) {
            ParticleData parsed = ParticleData.parse(raw);
            if (parsed != null) particles.add(parsed);
        }

        Sound parsedSound = null;
        try {
            parsedSound = Sound.valueOf(section.getString("sound", "ENTITY_PLAYER_BURP"));
        } catch (IllegalArgumentException ignored) {
        }
        this.sound = parsedSound;
    }

    public String id() { return id; }
    public ConsumableType type() { return type; }

    public ItemStack createItem(int amount) {
        ItemStack item = new ItemStack(material, Math.max(1, Math.min(amount, material.getMaxStackSize())));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(
                plugin.getConsumableKey(),
                PersistentDataType.STRING,
                type.configKey() + ":" + id
        );

        item.setItemMeta(meta);
        return item;
    }

    public void consume(org.bukkit.entity.Player player) {
        for (EffectData effect : effects) {
            effect.apply(player);
        }

        for (ParticleData particle : particles) {
            particle.spawn(player);
        }

        if (sound != null) {
            player.getWorld().playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public List<EffectData> effects() {
        return effects;
    }
}
