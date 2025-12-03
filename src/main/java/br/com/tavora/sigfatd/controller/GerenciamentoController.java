package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.Militar;
import br.com.tavora.sigfatd.service.MilitarRepository;
import br.com.tavora.sigfatd.view.TelaGerenciamentoView;
import br.com.tavora.sigfatd.view.TelaMilitarFormView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GerenciamentoController {

    private final TelaGerenciamentoView view;
    private final MilitarRepository militarRepository;

    public GerenciamentoController(TelaGerenciamentoView view, MilitarRepository militarRepository) {
        this.view = view;
        this.militarRepository = militarRepository;
        initController();
    }

    private void initController() {
        atualizarTabela();
        view.adicionarAcaoAdicionar(e -> adicionarMilitar());
        view.adicionarAcaoEditar(e -> editarMilitar());
        view.adicionarAcaoExcluir(e -> excluirMilitar());
        view.adicionarAcaoFechar(e -> view.fechar());

        view.getCampoPesquisa().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabela();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabela();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabela();
            }
        });
    }

    private void filtrarTabela() {
        String termo = view.getCampoPesquisa().getText().toLowerCase().trim();
        List<Militar> todosMilitares = militarRepository.getTodosMilitares();

        if (termo.isEmpty()) {
            view.atualizarTabela(todosMilitares);
        } else {
            List<Militar> militaresFiltrados = todosMilitares.stream()
                    .filter(militar ->
                            militar.getNomeCompleto().toLowerCase().contains(termo) ||
                                    militar.getNomeGuerra().toLowerCase().contains(termo)
                    )
                    .collect(Collectors.toList());
            view.atualizarTabela(militaresFiltrados);
        }
    }

    private void adicionarMilitar() {
        TelaMilitarFormView form = new TelaMilitarFormView(view, new Militar());
        form.setVisible(true);

        form.getMilitar().ifPresent(militar -> {
            militarRepository.adicionarMilitar(militar);
            atualizarTabela();
        });
    }

    private void editarMilitar() {
        getMilitarSelecionado().ifPresent(militarParaEditar -> {
            TelaMilitarFormView form = new TelaMilitarFormView(view, militarParaEditar);
            form.setVisible(true);

            form.getMilitar().ifPresent(militarAtualizado -> {
                militarRepository.atualizarMilitar(militarParaEditar.getId(), militarAtualizado);
                atualizarTabela();
            });
        });
    }

    private void excluirMilitar() {
        getMilitarSelecionado().ifPresent(militar -> {
            String mensagem = String.format("Excluir %s %s?", militar.getPostoGraduacao(), militar.getNomeGuerra());
            int confirmacao = JOptionPane.showConfirmDialog(view, mensagem, "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

            if (confirmacao == JOptionPane.YES_OPTION) {
                militarRepository.removerMilitar(militar.getId());
                atualizarTabela();
            }
        });
    }

    private Optional<Militar> getMilitarSelecionado() {
        int linhaSelecionada = view.getLinhaSelecionada();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(view, "Por favor, selecione um militar na tabela.", "Nenhum Militar Selecionado", JOptionPane.WARNING_MESSAGE);
            return Optional.empty();
        }
        int idMilitar = (int) view.getTabelaMilitares().getValueAt(linhaSelecionada, 0);
        return militarRepository.buscarPorId(idMilitar);
    }

    private void atualizarTabela() {
        filtrarTabela();
    }
}