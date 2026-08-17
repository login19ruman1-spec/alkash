package ru.example.consumables;

public enum ConsumableType {
    ALCOHOL("alcohol", "🍺 Алкоголь"),
    VITAMINS("vitamins", "💊 Витамины"),
    SMOKING("smoking", "🚬 Курево");

    private final String configKey;
    private final String displayName;

    ConsumableType(String configKey, String displayName) {
        this.configKey = configKey;
        this.displayName = displayName;
    }

    public String configKey() {
        return configKey;
    }

    public String displayName() {
        return displayName;
    }
}
