package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.*;
import br.com.tavora.sigfatd.service.*;
import br.com.tavora.sigfatd.view.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Desktop;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MenuController {

    private final TelaMenuView view;
    private final PersistenceService persistenceService;
    private final MilitarRepository militarRepository;
    private final FATDRepository fatdRepository;
    private final NupRepository nupRepository;
    private final ArquivoService arquivoService;
    private final SettingsService settingsService;
    private final FatdDocumentGenerator fatdDocumentGenerator;
    private final UserRepository userRepository;
    private final DecisaoDocumentGenerator decisaoDocumentGenerator;
    private final TextosDecisaoService textosDecisaoService;
    private final FeriadoRepository feriadoRepository;

    public MenuController(TelaMenuView view) {
        this.view = view;
        // Inicialização única dos serviços (Evita duplicidade de instâncias)
        this.persistenceService = PersistenceService.getInstance();
        this.militarRepository = MilitarRepository.getInstance();
        this.fatdRepository = FATDRepository.getInstance();
        this.nupRepository = NupRepository.getInstance();
        this.arquivoService = ArquivoService.getInstance();
        this.settingsService = SettingsService.getInstance();
        this.fatdDocumentGenerator = new FatdDocumentGenerator();
        this.userRepository = UserRepository.getInstance();
        this.decisaoDocumentGenerator = new DecisaoDocumentGenerator();
        this.textosDecisaoService = TextosDecisaoService.getInstance();
        this.feriadoRepository = FeriadoRepository.getInstance();

        initController();
    }

    private void initController() {
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salvarDadosAoSair();
            }
        });

        User usuarioLogado = SessionManager.getInstance().getCurrentUser();
        if (usuarioLogado != null) {
            view.setUsuarioLogado(usuarioLogado.getUsername());
        }

        List<MenuItem> menuItems = criarItensDeMenu();
        view.setMenuItems(menuItems);
    }

    private void abrirTelaDeAjuda() {
        TelaAjudaView ajudaView = new TelaAjudaView(view);
        new AjudaController(ajudaView);
        ajudaView.setVisible(true);
    }

    private List<MenuItem> criarItensDeMenu() {
        List<MenuItem> items = new ArrayList<>();
        User usuarioLogado = SessionManager.getInstance().getCurrentUser();
        Role role = (usuarioLogado != null) ? usuarioLogado.getRole() : null;
        if (role == null) return items;

        MenuItem itemAjuda = new MenuItem("Ajuda", "icons/ajuda.png", this::abrirTelaDeAjuda);

        switch (role) {
            case MASTER:
                items.add(new MenuItem("Gerenciar Militares", "icons/gerenciar_militares.png", this::abrirTelaGerenciamento));
                items.add(new MenuItem("Importar Militares", "icons/importar_militares.png", this::importarMilitares));
                items.add(new MenuItem("Importar NUP", "icons/importar_nup.png", this::importarNups));
                items.add(new MenuItem("Gerar FATD", "icons/gerar_fatd.png", this::abrirTelaGeracaoFatd));
                items.add(new MenuItem("Arquivo de FATDs", "icons/arquivo_fatd.png", this::abrirTelaArquivo));
                items.add(new MenuItem("Controle de Prazos", "icons/controle_prazos.png", this::abrirTelaDePrazos));
                items.add(new MenuItem("Decisão de Punição", "icons/decisao_punicao.png", this::abrirTelaDecisaoPunicao));
                items.add(new MenuItem("Consulta RDE", "icons/consulta_rde.png", this::abrirPdfRde));
                items.add(new MenuItem("Gerenciar Usuários", "icons/gerenciar_usuarios.png", this::abrirTelaGerenciamentoUsuarios));
                items.add(new MenuItem("Configurações", "icons/configuracoes.png", this::abrirTelaConfiguracoes));
                items.add(itemAjuda);
                items.add(new MenuItem("Logoff", "icons/logoff.png", this::fazerLogoff));
                break;

            case PARTICIPANTE:
                items.add(new MenuItem("Gerenciar Militares", "icons/gerenciar_militares.png", this::abrirTelaGerenciamento));
                items.add(new MenuItem("Importar Militares", "icons/importar_militares.png", this::importarMilitares));
                items.add(new MenuItem("Importar NUP", "icons/importar_nup.png", this::importarNups));
                items.add(new MenuItem("Gerar FATD", "icons/gerar_fatd.png", this::abrirTelaGeracaoFatd));
                items.add(new MenuItem("Arquivo de FATDs", "icons/arquivo_fatd.png", this::abrirTelaArquivo));
                items.add(new MenuItem("Controle de Prazos", "icons/controle_prazos.png", this::abrirTelaDePrazos));
                items.add(new MenuItem("Consulta RDE", "icons/consulta_rde.png", this::abrirPdfRde));
                items.add(new MenuItem("Configurações", "icons/configuracoes.png", this::abrirTelaConfiguracoes));
                items.add(itemAjuda);
                items.add(new MenuItem("Logoff", "icons/logoff.png", this::fazerLogoff));
                break;

            case AUTORIDADE:
                items.add(new MenuItem("Arquivo de FATDs", "icons/arquivo_fatd.png", this::abrirTelaArquivo));
                items.add(new MenuItem("Controle de Prazos", "icons/controle_prazos.png", this::abrirTelaDePrazos));
                items.add(new MenuItem("Decisão de Punição", "icons/decisao_punicao.png", this::abrirTelaDecisaoPunicao));
                items.add(new MenuItem("Consulta RDE", "icons/consulta_rde.png", this::abrirPdfRde));
                items.add(itemAjuda);
                items.add(new MenuItem("Logoff", "icons/logoff.png", this::fazerLogoff));
                break;

            case INTELIGENCIA:
                items.add(new MenuItem("Arquivo de FATDs", "icons/arquivo_fatd.png", this::abrirTelaArquivo));
                items.add(new MenuItem("Controle de Prazos", "icons/controle_prazos.png", this::abrirTelaDePrazos));
                items.add(new MenuItem("Consulta RDE", "icons/consulta_rde.png", this::abrirPdfRde));
                items.add(itemAjuda);
                items.add(new MenuItem("Logoff", "icons/logoff.png", this::fazerLogoff));
                break;
        }

        return items;
    }

    private void salvarDadosAoSair() {
        JDialog aDialog = new JDialog(view, "Salvando", true);
        JLabel aLabel = new JLabel("Salvando dados... Por favor, aguarde.", SwingConstants.CENTER);
        aDialog.add(aLabel);
        aDialog.setSize(300, 100);
        aDialog.setLocationRelativeTo(view);
        aDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        aDialog.setUndecorated(true);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                persistenceService.salvarDados();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(view, "Erro ao salvar os dados: " + e.getCause().getMessage(),
                            "Erro de Escrita", JOptionPane.ERROR_MESSAGE);
                } finally {
                    aDialog.dispose();
                    view.dispose();
                    System.exit(0);
                }
            }
        };

        worker.execute();
        aDialog.setVisible(true);
    }

    private void fazerLogoff() {
        SessionManager.getInstance().setCurrentUser(null);
        view.dispose();
        Main.iniciarAplicacao();
    }

    private void abrirTelaGerenciamento() {
        TelaGerenciamentoView gerenciamentoView = new TelaGerenciamentoView(view);
        new GerenciamentoController(gerenciamentoView, this.militarRepository);
        gerenciamentoView.setVisible(true);
    }

    private void abrirTelaArquivo() {
        TelaArquivoView arquivoView = new TelaArquivoView(view);
        new ArquivoController(arquivoView, fatdRepository, arquivoService, persistenceService);
        arquivoView.setVisible(true);
    }

    private File escolherArquivoCsv(String titulo) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(titulo);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos CSV", "csv"));
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private void importarMilitares() {
        File arquivo = escolherArquivoCsv("Selecione o arquivo CSV de Militares");
        if (arquivo == null) return;
        try {
            MilitarCsvParser parser = new MilitarCsvParser();
            List<Militar> novosMilitares = militarRepository.carregarMilitaresImportados(parser.parse(arquivo));
            JOptionPane.showMessageDialog(view,
                    novosMilitares.size() + " novos militares foram importados com sucesso!\n" +
                            "Total de militares no sistema: " + militarRepository.getTodosMilitares().size(),
                    "Importação Concluída",
                    JOptionPane.INFORMATION_MESSAGE);
            persistenceService.salvarDados();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erro ao ler o arquivo: " + e.getMessage(),
                    "Erro de Importação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importarNups() {
        File arquivo = escolherArquivoCsv("Selecione o arquivo CSV de NUPs");
        if (arquivo == null) return;
        try {
            NupCsvParser parser = new NupCsvParser();
            nupRepository.carregarNupsImportados(parser.parse(arquivo));
            JOptionPane.showMessageDialog(view, nupRepository.getTotalNupsCarregados() + " NUPs importados!",
                    "Importação Concluída", JOptionPane.INFORMATION_MESSAGE);
            persistenceService.salvarDados();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erro ao ler o arquivo: " + e.getMessage(), "Erro de Importação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTelaGeracaoFatd() {
        if (militarRepository.getTodosMilitares().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhum militar cadastrado para gerar FATD.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TelaGerarFatdView fatdView = new TelaGerarFatdView(view);
        new FatdController(fatdView, militarRepository, nupRepository, fatdDocumentGenerator,
                settingsService, fatdRepository, persistenceService, arquivoService);
        fatdView.setVisible(true);
    }

    private void abrirTelaDePrazos() {
        TelaPrazosView prazosView = new TelaPrazosView(view);
        new PrazosController(prazosView, this.fatdRepository);
        prazosView.setVisible(true);
    }

    // --- MÉTODOS REFATORADOS ---

    private void abrirTelaDecisaoPunicao() {
        SelecaoFatdController controllerSelecao = new SelecaoFatdController(
                view,
                this.fatdRepository,
                this.persistenceService,
                this.settingsService,
                this.decisaoDocumentGenerator,
                this.arquivoService,
                this.textosDecisaoService
        );
        controllerSelecao.exibirTela();
    }

    private void abrirPdfRde() {
        try (InputStream pdfStream = getClass().getClassLoader().getResourceAsStream("RDE.pdf")) {
            if (pdfStream == null) {
                JOptionPane.showMessageDialog(view, "Arquivo 'RDE.pdf' não encontrado no projeto.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File tempFile = File.createTempFile("RDE_temp", ".pdf");
            tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                pdfStream.transferTo(out);
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile);
            } else {
                JOptionPane.showMessageDialog(view, "Abertura de arquivos não suportada.", "Erro de Compatibilidade", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Erro ao tentar abrir o PDF: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirTelaConfiguracoes() {
        TelaConfiguracoesView configView = new TelaConfiguracoesView(view);
        new ConfiguracoesController(configView, settingsService, feriadoRepository);
        configView.setVisible(true);
    }

    private void abrirTelaGerenciamentoUsuarios() {
        TelaGerenciamentoUsuariosView usuariosView = new TelaGerenciamentoUsuariosView(view);
        new GerenciamentoUsuariosController(usuariosView, userRepository, persistenceService);
        usuariosView.setVisible(true);
    }
}