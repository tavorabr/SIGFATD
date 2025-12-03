package br.com.tavora.sigfatd.model;

import java.util.UUID;

public class Autoridade {
    private String id;
    private String postoGraduacao = "";
    private String nomeCompleto = "";
    private String titulacao = "";

    public Autoridade() {
        this.id = UUID.randomUUID().toString();
    }

    public String getPostoGraduacao() {
        return postoGraduacao;
    }

    public void setPostoGraduacao(String postoGraduacao) {
        this.postoGraduacao = postoGraduacao;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getTitulacao() {
        return titulacao;
    }

    public void setTitulacao(String titulacao) {
        this.titulacao = titulacao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", postoGraduacao, nomeCompleto);
    }
}