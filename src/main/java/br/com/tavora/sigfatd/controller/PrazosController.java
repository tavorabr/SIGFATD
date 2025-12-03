package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.FATD;
import br.com.tavora.sigfatd.model.Role;
import br.com.tavora.sigfatd.model.User;
import br.com.tavora.sigfatd.service.*;
import br.com.tavora.sigfatd.view.TelaPrazosView;
import br.com.tavora.sigfatd.view.dto.PrazoInfo;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrazosController {

    private static final int PRAZO_DEFESA_DIAS_UTEIS = 3;
    private static final int PRAZO_DECISAO_DIAS_UTEIS = 8;
    private static final int DIAS_ANTECEDENCIA_AVISO = 1;

    private static final int COLUNA_STATUS_DEFESA = 4;
    private static final int COLUNA_STATUS_DECISAO = 6;
    private static final int COLUNA_NOTA_BI = 7;
    private static final int COLUNA_BI = 8;
    private static final int COLUNA_QTD_DIAS = 9;
    private static final int COLUNA_PUNICAO = 10;

    private final TelaPrazosView view;
    private final FATDRepository fatdRepository;
    private final PrazoService prazoService;
    private final PersistenceService persistenceService;

    public PrazosController(TelaPrazosView view, FATDRepository fatdRepository) {
        this.view = view;
        this.fatdRepository = fatdRepository;
        this.prazoService = new PrazoService();
        this.persistenceService = PersistenceService.getInstance();
        initController();
    }

    private void initController() {
        view.adicionarAcaoFechar(e -> view.fechar());
        atualizarPrazos();

        User usuarioLogado = SessionManager.getInstance().getCurrentUser();
        if (usuarioLogado != null && (usuarioLogado.getRole() == Role.MASTER || usuarioLogado.getRole() == Role.PARTICIPANTE)) {
            view.setBotaoEditarVisivel(true);
            view.adicionarAcaoEditar(e -> editarDataInicio());
            view.adicionarAcaoConcluido(e -> concluirFatd());
            view.adicionarAcaoEdicaoTabela(this::handleTableEdit);
            view.setEdicaoCamposHabilitada(true);
        }
    }

    private void handleTableEdit(TableModelEvent e) {
        if (e.getType() != TableModelEvent.UPDATE) {
            return;
        }

        int linha = e.getFirstRow();
        int coluna = e.getColumn();

        if (coluna != COLUNA_STATUS_DEFESA && coluna != COLUNA_STATUS_DECISAO &&
                (coluna < COLUNA_NOTA_BI || coluna > COLUNA_PUNICAO)) {
            return;
        }

        TableModel model = (TableModel) e.getSource();

        Integer nup = (Integer) model.getValueAt(linha, 0);
        Object data = model.getValueAt(linha, coluna);

        Optional<FATD> fatdOptional = fatdRepository.buscarFatdPorNup(nup);
        if (fatdOptional.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Erro ao encontrar FATD para salvar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FATD fatd = fatdOptional.get();
        String logMessage = "";

        try {
            switch (coluna) {
                case COLUNA_STATUS_DEFESA:
                    fatd.setDefesaEntregue("ENTREGUE".equals(data));
                    logMessage = "Atualizou 'Status Defesa'";
                    break;
                case COLUNA_STATUS_DECISAO:
                    fatd.setDecisaoFeita("DECISÃO FEITA".equals(data));
                    logMessage = "Atualizou 'Status Decisão'";
                    break;
                case COLUNA_NOTA_BI:
                    fatd.setNotaBi((String) data);
                    logMessage = "Atualizou 'Nota ao BI'";
                    break;
                case COLUNA_BI:
                    fatd.setBi((String) data);
                    logMessage = "Atualizou 'BI'";
                    break;
                case COLUNA_QTD_DIAS:
                    int dias = 0;
                    if (data != null && !data.toString().trim().isEmpty()) {
                        dias = Integer.parseInt(data.toString());
                    }
                    fatd.setQuantidadeDias(dias);
                    logMessage = "Atualizou 'Qtd Dias'";
                    break;
                case COLUNA_PUNICAO:
                    fatd.setPunicao((String) data);
                    logMessage = "Atualizou 'Punição'";
                    break;
            }

            persistenceService.salvarDados();
            AuditService.getInstance().logAction(logMessage, "FATD Nº " + nup);
            atualizarPrazos();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "O valor para 'Qtd Dias' deve ser um número.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            atualizarPrazos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar alteração: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void concluirFatd() {
        Integer nupSelecionado = view.getNupSelecionado();
        if (nupSelecionado == null) {
            JOptionPane.showMessageDialog(view, "Por favor, selecione um processo na tabela para concluir.", "Nenhum Processo Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<FATD> fatdOptional = fatdRepository.buscarFatdPorNup(nupSelecionado);
        if (fatdOptional.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Erro: FATD selecionado não encontrado no sistema.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FATD fatd = fatdOptional.get();

        int confirmacao = JOptionPane.showConfirmDialog(view,
                "Tem certeza que deseja marcar a FATD Nº " + nupSelecionado + " como concluída?",
                "Confirmar Conclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                fatd.setConcluido(true);

                persistenceService.salvarDados();
                AuditService.getInstance().logAction("Marcou a FATD como Concluída", "Nº " + nupSelecionado);
                atualizarPrazos();

                JOptionPane.showMessageDialog(view, "FATD marcada como concluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(view, "Erro ao salvar as alterações no arquivo.", "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Ocorreu um erro ao marcar a FATD: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void atualizarPrazos() {
        List<FATD> todosFatds = fatdRepository.getHistoricoFatds();
        List<PrazoInfo> prazosInfo = todosFatds.stream()
                .sorted(Comparator.comparingInt(FATD::getNup).reversed())
                .map(this::criarPrazoInfo)
                .collect(Collectors.toList());
        view.atualizarTabela(prazosInfo);
    }

    private void editarDataInicio() {
        Integer nupSelecionado = view.getNupSelecionado();
        if (nupSelecionado == null) {
            JOptionPane.showMessageDialog(view, "Por favor, selecione um processo na tabela para editar.", "Nenhum Processo Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<FATD> fatdOptional = fatdRepository.buscarFatdPorNup(nupSelecionado);
        if (fatdOptional.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Erro: FATD selecionado não encontrado no sistema.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FATD fatdParaEditar = fatdOptional.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataAtual = fatdParaEditar.getDataProcesso().format(formatter);

        String novaDataStr = JOptionPane.showInputDialog(view, "Digite a nova data de início (dd/MM/aaaa):", dataAtual);

        if (novaDataStr != null && !novaDataStr.trim().isEmpty()) {
            try {
                LocalDate novaData = LocalDate.parse(novaDataStr.trim(), formatter);
                fatdParaEditar.setDataProcesso(novaData);
                persistenceService.salvarDados();
                AuditService.getInstance().logAction("Editou a data de início da FATD", "Nº " + nupSelecionado);
                atualizarPrazos();
                JOptionPane.showMessageDialog(view, "Data de início atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(view, "Formato de data inválido. Por favor, use dd/MM/yyyy.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view, "Erro ao salvar as alterações no arquivo.", "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private PrazoInfo criarPrazoInfo(FATD fatd) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicio = fatd.getDataProcesso();
        LocalDate prazoDefesa = prazoService.adicionarDiasUteis(dataInicio, PRAZO_DEFESA_DIAS_UTEIS);
        LocalDate prazoDecisao = prazoService.adicionarDiasUteis(prazoDefesa, PRAZO_DECISAO_DIAS_UTEIS);

        String statusDefesa = obterStatusDefesa(fatd, hoje, prazoDefesa);
        String statusDecisao = obterStatusDecisao(fatd, hoje, prazoDecisao);

        String nomeGuerra = fatd.getMilitar() != null ? fatd.getMilitar().getNomeGuerra() : "N/A";

        return new PrazoInfo(
                fatd.getNup(),
                nomeGuerra,
                dataInicio,
                prazoDefesa,
                prazoDecisao,
                statusDefesa,
                statusDecisao,
                fatd.getNotaBi(),
                fatd.getBi(),
                fatd.getQuantidadeDias(),
                fatd.getPunicao(),
                fatd.isConcluido(),
                fatd.isDefesaEntregue(),
                fatd.isDecisaoFeita()
        );
    }

    private String obterStatusDefesa(FATD fatd, LocalDate hoje, LocalDate prazoDefesa) {
        if (fatd.isDefesaEntregue()) {
            return "ENTREGUE";
        }
        return calcularStatus(hoje, prazoDefesa);
    }

    private String obterStatusDecisao(FATD fatd, LocalDate hoje, LocalDate prazoDecisao) {
        if (fatd.isDecisaoFeita()) {
            return "DECISÃO FEITA";
        }
        return calcularStatus(hoje, prazoDecisao);
    }

    private String calcularStatus(LocalDate hoje, LocalDate prazoFinal) {
        if (hoje.isAfter(prazoFinal)) {
            return "VENCIDO";
        }
        long diasRestantes = ChronoUnit.DAYS.between(hoje, prazoFinal);
        if (diasRestantes <= DIAS_ANTECEDENCIA_AVISO) {
            return "PRÓXIMO DO VENCIMENTO";
        }
        return "EM DIA";
    }
}