package controllers;

import log.NewsLog;
import models.News;
import services.NewsService;

public class NewsController {

    private final NewsService service;
    private final NewsLog log;

    public NewsController(NewsService newsService, NewsLog newsLog) {
        this.service = newsService;
        this.log = newsLog;
    }

    public News getNews() throws Exception {

        log.advanceCategory();

        String category = log.getCurrentCategory();

        News latestNews = service.fetchNews(category);

        if (latestNews == null) {
            return null;
        }

        News oldNews = log.getNews(category);

        if (isSameNews(latestNews, oldNews)) {
            return null;
        }

        log.saveNews(category, latestNews);

        return latestNews;
    }

    /* ========================= */

    private boolean isSameNews(News n1, News n2) {

        if (n1 == null || n2 == null) {
            return false;
        }

        // So sánh theo URL là an toàn nhất
        if (n1.getUrl() != null && n1.getUrl().equals(n2.getUrl())) {
            return true;
        }

        // fallback nếu URL null
        if (n1.getTitle() != null &&
                n1.getTitle().equals(n2.getTitle())) {
            return true;
        }

        return false;
    }
}