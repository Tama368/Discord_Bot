package models;

import java.time.LocalDateTime;

public class Game {
    private int id;
    private String name;
    private String url;
    private LocalDateTime freeUntil;

    public Game(int id, String name, String url, LocalDateTime freeUntil) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.freeUntil = freeUntil;
    }

    public Game() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getFreeUntil() {
        return freeUntil;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFreeUntil(LocalDateTime freeUntil) {
        this.freeUntil = freeUntil;
    }
}

