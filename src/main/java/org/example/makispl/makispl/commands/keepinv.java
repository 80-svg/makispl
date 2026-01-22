package org.example.makispl.makispl.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.makispl.makispl.Makispl;
import org.jetbrains.annotations.NotNull;

public class keepinv implements CommandExecutor {
    final Makispl makispl;

    public keepinv(Makispl makispl) {
        this.makispl = makispl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (makispl.isKeepInv()) {
                makispl.setKeepInv(false);
                player.sendMessage("KeepInv set to false");
            } else {
                makispl.setKeepInv(true);
                player.sendMessage("KeepInv set to true");
            }
        }
        return true;
    }
}
