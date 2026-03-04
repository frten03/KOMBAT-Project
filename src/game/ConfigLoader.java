package game;

import java.io.*;
import java.util.*;

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

    public static GameConfig load(String path) throws IOException {

        Map<String, Long> values = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

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
                    throw new IllegalArgumentException("Unknown config key: " + name);
                }

                long value;
                try {
                    value = Long.parseLong(valueStr);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Config value must be a number: " + name);
                }

                if (value < 0) {
                    throw new IllegalArgumentException("Config value cannot be negative: " + name);
                }

                if ((name.equals("init_hp") ||
                        name.equals("max_budget") ||
                        name.equals("max_turns") ||
                        name.equals("interest_pct")) && value == 0) {

                    throw new IllegalArgumentException(name + " must be greater than 0");
                }

                values.put(name, value);
            }
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