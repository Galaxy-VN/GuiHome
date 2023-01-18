package io.github.galaxyvn.guihome;

import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class GuiHome extends JavaPlugin {
    public static GuiHome plugin;

    @Getter
    private final InventoryManager manager = new InventoryManager(this);

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("GuiHome has been enabled!");
        manager.invoke();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
