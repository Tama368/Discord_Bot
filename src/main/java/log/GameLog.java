package log;

import models.Game;

import java.time.LocalDateTime;
import java.util.*;

public class GameLog extends AbstractJsonLog<GameLog.GameContainer> {

    public GameLog(String path) {
        super(path, GameContainer::new);
    }

    public boolean write(Game game) {
        GameContainer container = read(GameContainer.class);

        if (container.games.stream()
                .anyMatch(g -> g.getId() == game.getId())) {
            return false;
        }

        container.games.add(game);
        write(container);
        return true;
    }

    public Map<Integer, Game> readAll() {
        GameContainer container = read(GameContainer.class);

        Map<Integer, Game> map = new LinkedHashMap<>();
        for (Game g : container.games) {
            map.put(g.getId(), g);
        }

        return map;
    }

    public boolean deleteById(int id) {
        GameContainer container = read(GameContainer.class);

        boolean removed =
                container.games.removeIf(g -> g.getId() == id);

        if (removed) write(container);

        return removed;
    }

    public List<Game> getExpiredGames() {
        LocalDateTime now = LocalDateTime.now();

        return read(GameContainer.class)
                .games
                .stream()
                .filter(g -> g.getFreeUntil() != null &&
                        g.getFreeUntil().isBefore(now))
                .toList();
    }

    /* ============================= */

    public static class GameContainer {
        public List<Game> games = new ArrayList<>();
    }
}