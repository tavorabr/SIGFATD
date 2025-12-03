package br.com.tavora.sigfatd.service;

import br.com.tavora.sigfatd.model.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {

    private static AuditService instance;
    private static final String LOG_FILE = "auditoria.log";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditService() {
    }

    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void logAction(String action) {
        log(action, null);
    }

    public void logAction(String action, String target) {
        log(action, target);
    }

    private void log(String action, String target) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        String username = (currentUser != null) ? currentUser.getUsername() : "SISTEMA";

        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry;

        if (target != null && !target.isEmpty()) {
            logEntry = String.format("[%s] Usuário '%s': %s (Alvo: %s)", timestamp, username, action, target);
        } else {
            logEntry = String.format("[%s] Usuário '%s': %s", timestamp, username, action);
        }

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(logEntry);
        } catch (IOException e) {
            System.err.println("FALHA AO ESCREVER NO LOG DE AUDITORIA: " + e.getMessage());
        }
    }
}