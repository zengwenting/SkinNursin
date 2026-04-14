package com.tacomall.common.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(DotenvEnvironmentPostProcessor.class);
    private static final String DOTENV_NAME = "tacomallDotenv";
    private static final String DOTENV_FILE = ".env";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        if (propertySources.contains(DOTENV_NAME)) {
            return;
        }

        Path dotenvPath = locateDotenv();
        if (dotenvPath == null) {
            logger.debug("No .env file found from working directory upwards. Skipping local dotenv import.");
            return;
        }

        Map<String, Object> dotenvProperties = loadDotenv(dotenvPath);
        if (dotenvProperties.isEmpty()) {
            logger.debug("Dotenv file {} was found but contained no usable entries.", dotenvPath);
            return;
        }

        propertySources.addLast(new MapPropertySource(DOTENV_NAME, dotenvProperties));
        logger.info("Loaded {} entries from {}", dotenvProperties.size(), dotenvPath);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private Path locateDotenv() {
        Path current = Paths.get("").toAbsolutePath().normalize();
        while (current != null) {
            Path candidate = current.resolve(DOTENV_FILE);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
            current = current.getParent();
        }
        return null;
    }

    private Map<String, Object> loadDotenv(Path dotenvPath) {
        Map<String, Object> properties = new LinkedHashMap<>();
        try {
            List<String> lines = Files.readAllLines(dotenvPath, StandardCharsets.UTF_8);
            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("export ")) {
                    line = line.substring("export ".length()).trim();
                }

                int separator = line.indexOf('=');
                if (separator <= 0) {
                    continue;
                }

                String key = line.substring(0, separator).trim();
                if (key.isEmpty()) {
                    continue;
                }

                String value = line.substring(separator + 1).trim();
                properties.put(key, unquote(value));
            }
        } catch (IOException ex) {
            logger.warn("Failed to load dotenv file {}: {}", dotenvPath, ex.getMessage());
        }
        return properties;
    }

    private String unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
