package discord.builder;

import models.Game;

import java.time.format.DateTimeFormatter;

public class GameMessageBuilder implements MessageBuilder {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public String build(Object payload) {

        if (!(payload instanceof Game game)) {
            throw new IllegalArgumentException("Payload không phải Game");
        }

        if (game.getUrl() == null) return null;

        return "## "
                + (game.getName() != null ? game.getName() : "Không rõ")
                +"\n\n"
                + "**Hết miễn phí: **"
                + (game.getFreeUntil() != null
                ? game.getFreeUntil().format(FORMATTER)
                : "Không rõ")
                + "\n**Link: **"
                + game.getUrl();

    }
}