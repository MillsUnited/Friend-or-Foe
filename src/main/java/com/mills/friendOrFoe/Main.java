package com.mills.friendOrFoe;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private PVPManager pvpManager;
    private LivesManager livesManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        livesManager = new LivesManager(this.getDataFolder());
        pvpManager = new PVPManager(this);
        pvpManager.start();

        getCommand("pvpinfo").setExecutor(new PvpinfoCommand(pvpManager));

        Bukkit.getPluginManager().registerEvents(new DamageListener(pvpManager), this);
        Bukkit.getPluginManager().registerEvents(new LivesListener(livesManager), this);
    }

    @Override
    public void onDisable() {
        if (pvpManager != null) {
            pvpManager.stop();
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public LivesManager getLivesManager() {
        return livesManager;
    }
}
