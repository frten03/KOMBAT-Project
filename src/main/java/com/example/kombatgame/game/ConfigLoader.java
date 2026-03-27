package com.example.kombatgame.game;

import java.io.*;
import java.util.*;
import java.net.URL;

// Check Config file → ConfigLoader → GameConfig → GameState
public class ConfigLoader {

    private static final Set<String> REQUIRED_KEYS = Set.of(
            "spawn_cost",
            "hex_purchase_cost",
            "init_budget",
            "init_hp",
            "turn_budget",
            "max_budget",
            "interest_pct",
            "max_turns",
            "max_spawns"
    );

    public static GameConfig load(String filename) throws IOException {
        Map<String, Long> values = new HashMap<>();

        InputStream is = null;
        BufferedReader reader = null;

        try {
            is = ConfigLoader.class.getClassLoader().getResourceAsStream(filename);
            if (is == null) {
                is = new FileInputStream(filename);
            }
            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(":");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid config line: " + line);
                }

                String name = parts[0].trim();
                String valueStr = parts[1].trim();

                if (!REQUIRED_KEYS.contains(name)) {
                    continue;
                }

                long value = Long.parseLong(valueStr);
                values.put(name, value);
            }
        } finally {
            if (reader != null) reader.close();
            if (is != null) is.close();
        }

        // check config have all key
        for (String key : REQUIRED_KEYS) {
            if (!values.containsKey(key)) {
                throw new IllegalArgumentException("Missing config value: " + key);
            }
        }

        return new GameConfig(
                values.get("spawn_cost"),
                values.get("hex_purchase_cost"),
                values.get("init_budget"),
                values.get("init_hp"),
                values.get("turn_budget"),
                values.get("max_budget"),
                values.get("interest_pct"),
                values.get("max_turns"),
                values.get("max_spawns")
        );
    }
}