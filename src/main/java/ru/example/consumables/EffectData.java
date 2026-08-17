package ru.example.consumables;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public record EffectData(PotionEffectType type, int amplifier, int seconds) {

    public static EffectData parse(String value) {
        try {
            String[] parts = value.split(":");
            if (parts.length != 3) return null;

            PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
            if (type == null) return null;

            int amplifier = Math.max(1, Integer.parseInt(parts[1]));
            int seconds = Math.max(1, Integer.parseInt(parts[2]));

            return new EffectData(type, amplifier, seconds);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void apply(Player player) {
        // В конфиге уровень 1 = amplifier 0, уровень 2 = amplifier 1 и т.д.
        player.addPotionEffect(new PotionEffect(
                type,
                seconds * 20,
                amplifier - 1,
                false,
                true,
                true
        ));
    }
}
