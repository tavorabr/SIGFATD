package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.Participante;
import br.com.tavora.sigfatd.service.SettingsService;
import br.com.tavora.sigfatd.view.TelaParticipanteView;
import javax.swing.*;
import java.util.List;

public class ParticipanteController {
    private final TelaParticipanteView view;
    private final SettingsService settingsService;
    private List<Participante> listaDeParticipantes;

    public ParticipanteController(TelaParticipanteView view, SettingsService settingsService) {
        this.view = view;
        this.settingsService = settingsService;
        this.listaDeParticipantes = this.settingsService.getSettings().getParticipantes();
        initController();
    }

    private void initController() {
        view.atualizarLista(listaDeParticipantes);
        view.adicionarAcaoNovo(e -> prepararNovo());
        view.adicionarAcaoSalvar(e -> salvar());
        view.adicionarAcaoExcluir(e -> excluir());
        view.adicionarAcaoFechar(e -> view.fechar());
        view.adicionarAcaoSelecaoLista(e -> {
            if (!e.getValueIsAdjusting()) {
                exibirDetalhes();
            }
        });
    }

    private void prepararNovo() {
        view.limparSelecaoLista();
        view.setDadosFormulario(null);
    }

    private void exibirDetalhes() {
        Participante selecionado = view.getParticipanteSelecionado();
        view.setDadosFormulario(selecionado);
    }

    private void salvar() {
        Participante dadosFormulario = view.getDadosFormulario();
        Participante selecionado = view.getParticipanteSelecionado();

        if (dadosFormulario.getNomeCompleto().isEmpty()) {
            JOptionPane.showMessageDialog(view, "O campo 'Nome Completo' é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selecionado != null) {
            selecionado.setPostoGraduacao(dadosFormulario.getPostoGraduacao());
            selecionado.setNomeCompleto(dadosFormulario.getNomeCompleto());
            selecionado.setIdtMil(dadosFormulario.getIdtMil());
        } else {
            listaDeParticipantes.add(dadosFormulario);
        }

        settingsService.salvarSettings();
        view.atualizarLista(listaDeParticipantes);
        JOptionPane.showMessageDialog(view, "Dados salvos com sucesso!");
    }

    private void excluir() {
        Participante selecionado = view.getParticipanteSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(view, "Selecione um participante para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Tem certeza que deseja excluir o participante selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            listaDeParticipantes.remove(selecionado);
            settingsService.salvarSettings();
            view.atualizarLista(listaDeParticipantes);
            prepararNovo();
        }
    }
}