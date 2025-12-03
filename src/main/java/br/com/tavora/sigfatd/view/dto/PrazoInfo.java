package br.com.tavora.sigfatd.view.dto;

import java.time.LocalDate;

public class PrazoInfo {

    private final int nup;
    private final String nomeGuerra;
    private final LocalDate dataInicio;
    private final LocalDate dataPrazoDefesa;
    private final LocalDate dataPrazoDecisao;
    private final String statusDefesa;
    private final String statusDecisao;
    private final String notaBi;
    private final String bi;
    private final int quantidadeDias;
    private final String punicao;
    private final boolean concluido;
    private final boolean defesaEntregue;
    private final boolean decisaoFeita;

    public PrazoInfo(int nup, String nomeGuerra, LocalDate dataInicio, LocalDate dataPrazoDefesa,
                     LocalDate dataPrazoDecisao, String statusDefesa, String statusDecisao,
                     String notaBi, String bi, int quantidadeDias, String punicao, boolean concluido,
                     boolean defesaEntregue, boolean decisaoFeita) {
        this.nup = nup;
        this.nomeGuerra = nomeGuerra;
        this.dataInicio = dataInicio;
        this.dataPrazoDefesa = dataPrazoDefesa;
        this.dataPrazoDecisao = dataPrazoDecisao;
        this.statusDefesa = statusDefesa;
        this.statusDecisao = statusDecisao;
        this.notaBi = notaBi;
        this.bi = bi;
        this.quantidadeDias = quantidadeDias;
        this.punicao = punicao;
        this.concluido = concluido;
        this.defesaEntregue = defesaEntregue;
        this.decisaoFeita = decisaoFeita;
    }

    public int getNup() {return nup;}
    public String getNomeGuerra() {return nomeGuerra;}
    public LocalDate getDataInicio() {return dataInicio;}
    public LocalDate getDataPrazoDefesa() {return dataPrazoDefesa;}
    public LocalDate getDataPrazoDecisao() {return dataPrazoDecisao;}
    public String getStatusDefesa() {return statusDefesa;}
    public String getStatusDecisao() {return statusDecisao;}
    public String getNotaBi() { return notaBi; }
    public String getBi() { return bi; }
    public int getQuantidadeDias() { return quantidadeDias; }
    public String getPunicao() { return punicao; }
    public boolean isConcluido() { return concluido;}
    public boolean isDefesaEntregue() { return defesaEntregue; }
    public boolean isDecisaoFeita() { return decisaoFeita; }
}