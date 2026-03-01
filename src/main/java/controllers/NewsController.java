package controllers;

import models.News;
import services.NewsService;

public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    public News getNews() throws Exception {
        return newsService.fetchNews();
    }
}
