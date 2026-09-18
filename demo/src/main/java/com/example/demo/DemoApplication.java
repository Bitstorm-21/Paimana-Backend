package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		loadEnv();
		SpringApplication.run(DemoApplication.class, args);
	}

	/**
	 * Automatically loads key-value pairs from .env or demo/.env into System properties
	 * so Spring Boot can resolve ${VAR_NAME} placeholders without external plugins.
	 */
	private static void loadEnv() {
		File[] possibleLocations = new File[]{
				new File(".env"),
				new File("demo/.env"),
				new File("../.env")
		};

		for (File envFile : possibleLocations) {
			if (envFile.exists() && envFile.isFile()) {
				try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (line.isEmpty() || line.startsWith("#")) {
							continue;
						}
						int eqIdx = line.indexOf('=');
						if (eqIdx > 0) {
							String key = line.substring(0, eqIdx).trim();
							String value = line.substring(eqIdx + 1).trim();
							if ((value.startsWith("\"") && value.endsWith("\"")) ||
								(value.startsWith("'") && value.endsWith("'"))) {
								value = value.substring(1, value.length() - 1);
							}
							if (System.getProperty(key) == null && System.getenv(key) == null) {
								System.setProperty(key, value);
							}
						}
					}
				} catch (IOException ignored) {
				}
				break;
			}
		}
	}

}
