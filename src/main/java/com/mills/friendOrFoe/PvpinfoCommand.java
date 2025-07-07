package com.mills.friendOrFoe;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvpinfoCommand implements CommandExecutor {

    private final PVPManager pvpManager;

    public PvpinfoCommand(PVPManager pvpManager) {
        this.pvpManager = pvpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && !sender.hasPermission("pvpmanager.admin")) {
            sender.sendMessage("§cYou must be a player or have admin permissions to run this command.");
            return false;
        }

        long timeLeft = pvpManager.getTimeLeftInCurrentPhase();
        boolean pvpEnabled = pvpManager.isPvpAllowed();
        boolean isIn24hPhase = pvpManager.isIn24hPhase();

        String message = "§7PvP Timer Info:\n";

        if (isIn24hPhase) {
            long timeRemainingIn24h = pvpManager.getTimeRemainingIn24hPhase();
            message += "§cPvP is currently disabled for 24 hours.\n";
            message += "Time left in 24-hour phase: §e" + formatTime(timeRemainingIn24h) + "§7.\n";
        } else {
            message += "§aPvP is " + (pvpEnabled ? "enabled" : "disabled") + ".\n";
            message += "Time left in the current phase: §e" + formatTime(timeLeft) + "§7.\n";
        }

        sender.sendMessage(message);
        return true;
    }

    private String formatTime(long milliseconds) {
        long hours = milliseconds / (1000 * 60 * 60);
        long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (milliseconds % (1000 * 60)) / 1000;

        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
}
