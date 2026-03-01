package discord.builder;

import models.News;

import java.time.format.DateTimeFormatter;

public class NewsMessageBuilder implements MessageBuilder {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public String build(Object payload) {

        if (!(payload instanceof News news)) {
            throw new IllegalArgumentException("Payload không phải News");
        }

        if (news.getUrl() == null) return null;

        StringBuilder sb = new StringBuilder();

        sb.append("## ")
                .append(news.getTitle() != null ? news.getTitle() : "Không có tiêu đề")
                .append("\n\n");

        sb.append("**Nguồn: **")
                .append(news.getSourceName() != null ? news.getSourceName() : "Không rõ")
                .append("\n");

        sb.append("**Tác giả: **")
                .append(news.getAuthor() != null ? news.getAuthor() : "Không rõ")
                .append("\n");

        sb.append("**Mô tả: **")
                .append(news.getDescription() != null ? news.getDescription() : "Không có")
                .append("\n");

        sb.append("**Ngày đăng: **")
                .append(news.getPublishedAt() != null ? news.getPublishedAt().format(FORMATTER) : "Không rõ")
                .append("\n");

        sb.append("**Link: **")
                .append(news.getUrl());

        return sb.toString();
    }
}