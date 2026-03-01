package discord;

import discord.builder.GameMessageBuilder;
import discord.builder.MessageBuilder;
import discord.builder.NewsMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import models.Game;
import models.News;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordNotifier {

    private final List<JDA> bots;
    private final Map<String, List<String>> channelGroups;
    private final Map<String, MessageBuilder> builders = new HashMap<>();

    public DiscordNotifier(List<JDA> bots,
                           Map<String, List<String>> channelGroups) {

        this.bots = List.copyOf(bots);
        this.channelGroups = Map.copyOf(channelGroups);

        builders.put("games", new GameMessageBuilder());
        builders.put("news", new NewsMessageBuilder());
    }

    /* ===================================================== */

    public void sendMessage(String category, Object payload) {
        if (payload == null) return;

        List<String> channelIds = channelGroups.getOrDefault(category, List.of());
        if (channelIds.isEmpty()) {
            System.err.println("Không có channel cho category: " + category);
            return;
        }

        MessageBuilder builder = builders.get(category);
        if (builder == null) {
            throw new IllegalArgumentException("Không có builder cho category: " + category);
        }

        String message = builder.build(payload);
        if (message == null) return;

        for (JDA bot : bots) {
            for (String channelId : channelIds) {

                TextChannel channel = bot.getTextChannelById(channelId);
                if (channel == null) continue;

                channel.sendMessage(message)
                        .queue();
            }
        }
    }
}