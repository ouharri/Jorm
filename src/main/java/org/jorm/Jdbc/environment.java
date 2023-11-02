package org.jorm.Jdbc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The `env` class provides access to environment variables loaded from a .env file.
 */
public class environment {
    private static final Map<String, String> envVariables = new HashMap<>();

    private environment() {
    }

    static {
        synchronized (environment.class) {
            if (envVariables.isEmpty()) {
                String filePath = ".env";

                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().startsWith("#")) {
                            continue;
                        }

                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            envVariables.put(key, value);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error while loading the .env file", e);
                }
            }
        }
    }

    /**
     * Get the value of an environment variable by its key.
     *
     * @param key The key of the environment variable.
     * @return The value of the environment variable, or null if not found.
     */
    public static String get(String key) {
        return envVariables.get(key);
    }
}