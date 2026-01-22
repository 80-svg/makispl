package org.example.makispl.makispl;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.kyori.adventure.text.format.NamedTextColor;
import org.example.makispl.makispl.commands.coinflip;
import org.example.makispl.makispl.listeners.BlockBreakListener;
import org.example.makispl.makispl.listeners.PlayerDeathListener;
import org.example.makispl.makispl.listeners.PlayerJoinListener;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public final class Makispl extends JavaPlugin {
    public CustomCrafting customRecipe = new CustomCrafting(this);
    private Makispl plugin;
    private JDA jda;
//    Map<String, String> configMap = new HashMap<>();
    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (getConfig().getBoolean("coinflip")) {
            this.getCommand("coinflip").setExecutor(new coinflip());
        }
    boolean ENABLE_DC_BOT= getConfig().getBoolean("ENABLE_DC_BOT");
    if (ENABLE_DC_BOT) {
        try {
            // 1. Login to discord
            EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
            long targetChannelId = getConfig().getLong("TARGET_CHANNEL_ID");
            String BOT_TOKEN = getConfig().getString("BOT_TOKEN");
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
            getLogger().severe("Could not login to discord");
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
                                                    return 0;
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
                                                    return 0;
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
        getServer().addRecipe(customRecipe.emeraldShovel());
        getServer().addRecipe(customRecipe.emeraldHoe());
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
    // Plugin getter
    public Makispl getPlugin() {return plugin;}
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (jda != null) {
            jda.shutdownNow();
            try {
                jda.awaitShutdown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
