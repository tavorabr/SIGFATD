package br.com.tavora.sigfatd.model;

public class Militar {
    private int id;
    private String idtMilitar;
    private String nomeGuerra;
    private String nomeCompleto;
    private String postoGraduacao;

    public Militar(int id, String idtMilitar, String nomeGuerra, String nomeCompleto, String postoGraduacao) {
        this.id = id;
        this.idtMilitar = idtMilitar;
        this.nomeGuerra = nomeGuerra;
        this.nomeCompleto = nomeCompleto;
        this.postoGraduacao = postoGraduacao;
    }

    public Militar() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIdtMilitar() { return idtMilitar; }
    public void setIdtMilitar(String idtMilitar) { this.idtMilitar = idtMilitar; }

    public String getNomeGuerra() { return nomeGuerra; }
    public void setNomeGuerra(String nomeGuerra) { this.nomeGuerra = nomeGuerra; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getPostoGraduacao() { return postoGraduacao; }
    public void setPostoGraduacao(String postoGraduacao) { this.postoGraduacao = postoGraduacao; }

    @Override
    public String toString() {
        return String.format("%s %s - %s", postoGraduacao, nomeGuerra.toUpperCase(), nomeCompleto);
    }
}