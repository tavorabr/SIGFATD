package br.com.tavora.sigfatd.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NupRepository {

    private static NupRepository instance;
    private Map<Integer, String> nupMap;

    private NupRepository() {
        nupMap = new HashMap<>();
    }

    public static NupRepository getInstance() {
        if (instance == null) {
            instance = new NupRepository();
        }
        return instance;
    }

    public void carregarNupsImportados(Map<Integer, String> importados) {
        nupMap.clear();
        nupMap.putAll(importados);
    }

    public Optional<String> buscarNupCompleto(int fatdNumero) {
        return Optional.ofNullable(nupMap.get(fatdNumero));
    }

    public Map<Integer, String> getNupMap() {
        return nupMap;
    }

    public void adicionarNup(int fatdNumero, String nupCompleto) {
        nupMap.put(fatdNumero, nupCompleto);
    }

    public int getTotalNupsCarregados() {
        return nupMap.size();
    }
}