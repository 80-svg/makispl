package org.example.makispl.makispl.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Random;
public class coinflip implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player) {
            Random random = new Random();
            boolean coin = random.nextBoolean();
            Player player = (Player) sender;
            if (coin) {
                player.sendMessage(Component.text("You won!!!"));
            } else {
                player.sendMessage(Component.text("You lost!!!"));
            }

        }
        return true;
    }
}
