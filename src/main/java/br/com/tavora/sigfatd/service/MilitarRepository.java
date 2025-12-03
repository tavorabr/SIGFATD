package br.com.tavora.sigfatd.service;

import br.com.tavora.sigfatd.model.Militar;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MilitarRepository {

    private static MilitarRepository instance;
    private final Map<Integer, Militar> militares;
    private final AtomicInteger idCounter;

    private MilitarRepository() {
        this.militares = new ConcurrentHashMap<>();
        this.idCounter = new AtomicInteger(0);
    }

    public static synchronized MilitarRepository getInstance() {
        if (instance == null) {
            instance = new MilitarRepository();
        }
        return instance;
    }

    public List<Militar> getTodosMilitares() {
        final Comparator<Militar> comparadorDePostos = Comparator.comparingInt(m -> {
            switch (m.getPostoGraduacao()) {
                case "3º Sgt": return 1;
                case "Cb":     return 2;
                case "Sd EP":  return 3;
                case "Sd EV":  return 4;
                default:       return 5;
            }
        });

        return militares.values().stream()
                .sorted(comparadorDePostos)
                .collect(Collectors.toList());
    }

    public void adicionarMilitar(Militar militar) {
        if (militar != null) {
            int newId = idCounter.incrementAndGet();
            militar.setId(newId);
            militares.put(newId, militar);
        }
    }

    public Optional<Militar> buscarPorId(int id) {
        return Optional.ofNullable(militares.get(id));
    }

    public void atualizarMilitar(int id, Militar militarAtualizado) {
        if (militarAtualizado != null && militares.containsKey(id)) {
            militarAtualizado.setId(id);
            militares.put(id, militarAtualizado);
        }
    }

    public void removerMilitar(int id) {
        militares.remove(id);
    }

    public List<Militar> carregarMilitaresImportados(List<Militar> importados) {
        if (importados == null || importados.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> identidadesExistentes = militares.values().stream()
                .map(Militar::getIdtMilitar)
                .collect(Collectors.toSet());

        List<Militar> novosMilitaresAdicionados = new ArrayList<>();

        for (Militar militarImportado : importados) {
            if (!identidadesExistentes.contains(militarImportado.getIdtMilitar())) {
                adicionarMilitar(militarImportado);
                novosMilitaresAdicionados.add(militarImportado);
            }
        }

        return novosMilitaresAdicionados;
    }
}