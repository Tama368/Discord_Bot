package discord;

import config.EnvConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordBot {

    private final List<JDA> bots = new ArrayList<>();
    private final DiscordNotifier notifier;

    public DiscordBot() throws Exception {

        List<String> tokens = EnvConfig.get("DISCORD_TOKENS");

        for (String token : tokens) {
            JDA jda = JDABuilder.createDefault(token).build();

            jda.awaitReady();
            bots.add(jda);

            System.out.println("Bot connected: " + jda.getSelfUser().getName());
        }

        Map<String, List<String>> channelGroups = new HashMap<>();

        channelGroups.put("games", EnvConfig.get("DISCORD_CHANNEL_GAMES"));
        channelGroups.put("news", EnvConfig.get("DISCORD_CHANNEL_NEWS"));

        this.notifier = new DiscordNotifier(bots, channelGroups);
    }

    public DiscordNotifier getNotifier() {
        return notifier;
    }

    public void shutdown() {
        for(JDA jda : bots) {
        jda.shutdown();}
    }
}