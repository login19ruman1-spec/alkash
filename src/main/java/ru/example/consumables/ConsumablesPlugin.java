package ru.example.consumables;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConsumablesPlugin extends JavaPlugin {
    private NamespacedKey consumableKey;
    private ConsumableManager consumableManager;
    private ConsumablesGUI gui;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        consumableKey = new NamespacedKey(this, "consumable");
        consumableManager = new ConsumableManager(this);
        consumableManager.reload();

        gui = new ConsumablesGUI(this);

        getServer().getPluginManager().registerEvents(new ConsumableListener(this), this);
        getServer().getPluginManager().registerEvents(gui, this);

        AdminCommand command = new AdminCommand(this);
        getCommand("consumables").setExecutor(command);
        getCommand("consumables").setTabCompleter(command);

        getLogger().info("Consumables enabled.");
    }

    public NamespacedKey getConsumableKey() {
        return consumableKey;
    }

    public ConsumableManager getConsumableManager() {
        return consumableManager;
    }

    public ConsumablesGUI getGui() {
        return gui;
    }
}
