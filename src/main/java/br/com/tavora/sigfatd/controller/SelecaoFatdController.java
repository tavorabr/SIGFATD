package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.FATD;
import br.com.tavora.sigfatd.service.*;
import br.com.tavora.sigfatd.view.TelaDecisaoView;
import br.com.tavora.sigfatd.view.TelaSelecaoFatdView;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class SelecaoFatdController {

    private final TelaSelecaoFatdView viewSelecao;
    private final JFrame parentFrame;

    // Dependências que precisamos passar para o DecisaoController depois
    private final FATDRepository fatdRepository;
    private final PersistenceService persistenceService;
    private final SettingsService settingsService;
    private final DecisaoDocumentGenerator documentGenerator;
    private final ArquivoService arquivoService;
    private final TextosDecisaoService textosDecisaoService;

    public SelecaoFatdController(JFrame parentFrame,
                                 FATDRepository fatdRepository,
                                 PersistenceService persistenceService,
                                 SettingsService settingsService,
                                 DecisaoDocumentGenerator documentGenerator,
                                 ArquivoService arquivoService,
                                 TextosDecisaoService textosDecisaoService) {
        this.parentFrame = parentFrame;
        this.fatdRepository = fatdRepository;
        this.persistenceService = persistenceService;
        this.settingsService = settingsService;
        this.documentGenerator = documentGenerator;
        this.arquivoService = arquivoService;
        this.textosDecisaoService = textosDecisaoService;

        this.viewSelecao = new TelaSelecaoFatdView(parentFrame);
        initController();
    }

    public void exibirTela() {
        carregarTabela();
        viewSelecao.setVisible(true);
    }

    private void initController() {
        viewSelecao.adicionarAcaoSelecionar(e -> processarSelecaoManual());
        viewSelecao.adicionarAcaoCancelar(e -> viewSelecao.fechar());

        viewSelecao.adicionarListenerTabela(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    processarSelecaoTabela();
                }
            }
        });
    }

    private void carregarTabela() {
        viewSelecao.getTableModel().setRowCount(0);
        List<FATD> lista = fatdRepository.getHistoricoFatds();

        for (FATD f : lista) {
            // Só mostra FATDs que ainda não foram concluídas (opcional)
            // Se quiser mostrar todas, remova o 'if'
            // if (!f.isConcluido()) {
            String status = f.isConcluido() ? "Concluído" : "Pendente";
            viewSelecao.getTableModel().addRow(new Object[]{
                    f.getNup(),
                    f.getDataProcesso().getYear(),
                    f.getMilitar().getPostoGraduacao() + " " + f.getMilitar().getNomeGuerra(),
                    status
            });
            // }
        }
    }

    private void processarSelecaoManual() {
        String nupTexto = viewSelecao.getNupDigitado();
        if (nupTexto.isEmpty()) {
            // Tenta pegar da tabela se o campo estiver vazio
            processarSelecaoTabela();
            return;
        }
        try {
            int nup = Integer.parseInt(nupTexto);
            abrirJulgamento(nup);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(viewSelecao, "NUP inválido.");
        }
    }

    private void processarSelecaoTabela() {
        Integer nup = viewSelecao.getNupSelecionadoNaTabela();
        if (nup != null) {
            abrirJulgamento(nup);
        } else {
            JOptionPane.showMessageDialog(viewSelecao, "Selecione uma FATD na tabela.");
        }
    }

    private void abrirJulgamento(int nup) {
        Optional<FATD> fatdOpt = fatdRepository.buscarFatdPorNup(nup);

        if (fatdOpt.isPresent()) {
            FATD fatd = fatdOpt.get();
            viewSelecao.fechar(); // Fecha a tela de seleção

            // Cria a View de Decisão (usando o arquivo que você já tem)
            TelaDecisaoView viewDecisao = new TelaDecisaoView(parentFrame, fatd);

            // Cria o SEU Controller original, passando todas as dependências
            new DecisaoController(viewDecisao, fatd, fatdRepository, persistenceService,
                    settingsService, documentGenerator, arquivoService, textosDecisaoService);

            viewDecisao.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(viewSelecao, "FATD Nº " + nup + " não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}