package br.com.tavora.sigfatd.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Settings {

    private int proximoNup = 1;
    private List<Participante> participantes;
    private List<Autoridade> autoridades;
    private int fontSizeModifier = 0;
    private int anoReferencia;

    public Settings() {
        if (participantes == null) participantes = new ArrayList<>();
        if (autoridades == null) autoridades = new ArrayList<>();
        this.anoReferencia = LocalDate.now().getYear();
    }


    public int getProximoNup() { return proximoNup; }
    public void setProximoNup(int proximoNup) { this.proximoNup = proximoNup; }

    public List<Participante> getParticipantes() {
        if (participantes == null) participantes = new ArrayList<>();
        return participantes;
    }
    public void setParticipantes(List<Participante> participantes) { this.participantes = participantes; }

    public List<Autoridade> getAutoridades() {
        if (autoridades == null) autoridades = new ArrayList<>();
        return autoridades;
    }
    public void setAutoridades(List<Autoridade> autoridades) { this.autoridades = autoridades; }

    public int getFontSizeModifier() {
        return fontSizeModifier;
    }
    public void setFontSizeModifier(int fontSizeModifier) {
        this.fontSizeModifier = fontSizeModifier;
    }

    public int getAnoReferencia() {
        return anoReferencia;
    }

    public void setAnoReferencia(int anoReferencia) {
        this.anoReferencia = anoReferencia;
    }
}