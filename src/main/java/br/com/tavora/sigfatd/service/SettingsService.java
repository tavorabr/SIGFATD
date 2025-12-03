package br.com.tavora.sigfatd.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import br.com.tavora.sigfatd.model.Settings;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class SettingsService {

    private static SettingsService instance;
    private final Gson gson;
    private static final String SETTINGS_FILE_NAME = "settings.json";
    private Settings settings;

    private SettingsService() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        carregarSettings();
    }

    public static synchronized SettingsService getInstance() {
        if (instance == null) {
            instance = new SettingsService();
        }
        return instance;
    }

    private void carregarSettings() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(SETTINGS_FILE_NAME)) {
            File file = new File(SETTINGS_FILE_NAME);
            Reader reader;
            if (file.exists()) {
                reader = new FileReader(file, StandardCharsets.UTF_8);
            } else if (stream != null) {
                reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            } else {
                System.err.println("AVISO: " + SETTINGS_FILE_NAME + " não encontrado. Criando um novo com valores padrão.");
                this.settings = new Settings();
                return;
            }

            this.settings = gson.fromJson(reader, Settings.class);
            if (this.settings == null) {
                this.settings = new Settings();
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("ERRO ao ler " + SETTINGS_FILE_NAME + ": " + e.getMessage());
            this.settings = new Settings();
        }
    }

    public void salvarSettings() {
        try (Writer writer = new FileWriter(SETTINGS_FILE_NAME, StandardCharsets.UTF_8)) {
            gson.toJson(this.settings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Settings getSettings() {
        return settings;
    }
}