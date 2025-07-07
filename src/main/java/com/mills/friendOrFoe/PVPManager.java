package com.mills.friendOrFoe;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class PVPManager {

    private final Main plugin;

    private boolean isIn24hPhase;
    private boolean pvpEnabled;
    private long phaseStartTime;
    private long timeLeftInCurrentPhase;

    private BukkitRunnable currentTask;
    private BukkitRunnable delayedStartTask;
    private BukkitRunnable saveConfigTask;

    private static final long PHASE_DURATION = 10 * 60 * 1000;

    public PVPManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void start() {
        startSaveConfigTask();

        if (isIn24hPhase) {
            long now = System.currentTimeMillis();
            long elapsed = now - phaseStartTime;
            long timeLeft = (24 * 60 * 60 * 1000) - elapsed;

            if (timeLeft <= 0) {
                isIn24hPhase = false;
                saveConfig();
                startPvPTimer(PHASE_DURATION);
            } else {
                Bukkit.broadcastMessage("§cPvP is disabled for another " + (timeLeft / 1000 / 60) + " minutes.");
                delayedStartTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        isIn24hPhase = false;
                        saveConfig();
                        startPvPTimer(PHASE_DURATION);
                        Bukkit.broadcastMessage("§a24 hours passed! PvP timer started.");
                    }
                };
                delayedStartTask.runTaskLater(plugin, timeLeft / 50);
            }
        } else {
            startPvPTimer(timeLeftInCurrentPhase > 0 ? timeLeftInCurrentPhase : PHASE_DURATION);
        }
    }

    public void stop() {
        if (currentTask != null) currentTask.cancel();
        if (delayedStartTask != null) delayedStartTask.cancel();
        if (saveConfigTask != null) saveConfigTask.cancel();
        saveConfig();
    }

    private void startPvPTimer(long firstDelayMillis) {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveConfig();

                currentTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        pvpEnabled = !pvpEnabled;
                        Bukkit.broadcastMessage(pvpEnabled ? "§aPvP is now ENABLED!" : "§cPvP is now DISABLED!");
                        saveConfig();
                    }
                };
                currentTask.runTaskTimer(plugin, 0L, PHASE_DURATION / 50);
            }
        }.runTaskLater(plugin, firstDelayMillis / 50);
    }

    public boolean isPvpAllowed() {
        return !isIn24hPhase && pvpEnabled;
    }

    private void startSaveConfigTask() {
        saveConfigTask = new BukkitRunnable() {
            @Override
            public void run() {
                saveConfig();
            }
        };
        saveConfigTask.runTaskTimer(plugin, 0L, 1200L);
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        isIn24hPhase = config.getBoolean("in24hPhase", true);
        pvpEnabled = config.getBoolean("isPvpCurrentlyEnabled", false);
        phaseStartTime = config.getLong("phaseStartTime", System.currentTimeMillis());
        timeLeftInCurrentPhase = config.getLong("timeLeftInCurrentPhase", PHASE_DURATION);

        if (!config.isSet("phaseStartTime")) {
            phaseStartTime = System.currentTimeMillis();
            saveConfig();
        }
    }

    private void saveConfig() {
        FileConfiguration config = plugin.getConfig();
        config.set("in24hPhase", isIn24hPhase);
        config.set("isPvpCurrentlyEnabled", pvpEnabled);
        config.set("phaseStartTime", phaseStartTime);

        if (currentTask != null) {
            config.set("timeLeftInCurrentPhase", PHASE_DURATION);
        } else {
            config.set("timeLeftInCurrentPhase", timeLeftInCurrentPhase);
        }

        plugin.saveConfig();
    }

    public boolean isIn24hPhase() {
        return isIn24hPhase;
    }

    public long getTimeLeftInCurrentPhase() {
        if (isIn24hPhase) {
            return getTimeRemainingIn24hPhase();
        }
        return timeLeftInCurrentPhase;
    }

    public long getTimeRemainingIn24hPhase() {
        long elapsed = System.currentTimeMillis() - phaseStartTime;
        long timeRemaining = (24 * 60 * 60 * 1000) - elapsed; // 24h in milliseconds
        return timeRemaining > 0 ? timeRemaining : 0;
    }
}
