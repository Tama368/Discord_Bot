package services;

import config.EnvConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.News;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class NewsService {

    private static final String BASE_URL =
            "https://newsapi.org/v2/everything";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public News fetchNews() throws Exception {

        String apiKey = EnvConfig.get("NEWS_API_KEY").get(0);

        LocalDate yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1);
        String from = yesterday + "T00:00:00";
        String to   = yesterday + "T23:59:59";

        String urlString = BASE_URL
                + "?q=a"
                + "&from=" + from
                + "&to=" + to
                + "&language=en"
                + "&sortBy=popularity"
                + "&pageSize=1"
                + "&page=1"
                + "&apiKey=" + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException(
                    "News API error: HTTP " + status
            );
        }

        InputStream is = conn.getInputStream();

        JsonNode root =
                objectMapper.readTree(is);

        if (!"ok".equals(root.get("status").asText())) {
            throw new RuntimeException("News API returned non-ok status");
        }

        JsonNode articles = root.get("articles");

        if (articles == null || articles.isEmpty()) {
            return null;
        }

        JsonNode article = articles.get(0);

        return mapToNews(article);
    }

    /* =============================== */

    private News mapToNews(JsonNode article) {

        News news = new News();

        JsonNode source = article.get("source");

        if (source != null) {
            news.setSourceId(getText(source, "id"));
            news.setSourceName(getText(source, "name"));
        }

        news.setAuthor(getText(article, "author"));
        news.setTitle(getText(article, "title"));
        news.setDescription(getText(article, "description"));
        news.setUrl(getText(article, "url"));
        news.setUrlToImage(getText(article, "urlToImage"));

        String publishedStr = getText(article, "publishedAt");

        if (publishedStr != null) {
            OffsetDateTime odt = OffsetDateTime.parse(publishedStr);
            news.setPublishedAt(odt.toLocalDateTime());
        }

        news.setContent(getText(article, "content"));

        return news;
    }

    private String getText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && !value.isNull()
                ? value.asText()
                : null;
    }
}