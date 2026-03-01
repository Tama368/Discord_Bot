package controllers;

import log.GameLog;
import models.Game;
import services.SteamService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameController {

    private final SteamService steamService;
    private final GameLog log;

    public GameController(SteamService steamService, GameLog log) {
        this.steamService = steamService;
        this.log = log;
    }

    /* =========================================
       1) LẤY DANH SÁCH GAME FREE MỚI
       ========================================= */
    public List<Game> getNewFreeGames() throws Exception {

        // Game free hiện tại từ Steam
        List<Game> currentFreeGames = steamService.fetchFreeGames();

        // Game đã lưu trong log
        Map<Integer, Game> loggedGames = log.readAll();

        List<Game> newGames = new ArrayList<>();

        for (Game g : currentFreeGames) {
            if (!loggedGames.containsKey(g.getId())) {
                newGames.add(g);
            }
        }

        return newGames;
    }

    /* =========================================
       2) XÓA GAME HẾT HẠN KHỎI LOG
       ========================================= */
    public void deleteOldGames() {

        List<Game> expiredGames = log.getExpiredGames();

        for (Game g : expiredGames) {
            log.deleteById(g.getId());
        }
    }

    /* =========================================
       3) LƯU GAME MỚI VÀO LOG
       ========================================= */
    public void saveNewGames(List<Game> games) {
        for (Game g : games) {
            log.write(g);
        }
    }

    public List<Game> getCurrentFreeGames() throws Exception {
        return steamService.fetchFreeGames();
    }
}
