package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.FATD;
import br.com.tavora.sigfatd.model.Role;
import br.com.tavora.sigfatd.model.User;
import br.com.tavora.sigfatd.service.*;
import br.com.tavora.sigfatd.view.TelaArquivoView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class ArquivoController {
    private final TelaArquivoView view;
    private final FATDRepository fatdRepository;
    private final ArquivoService arquivoService;
    private final PersistenceService persistenceService;

    public ArquivoController(TelaArquivoView view, FATDRepository fatdRepository, ArquivoService arquivoService, PersistenceService persistenceService) {
        this.view = view;
        this.fatdRepository = fatdRepository;
        this.arquivoService = arquivoService;
        this.persistenceService = persistenceService;
        initController();
    }

    private void initController() {
        view.popularListaFatds(fatdRepository.getHistoricoFatds());

        view.adicionarAcaoSelecaoFatd(e -> {
            if (!e.getValueIsAdjusting()) {
                FATD fatdSelecionado = view.getFatdSelecionado();
                if (fatdSelecionado != null) {
                    view.popularListaArquivos(arquivoService.listarArquivos(fatdSelecionado));
                } else {
                    view.popularListaArquivos(Collections.emptyList());
                }
                view.habilitarBotaoExcluir(fatdSelecionado != null);
            }
        });

        view.adicionarAcaoAbrirArquivo(e -> abrirArquivoSelecionado());

        User usuarioLogado = SessionManager.getInstance().getCurrentUser();
        if (usuarioLogado != null) {
            Role role = usuarioLogado.getRole();

            if (role == Role.MASTER) {
                view.setExcluirFatdButtonVisible(true);
                view.adicionarAcaoExcluirFatd(e -> excluirFatdSelecionado());
            }

            if (role == Role.MASTER || role == Role.PARTICIPANTE) {
                view.setAnexarArquivoButtonVisible(true);
                view.adicionarAcaoAnexarArquivo(e -> anexarArquivo());
            }
        }
    }

    private void anexarArquivo() {
        FATD fatdSelecionado = view.getFatdSelecionado();
        if (fatdSelecionado == null) {
            JOptionPane.showMessageDialog(view, "Por favor, selecione um FATD para adicionar um anexo.",
                    "Nenhum FATD Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            arquivoService.anexarArquivo(fatdSelecionado, view).ifPresent(novoArquivo -> {
                view.popularListaArquivos(arquivoService.listarArquivos(fatdSelecionado));
                JOptionPane.showMessageDialog(view, "Arquivo '" + novoArquivo.getName() + "' anexado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            });
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(view, "Ocorreu um erro ao anexar o arquivo:\n" + ex.getMessage(),
                    "Erro de Anexo", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void abrirArquivoSelecionado() {
        File arquivo = view.getArquivoSelecionado();
        if (arquivo != null) {
            try {
                arquivoService.abrirArquivo(arquivo);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(view, "Erro ao abrir o arquivo:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirFatdSelecionado() {
        FATD fatdSelecionado = view.getFatdSelecionado();
        if (fatdSelecionado == null) {
            JOptionPane.showMessageDialog(view, "Por favor, selecione um FATD para excluir.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensagem = String.format(
                "<html>Deseja excluir permanentemente o <b>FATD Nº %s</b> (%s)?<br><br>" +
                        "<b>Atenção:</b> Esta ação não pode ser desfeita e apagará também todos os arquivos associados.</html>",
                fatdSelecionado.getNup(),
                fatdSelecionado.getMilitar().getNomeGuerra()
        );

        int confirmacao = JOptionPane.showConfirmDialog(
                view,
                mensagem,
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                AuditService.getInstance().logAction("Excluiu a FATD", "Nº " + fatdSelecionado.getNup());
                fatdRepository.removerFatd(fatdSelecionado);
                arquivoService.excluirPastaFatd(fatdSelecionado);
                persistenceService.salvarDados();
                view.popularListaFatds(fatdRepository.getHistoricoFatds());
                view.popularListaArquivos(Collections.emptyList());
                JOptionPane.showMessageDialog(view, "FATD excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view, "Ocorreu um erro ao excluir a pasta ou salvar as alterações:\n" + e.getMessage(), "Erro de Exclusão", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}