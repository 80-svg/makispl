package org.example.makispl.makispl;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
//import io.papermc.paper.command.brigadier.CommandSourceStack;
//import io.papermc.paper.event.player.AsyncChatEvent;
//import io.papermc.paper.event.player.PlayerArmSwingEvent;
//import io.papermc.paper.plugin.bootstrap.BootstrapContext;
//import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
//import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
//import net.dv8tion.jda.api.Permission;
//import net.dv8tion.jda.api.components.actionrow.ActionRow;
//import net.dv8tion.jda.api.components.buttons.Button;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.api.interactions.IntegrationType;
//import net.dv8tion.jda.api.interactions.InteractionContextType;
//import net.dv8tion.jda.api.interactions.InteractionHook;
//import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
//import net.dv8tion.jda.api.interactions.commands.OptionMapping;
//import net.dv8tion.jda.api.interactions.commands.build.Commands;
//import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
//import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
//import org.bukkit.event.block.Action;
//import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.event.entity.FoodLevelChangeEvent;
//import org.bukkit.event.entity.PlayerDeathEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.event.player.PlayerItemConsumeEvent;
//import org.bukkit.event.player.PlayerChatEvent;
//import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.inventory.ItemStack;
//import java.util.Collection;
//import java.util.Collections;
import java.io.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.kyori.adventure.text.format.NamedTextColor;
import org.example.makispl.makispl.listeners.BlockBreakListener;
import org.example.makispl.makispl.listeners.PlayerDeathListener;
import org.example.makispl.makispl.listeners.PlayerJoinListener;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

import java.util.Scanner;

public final class Makispl extends JavaPlugin {

    private Makispl plugin;
    private JDA jda;
    Map<String, String> configMap = new HashMap<>();
    @Override
    public void onEnable() {
    File configFile = new File("makis.yml");
    String defaultVal = "BOT_TOKEN=PUT_TOKEN_HERE\n" +
                        "TARGET_CHANNEL_ID=PUT_CHANNEL";
    if (!configFile.exists()) {
        try {
            configFile.createNewFile();
            FileWriter myWriter = new FileWriter(configFile);
            myWriter.write(defaultVal);
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    try (Scanner myReader = new Scanner(configFile)) {
        while (myReader.hasNextLine()) {
            String data= myReader.nextLine();
            if (data.contains("=")) {
                int index = data.indexOf("=");
                String part1 = data.substring(index - 1);
                String part2 = data.substring(index + 1);
                configMap.put(part1, part2);
            }
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    boolean ENABLE_DC_BOT= Boolean.parseBoolean(configMap.getOrDefault("ENABLE_DC_BOT", "false"));
    final long targetChannelId = Long.parseLong(configMap.getOrDefault("TARGET_CHANNEL_ID", "1"));
        // 1446543469723259104L
    if (ENABLE_DC_BOT) {
        try {
            // 1. Login to discord
            EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
            String BOT_TOKEN = configMap.getOrDefault("BOT_TOKEN", "LOL");
            // MTQ0NjU0MzU3MzkwNTQ0MDc5OQ.GWDWFL._puyxFdNogj3wJSIATVcfGQ0F9g9HQZFo1pZUY
            jda = JDABuilder.createLight(BOT_TOKEN, intents)
                    .addEventListeners(new DiscordCommandListener(this))
                    .build();
            // 2. Wait for login
            jda.awaitReady();
            jda.updateCommands().addCommands(
                    net.dv8tion.jda.api.interactions.commands.build.Commands.slash("online", "Says who is online.")
            ).queue();
            getLogger().info("Discord bot is online!!!");
        } catch (Exception e) {
            getLogger().severe("Could not login to dicsord");
            e.printStackTrace();
        }
        plugin = this;
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(
                    literal("gethearts")
                            .then(
                                    argument("player", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                final String playerName = ctx.getArgument("player", String.class);
                                                Player targetPlayer = this.getServer().getPlayer(playerName);
                                                if (targetPlayer == null) {
                                                    ctx.getSource().getSender().sendMessage(
                                                            Component.text(playerName + " is wrong or offline")
                                                                    .color(NamedTextColor.RED)
                                                    );
                                                }
                                                assert targetPlayer != null;
                                                double health = targetPlayer.getHealth();
                                                double hearts = health / 2.0f;
                                                ctx.getSource().getSender().sendMessage(
                                                        Component.text(playerName + "'s health: " + health + " (" + hearts + " hearts)")
                                                                .color(NamedTextColor.BLACK)
                                                );
                                                return 1;
                                            })
                            )
                            .build(),
                    "Gets the hp of said player"
            );
            commands.register(
                    literal("getcoords")
                            .then(
                                    argument("player", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                final String playerName = ctx.getArgument("player", String.class);
                                                Player targetPlayer = this.getServer().getPlayer(playerName);
                                                if (targetPlayer == null) {
                                                    ctx.getSource().getSender().sendMessage(
                                                            Component.text(playerName + " is wrong or offline")
                                                                    .color(NamedTextColor.RED)
                                                    );
                                                }
                                                assert targetPlayer != null;
                                                ctx.getSource().getSender().sendMessage(
                                                        Component.text(playerName + "'s location: " + targetPlayer.getLocation())
                                                                .color(NamedTextColor.BLACK)
                                                );
                                                return 1;
                                            })
                            ).build(), "Gets the coordinates of a player"
            );
            var command = literal("onlineplayers")
                    .executes(commandContext -> {
                       String playerList = Bukkit.getOnlinePlayers().stream()
                               .map(Player::getName)
                               .collect(Collectors.joining(", "));

                       if (playerList.isEmpty()) {
                           commandContext.getSource().getSender().sendMessage(
                                   Component.text("No players online.", NamedTextColor.YELLOW)
                           );
                       } else {
                           commandContext.getSource().getSender().sendMessage(
                                   Component.text("Online: " + playerList, NamedTextColor.GREEN)
                           );
                       }
                       return 1;
                    }).build();
            event.registrar().register(command, "Lists online players", List.of("listplayers"));
        });}
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }
    public static class DiscordCommandListener extends ListenerAdapter {
        private final Makispl plugin;
        public DiscordCommandListener(Makispl plugin) {
            this.plugin = plugin;
        }
        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            if(!event.getName().equals("online")) return;

            event.deferReply().queue();

            Bukkit.getScheduler().runTask(plugin, () -> {
                String playerList = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.joining(", "));
                int count = Bukkit.getOnlinePlayers().size();
                int max = Bukkit.getMaxPlayers();

                //Format the message
                String response;
                if(playerList.isEmpty()) {
                    response = "**Status:** Server is empty :(\n" + "**Online:** 0/" + max;
                } else {
                    response = "**Status:** There are " + count + "/" + max + " players online.\n" +
                            "**Players:** " + playerList;
                }
                event.getHook().editOriginal(response).queue();
            });
        }
    }
    public Makispl getPlugin() {
        return plugin;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (jda != null) {
            jda.shutdown();
        }
    }
}
