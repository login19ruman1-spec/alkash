package ru.example.consumables;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

public record ParticleData(Particle particle, int count, double x, double y, double z) {

    public static ParticleData parse(String value) {
        try {
            String[] parts = value.split(":");
            if (parts.length != 5) return null;

            Particle particle = Particle.valueOf(parts[0].toUpperCase());
            int count = Math.max(1, Integer.parseInt(parts[1]));
            double x = Double.parseDouble(parts[2]);
            double y = Double.parseDouble(parts[3]);
            double z = Double.parseDouble(parts[4]);

            return new ParticleData(particle, count, x, y, z);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void spawn(Player player) {
        player.getWorld().spawnParticle(
                particle,
                player.getLocation().add(0, 1.0, 0),
                count,
                x, y, z,
                0.02
        );
    }
}
