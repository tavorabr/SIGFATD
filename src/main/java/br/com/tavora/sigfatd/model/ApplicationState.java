package br.com.tavora.sigfatd.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ApplicationState {

    private List<Militar> militares;
    private List<FATD> fatds;
    private Map<Integer, String> nups;
    private Map<LocalDate, String> feriados;
    private List<User> users;

    public List<Militar> getMilitares() {
        return militares;
    }

    public void setMilitares(List<Militar> militares) {
        this.militares = militares;
    }

    public List<FATD> getFatds() {
        return fatds;
    }

    public void setFatds(List<FATD> fatds) {
        this.fatds = fatds;
    }

    public Map<Integer, String> getNups() {
        return nups;
    }

    public void setNups(Map<Integer, String> nups) {
        this.nups = nups;
    }

    public Map<LocalDate, String> getFeriados() {
        return feriados;
    }

    public void setFeriados(Map<LocalDate, String> feriados) {
        this.feriados = feriados;
    }

    public List<User> getUsers() { return users; }

    public void setUsers(List<User> users) { this.users = users; }
}