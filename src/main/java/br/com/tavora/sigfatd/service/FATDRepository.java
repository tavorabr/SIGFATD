package br.com.tavora.sigfatd.service;

import br.com.tavora.sigfatd.model.FATD;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FATDRepository {

    private static FATDRepository instance;
    private final Map<Integer, FATD> historicoFatds;

    private FATDRepository() {
        historicoFatds = new ConcurrentHashMap<>();
    }

    public static synchronized FATDRepository getInstance() {
        if (instance == null) {
            instance = new FATDRepository();
        }
        return instance;
    }

    public void adicionarFatd(FATD fatd) {
        if (fatd != null) {
            historicoFatds.put(fatd.getNup(), fatd);
        }
    }

    public List<FATD> getHistoricoFatds() {
        return historicoFatds.values().stream()
                .sorted(Comparator.comparingInt(FATD::getNup))
                .collect(Collectors.toList());
    }

    public void carregarHistorico(List<FATD> historicoCarregado) {
        historicoFatds.clear();
        if (historicoCarregado != null) {
            Map<Integer, FATD> mapaCarregado = historicoCarregado.stream()
                    .collect(Collectors.toMap(FATD::getNup, fatd -> fatd, (existente, novo) -> novo));
            historicoFatds.putAll(mapaCarregado);
        }
    }

    public Optional<FATD> buscarFatdPorNup(int nup) {
        return Optional.ofNullable(historicoFatds.get(nup));
    }

    public void atualizarFatd(FATD fatdAtualizado) {
        adicionarFatd(fatdAtualizado);
    }

    public void removerFatd(FATD fatd) {
        if (fatd != null) {
            historicoFatds.remove(fatd.getNup());
        }
    }

//    public void removerFatdPorNup(int nup) {
//        historicoFatds.removeIf(fatd -> fatd.getNup() == nup);
//    }
}