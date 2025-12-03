package br.com.tavora.sigfatd.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import br.com.tavora.sigfatd.model.ApplicationState;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class PersistenceService {

    private static PersistenceService instance;
    private static final String DATA_FILE = "sigfatd_data.json";
    private final Gson gson;

    private PersistenceService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    public static synchronized PersistenceService getInstance() {
        if (instance == null) {
            instance = new PersistenceService();
        }
        return instance;
    }

    public void salvarDados() throws IOException {
        ApplicationState appState = new ApplicationState();
        appState.setMilitares(MilitarRepository.getInstance().getTodosMilitares());
        appState.setFatds(FATDRepository.getInstance().getHistoricoFatds());
        appState.setNups(NupRepository.getInstance().getNupMap());
        appState.setFeriados(FeriadoRepository.getInstance().getTodosFeriados());
        appState.setUsers(UserRepository.getInstance().getAllUsers());

        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(appState, writer);
        }
    }

    public void carregarDados() throws IOException {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            UserRepository.getInstance().loadUsers(null);
            System.out.println("Arquivo de dados '" + DATA_FILE + "' não encontrado. Iniciando com repositórios vazios.");
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            ApplicationState appState = gson.fromJson(reader, ApplicationState.class);

            if (appState != null) {
                if(appState.getMilitares() != null) MilitarRepository.getInstance().carregarMilitaresImportados(appState.getMilitares());
                if(appState.getFatds() != null) FATDRepository.getInstance().carregarHistorico(appState.getFatds());
                if(appState.getNups() != null) NupRepository.getInstance().carregarNupsImportados(appState.getNups());
                if(appState.getFeriados() != null) FeriadoRepository.getInstance().carregarFeriados(appState.getFeriados());
                UserRepository.getInstance().loadUsers(appState.getUsers());
            } else {
                UserRepository.getInstance().loadUsers(null);
            }
        }
    }
}