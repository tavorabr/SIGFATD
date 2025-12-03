package br.com.tavora.sigfatd.model;

import java.time.LocalDate;

public class FATD {
    private Militar militar;
    private int nup;
    private String nupCompleto;
    private LocalDate dataProcesso;
    private String referencia;
    private String relatoFato;
    private String decisaoPunicao;
    private String notaBi = "";
    private String bi = "";
    private int quantidadeDias = 0;
    private String punicao = "";
    private boolean concluido;
    private boolean defesaEntregue = false;
    private boolean decisaoFeita = false;

    public FATD(Militar militar, int nup, String nupCompleto, LocalDate dataProcesso, String referencia, String relatoFato, String decisaoPunicao) {
        this.militar = militar;
        this.nup = nup;
        this.nupCompleto = nupCompleto;
        this.dataProcesso = dataProcesso;
        this.referencia = referencia;
        this.relatoFato = relatoFato;
        this.decisaoPunicao = decisaoPunicao;
        this.notaBi = "";
        this.bi = "";
        this.quantidadeDias = 0;
        this.punicao = "";
        this.concluido = false;
        this.defesaEntregue = false;
        this.decisaoFeita = false;
    }

    public FATD() {
        this.nupCompleto = "";
        this.referencia = "";
        this.relatoFato = "";
        this.decisaoPunicao = "";
        this.notaBi = "";
        this.bi = "";
        this.quantidadeDias = 0;
        this.punicao = "";
        this.concluido = false;
        this.defesaEntregue = false;
        this.decisaoFeita = false;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    public boolean isDefesaEntregue() {
        return defesaEntregue;
    }

    public void setDefesaEntregue(boolean defesaEntregue) {
        this.defesaEntregue = defesaEntregue;
    }

    public boolean isDecisaoFeita() {
        return decisaoFeita;
    }

    public void setDecisaoFeita(boolean decisaoFeita) {
        this.decisaoFeita = decisaoFeita;
    }

    public Militar getMilitar() { return militar; }
    public void setMilitar(Militar militar) { this.militar = militar; }
    public int getNup() { return nup; }
    public void setNup(int nup) { this.nup = nup; }
    public String getNupCompleto() { return nupCompleto; }
    public void setNupCompleto(String nupCompleto) { this.nupCompleto = nupCompleto; }
    public LocalDate getDataProcesso() { return dataProcesso; }
    public void setDataProcesso(LocalDate dataProcesso) { this.dataProcesso = dataProcesso; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getRelatoFato() { return relatoFato; }
    public void setRelatoFato(String relatoFato) { this.relatoFato = relatoFato; }
    public String getDecisaoPunicao() { return decisaoPunicao; }
    public void setDecisaoPunicao(String decisaoPunicao) { this.decisaoPunicao = decisaoPunicao; }
    public String getNotaBi() { return notaBi; }
    public void setNotaBi(String notaBi) { this.notaBi = notaBi; }
    public String getBi() { return bi; }
    public void setBi(String bi) { this.bi = bi; }
    public int getQuantidadeDias() { return quantidadeDias; }
    public void setQuantidadeDias(int quantidadeDias) { this.quantidadeDias = quantidadeDias; }
    public String getPunicao() { return punicao; }
    public void setPunicao(String punicao) { this.punicao = punicao; }

    @Override
    public String toString() {
        if (militar == null || dataProcesso == null) {
            return "FATD Nº " + nup;
        }
        return String.format("FATD Nº %d/%d - %s %s",
                nup,
                dataProcesso.getYear(),
                militar.getPostoGraduacao(),
                militar.getNomeGuerra());
    }
}