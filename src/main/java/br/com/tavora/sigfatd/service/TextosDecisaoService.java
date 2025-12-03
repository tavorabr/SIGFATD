package br.com.tavora.sigfatd.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextosDecisaoService {

    private static TextosDecisaoService instance;
    private final Gson gson;
    private Map<String, Object> textosProntos;

    private static final String FILE_NAME = "/textos_decisao.json";

    private TextosDecisaoService() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        carregarTextos();
    }

    public static TextosDecisaoService getInstance() {
        if (instance == null) {
            instance = new TextosDecisaoService();
        }
        return instance;
    }

    private void carregarTextos() {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(FILE_NAME))) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            textosProntos = gson.fromJson(reader, type);
            if (textosProntos == null) {
                textosProntos = new LinkedHashMap<>();
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar textos de decisão: " + e.getMessage());
            textosProntos = new LinkedHashMap<>();
        }
    }

    public Map<String, Object> getTextosProntos() {
        return Collections.unmodifiableMap(textosProntos);
    }
}