import config.EnvConfig;
import controllers.GameController;
import controllers.NewsController;
import discord.DiscordBot;
import discord.DiscordNotifier;
import log.GameLog;
import log.NewsLog;
import models.Game;
import models.News;
import services.NewsService;
import services.SteamService;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            System.out.println("=== START APPLICATION ===");

            SteamService steamService = new SteamService();
            NewsService newsService = new NewsService();
            GameLog gameLog = new GameLog(EnvConfig.get("GAME_LOG_PATH").get(0));
            NewsLog newsLog = new  NewsLog(EnvConfig.get("NEWS_LOG_PATH").get(0));

            GameController gameController = new GameController(steamService, gameLog);
            NewsController newsController = new  NewsController(newsService, newsLog);

            DiscordBot bot = new DiscordBot();
            DiscordNotifier notifier = bot.getNotifier();

            gameController.deleteOldGames();

            List<Game> newGames = gameController.getNewFreeGames();
            News news = newsController.getNews();


            if (!newGames.isEmpty()) {
                for (Game g : newGames) {
                    notifier.sendMessage("games",g);
                }
                gameController.saveNewGames(newGames);
                Thread.sleep(5000);
            } else {
                System.out.println("Không có game mới hôm nay.");
            }

            if (news != null) {
                notifier.sendMessage("news",news);
            } else {
                System.out.println("Không có news mới hôm nay.");
            }

            bot.shutdown();
            System.out.println("=== Done ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}