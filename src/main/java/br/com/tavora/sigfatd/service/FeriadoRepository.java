package br.com.tavora.sigfatd.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class FeriadoRepository {

    private static FeriadoRepository instance;
    private Map<LocalDate, String> feriados;

    private FeriadoRepository() {
        feriados = new HashMap<>();
    }

    public static FeriadoRepository getInstance() {
        if (instance == null) {
            instance = new FeriadoRepository();
        }
        return instance;
    }

    public Map<LocalDate, String> getTodosFeriados() {
        return feriados;
    }

    public void adicionarFeriado(LocalDate data, String nome) {
        feriados.put(data, nome);
    }

    public void removerFeriado(LocalDate data) {
        feriados.remove(data);
    }

    public boolean isFeriado(LocalDate data) {
        return feriados.containsKey(data);
    }

    public void carregarFeriados(Map<LocalDate, String> feriadosCarregados) {
        if (feriadosCarregados != null) {
            feriados.clear();
            feriados.putAll(feriadosCarregados);
        }
    }
}