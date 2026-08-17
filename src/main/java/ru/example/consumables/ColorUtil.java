package ru.example.consumables;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ColorUtil {
    private static final LegacyComponentSerializer SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    private ColorUtil() {}

    public static String color(String text) {
        return text == null ? "" : text.replace("&", "§");
    }

    public static Component component(String text) {
        return SERIALIZER.deserialize(text == null ? "" : text);
    }
}
