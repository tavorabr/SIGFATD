package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.FATD;
import br.com.tavora.sigfatd.model.Militar;
import br.com.tavora.sigfatd.model.Participante;
import br.com.tavora.sigfatd.service.*;
import br.com.tavora.sigfatd.view.TelaGerarFatdView;
import br.com.tavora.sigfatd.view.TelaNupFormView;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class FatdController {

    private final TelaGerarFatdView view;
    private final MilitarRepository militarRepository;
    private final NupRepository nupRepository;
    private final FatdDocumentGenerator documentGenerator;
    private final SettingsService settingsService;
    private final FATDRepository fatdRepository;
    private final PersistenceService persistenceService;
    private final ArquivoService arquivoService;
    private final List<Militar> todosMilitares;

    public FatdController(TelaGerarFatdView view, MilitarRepository militarRepository, NupRepository nupRepository,
                          FatdDocumentGenerator documentGenerator, SettingsService settingsService,
                          FATDRepository fatdRepository, PersistenceService persistenceService,
                          ArquivoService arquivoService) {
        this.view = view;
        this.militarRepository = militarRepository;
        this.nupRepository = nupRepository;
        this.documentGenerator = documentGenerator;
        this.settingsService = settingsService;
        this.fatdRepository = fatdRepository;
        this.persistenceService = persistenceService;
        this.arquivoService = arquivoService;
        this.todosMilitares = this.militarRepository.getTodosMilitares();
        initController();
    }

    private void initController() {
        // --- NOVO: Verifica se o ano virou antes de carregar a tela ---
        verificarViradaDeAno();

        view.getListModel().addAll(todosMilitares);
        view.setParticipantes(settingsService.getSettings().getParticipantes());
        view.adicionarAcaoGerarDocumento(e -> gerarDocumento());
        view.adicionarAcaoInserirNup(e -> abrirFormularioNup());
        view.getCampoPesquisa().getDocument().addDocumentListener(createFilterListener());
        view.getListaMilitares().addListSelectionListener(e -> exibirDetalhesDoMilitarSelecionado());

        // Atualiza a interface (já com o número corrigido se o ano tiver virado)
        SwingUtilities.invokeLater(this::setProximoNumeroFatdTexto);
    }

    // --- LÓGICA DE RESET ANUAL ---
    private void verificarViradaDeAno() {
        int anoAtual = LocalDate.now().getYear();

        // Agora o método getAnoReferencia() existe no seu Settings.java
        int anoSalvo = settingsService.getSettings().getAnoReferencia();

        // CASO 1: Primeira execução após a atualização (anoSalvo pode ser 0)
        if (anoSalvo == 0) {
            settingsService.getSettings().setAnoReferencia(anoAtual);
            settingsService.salvarSettings();
        }
        // CASO 2: Virada de ano detectada (ex: salvo 2025, atual 2026)
        else if (anoSalvo < anoAtual) {
            settingsService.getSettings().setProximoNup(1); // Reseta para 1
            settingsService.getSettings().setAnoReferencia(anoAtual); // Atualiza para o ano novo
            settingsService.salvarSettings();

            System.out.println("Virada de ano detectada (" + anoSalvo + " -> " + anoAtual + "). Numeração resetada para 1.");
        }
    }

    private void gerarDocumento() {
        Militar militar = view.getMilitarSelecionado();
        String nupCompleto = view.getNupCompleto();
        String relato = view.getRelatoFatos();
        Participante participante = view.getParticipanteSelecionado();

        if (militar == null || nupCompleto.equals("NUP não encontrado") || relato.trim().isEmpty() || participante == null) {
            JOptionPane.showMessageDialog(view, "Todos os campos, incluindo a seleção do militar e do participante, são obrigatórios.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int nupInt = Integer.parseInt(view.getNumeroFatdTexto().split("/")[0]);
            FATD dadosFatd = new FATD(militar, nupInt, nupCompleto, LocalDate.now(), view.getReferencia(), relato, "");

            String outputPath = arquivoService.getCaminhoParaSalvarFatd(dadosFatd);

            InputStream templateStream = getClass().getClassLoader().getResourceAsStream("template_fatd.docx");
            documentGenerator.generate(dadosFatd, participante, templateStream, outputPath);

            fatdRepository.adicionarFatd(dadosFatd);

            settingsService.getSettings().setProximoNup(nupInt + 1);
            settingsService.getSettings().setAnoReferencia(LocalDate.now().getYear());
            settingsService.salvarSettings();

            persistenceService.salvarDados();

            AuditService.getInstance().logAction("Gerou o documento da FATD", "Nº " + nupInt);

            JOptionPane.showMessageDialog(view, "Documento gerado com sucesso em:\n" + outputPath, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            view.fechar();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Número da FATD inválido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Erro ao gerar o documento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setProximoNumeroFatdTexto() {
        int proximoNumero = settingsService.getSettings().getProximoNup();
        view.setNumeroFatd(String.format("%d/%d", proximoNumero, LocalDate.now().getYear()));
        nupRepository.buscarNupCompleto(proximoNumero).ifPresentOrElse(view::setNupCompleto, () -> view.setNupCompleto("NUP não encontrado"));
    }

    private void abrirFormularioNup() {
        TelaNupFormView form = new TelaNupFormView(view);
        form.setVisible(true);
        form.getNumeroFatd().ifPresent(fatdNum -> form.getNupCompleto().ifPresent(nupComp -> {
            nupRepository.adicionarNup(fatdNum, nupComp);
            settingsService.getSettings().setProximoNup(fatdNum + 1);
            settingsService.salvarSettings();
            JOptionPane.showMessageDialog(view, "NUP e FATD adicionados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            setProximoNumeroFatdTexto();
        }));
    }

    private JFileChooser createFileChooser(FATD dados) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Documento FATD");
        String nomeArquivo = String.format("FATD_%d_%s.docx", dados.getNup(), dados.getMilitar().getNomeGuerra());
        fileChooser.setSelectedFile(new File(nomeArquivo)); return fileChooser;
    }

    private void exibirDetalhesDoMilitarSelecionado() {
        view.exibirDetalhesMilitar(view.getMilitarSelecionado());
    }

    private DocumentListener createFilterListener() {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarLista(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarLista(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarLista(); }
        };
    }

    private void filtrarLista() {
        String termo = view.getCampoPesquisa().getText().toLowerCase().trim();
        DefaultListModel<Militar> model = view.getListModel(); model.clear();
        if (termo.isEmpty() || termo.equals("pesquisar militar...")) {
            model.addAll(todosMilitares);
        } else {
            List<Militar> filtrados = todosMilitares.stream().filter(m -> m.getNomeCompleto().toLowerCase().contains(termo) ||
                    m.getNomeGuerra().toLowerCase().contains(termo)).collect(Collectors.toList()); model.addAll(filtrados);
        }
    }
}