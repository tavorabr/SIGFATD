package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.Settings;
import br.com.tavora.sigfatd.service.FeriadoRepository;
import br.com.tavora.sigfatd.service.SettingsService;
import br.com.tavora.sigfatd.view.TelaAutoridadeView;
import br.com.tavora.sigfatd.view.TelaConfiguracoesView;
import br.com.tavora.sigfatd.view.TelaFeriadosView;
import br.com.tavora.sigfatd.view.TelaParticipanteView;
import javax.swing.JOptionPane;

public class ConfiguracoesController {

    private final TelaConfiguracoesView view;
    private final SettingsService settingsService;
    private final FeriadoRepository feriadoRepository;

    public ConfiguracoesController(TelaConfiguracoesView view, SettingsService settingsService, FeriadoRepository feriadoRepository) {
        this.view = view;
        this.settingsService = settingsService;
        this.feriadoRepository = feriadoRepository;
        initController();
    }

    private void initController() {
        view.setProximoNup(settingsService.getSettings().getProximoNup());
        view.adicionarAcaoSalvar(e -> salvarConfiguracoes());
        view.adicionarAcaoGerirFeriados(e -> abrirTelaDeFeriados());
        view.adicionarAcaoMilitarParticipante(e -> abrirTelaParticipante());
        view.adicionarAcaoAutoridadeCompetente(e -> abrirTelaAutoridade());
    }
    
    private void salvarConfiguracoes() {
        try {
            int novoNup = Integer.parseInt(view.getProximoNup());
            settingsService.getSettings().setProximoNup(novoNup);
            settingsService.salvarSettings();
            JOptionPane.showMessageDialog(view, "Configurações salvas com sucesso!");
            view.fechar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Por favor, insira um número válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTelaDeFeriados() {
        TelaFeriadosView feriadosView = new TelaFeriadosView(view);
        new FeriadosController(feriadosView, feriadoRepository);
        feriadosView.setVisible(true);
    }

    private void abrirTelaParticipante() {
        TelaParticipanteView participanteView = new TelaParticipanteView(view);
        new ParticipanteController(participanteView, settingsService);
        participanteView.setVisible(true);
    }

    private void abrirTelaAutoridade() {
        TelaAutoridadeView autoridadeView = new TelaAutoridadeView(view);
        new AutoridadeController(autoridadeView, settingsService);
        autoridadeView.setVisible(true);
    }
}