package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.service.FATDRepository; // Certifique-se que é FATDRepository
import br.com.tavora.sigfatd.view.TelaRelatorioView;

public class RelatorioController {

    private TelaRelatorioView view;
    private FATDRepository fatdRepository;

    public RelatorioController(TelaRelatorioView view) {
        this.view = view;
        this.fatdRepository = FATDRepository.getInstance();

        this.view.atualizarTabela(fatdRepository.getHistoricoFatds());

        this.view.adicionarAcaoFechar(e -> view.dispose());
    }
}