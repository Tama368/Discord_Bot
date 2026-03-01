package log;

import models.News;

import java.util.ArrayList;
import java.util.List;

public class NewsLog extends AbstractJsonLog<NewsLog.NewsContainer> {

    private static final String[] DEFAULT_CATEGORIES = {
            "business",
            "entertainment",
            "general",
            "health",
            "science",
            "sports",
            "technology"
    };

    public NewsLog(String path) {
        super(path, NewsContainer::new);
    }

    /* ========================================== */

    public int getCurrentIndex() {
        return read(NewsContainer.class).currentIndex;
    }

    public String getCurrentCategory() {
        NewsContainer data = read(NewsContainer.class);
        return data.categories.get(data.currentIndex).name;
    }

    public void advanceCategory() {
        NewsContainer data = read(NewsContainer.class);

        data.currentIndex =
                (data.currentIndex + 1) % data.categories.size();

        write(data);
    }

    /* ========================================== */

    public void saveNews(String categoryName, News news) {

        NewsContainer data = read(NewsContainer.class);

        for (CategoryEntry entry : data.categories) {
            if (entry.name.equals(categoryName)) {
                entry.news = news;
                write(data);
                return;
            }
        }

        throw new IllegalArgumentException("Invalid category");
    }

    public News getNews(String categoryName) {

        NewsContainer data = read(NewsContainer.class);

        for (CategoryEntry entry : data.categories) {
            if (entry.name.equals(categoryName)) {
                return entry.news;
            }
        }

        return null;
    }

    /* ========================================== */
    /* ========= JSON STRUCTURE CLASSES ========= */
    /* ========================================== */

    public static class NewsContainer {
        public int currentIndex = 0;
        public List<CategoryEntry> categories = new ArrayList<>();

        public NewsContainer() {
            for (String c : DEFAULT_CATEGORIES) {
                categories.add(new CategoryEntry(c, null));
            }
        }
    }

    public static class CategoryEntry {
        public String name;
        public News news;

        public CategoryEntry() {
        }

        public CategoryEntry(String name, News news) {
            this.name = name;
            this.news = news;
        }
    }
}