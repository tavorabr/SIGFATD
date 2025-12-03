package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.Autoridade;
import br.com.tavora.sigfatd.service.SettingsService;
import br.com.tavora.sigfatd.view.TelaAutoridadeView;
import javax.swing.*;
import java.util.List;

public class AutoridadeController {
    private final TelaAutoridadeView view;
    private final SettingsService settingsService;
    private List<Autoridade> listaDeAutoridades;

    public AutoridadeController(TelaAutoridadeView view, SettingsService settingsService) {
        this.view = view;
        this.settingsService = settingsService;
        this.listaDeAutoridades = this.settingsService.getSettings().getAutoridades();
        initController();
    }

    private void initController() {
        view.atualizarLista(listaDeAutoridades);
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
        Autoridade selecionada = view.getAutoridadeSelecionada();
        view.setDadosFormulario(selecionada);
    }

    private void salvar() {
        Autoridade dadosFormulario = view.getDadosFormulario();
        Autoridade selecionada = view.getAutoridadeSelecionada();

        if (dadosFormulario.getNomeCompleto().isEmpty()) {
            JOptionPane.showMessageDialog(view, "O campo 'Nome Completo' é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selecionada != null) {
            selecionada.setPostoGraduacao(dadosFormulario.getPostoGraduacao());
            selecionada.setNomeCompleto(dadosFormulario.getNomeCompleto());
            selecionada.setTitulacao(dadosFormulario.getTitulacao());
        } else {
            listaDeAutoridades.add(dadosFormulario);
        }

        settingsService.salvarSettings();
        view.atualizarLista(listaDeAutoridades);
        JOptionPane.showMessageDialog(view, "Dados salvos com sucesso!");
    }

    private void excluir() {
        Autoridade selecionada = view.getAutoridadeSelecionada();
        if (selecionada == null) {
            JOptionPane.showMessageDialog(view, "Selecione uma autoridade para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Tem certeza que deseja excluir a autoridade selecionada?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            listaDeAutoridades.remove(selecionada);
            settingsService.salvarSettings();
            view.atualizarLista(listaDeAutoridades);
            prepararNovo();
        }
    }
}