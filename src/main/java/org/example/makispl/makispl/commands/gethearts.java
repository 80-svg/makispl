package org.example.makispl.makispl.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class gethearts implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player) {
            Player guy = (Player) sender;
            if (args.length == 0)
            {
                guy.sendMessage(Component.text("Usage: /gethearts <player>"));
                return true;
            }
            final String playerName = args[0];
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                guy.sendMessage(Component.text("Player not found."));
                return true;
            }
            final double health = player.getHealth();
            guy.sendMessage(Component.text("Health: " + health));
        }
        return true;
    }
}
