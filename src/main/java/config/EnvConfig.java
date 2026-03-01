package config;

import java.util.*;

public final class EnvConfig {

    private static final Map<String, String> ENV_CACHE;

    static {
        ENV_CACHE = System.getenv();
    }

    private EnvConfig() {
    }

    public static List<String> get(String key) {
        String value = ENV_CACHE.get(key);

        if (value == null) {
            throw new IllegalStateException("Thiếu biến môi trường: " + key);
        }

//        if (value.isEmpty()) {
//            throw new IllegalStateException("Biến môi trường rỗng: " + key);
//        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}