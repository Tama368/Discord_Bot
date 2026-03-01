package services;

import config.EnvConfig;
import models.Game;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SteamService {
    public List<Game> fetchFreeGames() throws Exception {

        List<Game> result = new ArrayList<>();
        String steamUrl = EnvConfig.get("STEAM_URL").get(0);

        Document doc = Jsoup.connect(steamUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/122.0.0.0 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Accept", "text/html")
                .timeout(15000)
                .get();

        // DEBUG: kiểm tra HTML trả về
        System.out.println("HTML length: " + doc.html().length());
        System.out.println(doc.html().substring(0, Math.min(500, doc.html().length())));

        Elements games = doc.select("a.search_result_row");

        for (Element e : games) {

            String title = e.select(".title").text();
            String link = e.attr("href");

            int appId = extractAppId(link);
            if (appId == -1) continue;

            LocalDateTime freeUntil = fetchFreeUntil(appId);
            if (freeUntil == null) continue;

            Game g = new Game(
                    appId,
                    title,
                    link,
                    freeUntil
            );

            result.add(g);
        }

        return result;
    }

    /* ===== LẤY NGÀY + GIỜ HẾT FREE (UTC) ===== */
    private LocalDateTime fetchFreeUntil(int appId) {
        try {
            String url = "https://store.steampowered.com/app/" + appId;
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/122.0.0.0 Safari/537.36")
                    .header("Accept-Language", "vi-VN,vi;q=0.9,en;q=0.8") // ép tiếng Việt
                    .header("Accept", "text/html")
                    .cookie("Steam_Language", "vietnamese")               // cookie ép VN
                    .timeout(15000)
                    .get();

            // DEBUG: kiểm tra HTML trả về
            System.out.println("HTML length (app " + appId + "): " + doc.html().length());
            System.out.println(doc.html().substring(0, Math.min(500, doc.html().length())));

            Element p = doc.selectFirst(".game_purchase_discount_quantity");
            if (p == null) return null;

            String text = p.text();
//            System.out.println("📄 Steam raw text: " + text);

            Pattern pattern = Pattern.compile(
                    "(?:trước|before)\\s+" +
                            "(\\d{1,2})\\s+" +
                            "(Thg\\d{2}|Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)" +
                            "(?:,\\s*(\\d{4}))?" +
                            "\\s*@\\s*" +
                            "(\\d{1,2}):(\\d{2})(am|pm)",
                    Pattern.CASE_INSENSITIVE
            );

            Matcher m = pattern.matcher(text);
            if (!m.find()) {
//                System.out.println("❌ Regex không match");
                return null;
            }

            int day = Integer.parseInt(m.group(1));
            String monthStr = m.group(2);
            int year = (m.group(3) != null)
                    ? Integer.parseInt(m.group(3))
                    : LocalDate.now().getYear();

            int hour = Integer.parseInt(m.group(4));
            int minute = Integer.parseInt(m.group(5));
            String ampm = m.group(6).toLowerCase();

            if (ampm.equals("pm") && hour != 12) hour += 12;
            if (ampm.equals("am") && hour == 12) hour = 0;

            int month = parseMonth(monthStr);

            LocalDateTime steamTime = LocalDateTime.of(year, month, day, hour, minute);
//            System.out.println("🕒 Steam time (raw): " + steamTime);

            // +15 tiếng
            LocalDateTime vnTime = steamTime.plusHours(15);
//            System.out.println("🇻🇳 VN time (+15h): " + vnTime);

            return vnTime;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private int extractAppId(String url) {
        try {
            String[] parts = url.split("/app/");
            if (parts.length < 2) return -1;

            return Integer.parseInt(
                    parts[1].split("/")[0]
            );
        } catch (Exception e) {
            return -1;
        }
    }

    private int parseMonth(String m) {
        if (m.startsWith("Thg")) {
            return Integer.parseInt(m.substring(3));
        }

        m = m.toLowerCase();

        switch (m) {
            case "jan": return 1;
            case "feb": return 2;
            case "mar": return 3;
            case "apr": return 4;
            case "may": return 5;
            case "jun": return 6;
            case "jul": return 7;
            case "aug": return 8;
            case "sep": return 9;
            case "oct": return 10;
            case "nov": return 11;
            case "dec": return 12;
            default:
                throw new IllegalArgumentException("Unknown month: " + m);
        }
    }
}

