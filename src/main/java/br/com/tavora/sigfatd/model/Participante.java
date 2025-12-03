package br.com.tavora.sigfatd.model;

import java.util.UUID;

public class Participante {
    private String id;
    private String postoGraduacao = "";
    private String nomeCompleto = "";
    private String idtMil = "";

    public Participante() {
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

    public String getIdtMil() {
        return idtMil;
    }

    public void setIdtMil(String idtMil) {
        this.idtMil = idtMil;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", postoGraduacao, nomeCompleto);
    }

}