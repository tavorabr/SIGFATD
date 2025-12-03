package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.Autoridade;
import br.com.tavora.sigfatd.model.FATD;
import br.com.tavora.sigfatd.service.*;
import br.com.tavora.sigfatd.view.TelaDecisaoView;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecisaoController {

    private final TelaDecisaoView view;
    private final FATD fatdAlvo;
    private final FATDRepository fatdRepository;
    private final PersistenceService persistenceService;
    private final SettingsService settingsService;
    private final DecisaoDocumentGenerator documentGenerator;
    private final ArquivoService arquivoService;
    private final TextosDecisaoService textosDecisaoService;

    // Guardamos o template do corpo para substituir as tags pelos valores dos Combos na hora de salvar
    private String templateCorpoUtilizado;

    public DecisaoController(TelaDecisaoView view, FATD fatdAlvo, FATDRepository repo,
                             PersistenceService persistence, SettingsService settings,
                             DecisaoDocumentGenerator generator, ArquivoService arquivo,
                             TextosDecisaoService textosService) {
        this.view = view;
        this.fatdAlvo = fatdAlvo;
        this.fatdRepository = repo;
        this.persistenceService = persistence;
        this.settingsService = settings;
        this.documentGenerator = generator;
        this.arquivoService = arquivo;
        this.textosDecisaoService = textosService;

        initController();
    }

    private void initController() {
        // 1. Autoridades
        view.popularAutoridades(settingsService.getSettings().getAutoridades());

        // 2. Prepara Listas para os Combos (Punições, etc.)
        Map<String, Object> jsonMap = textosDecisaoService.getTextosProntos();
        Map<String, List<String>> opcoesParaCombos = new HashMap<>();

        // Garante que "Julgamento realizado." seja a primeira opção e padrão
        List<String> listaJulgamento = new ArrayList<>(transformarEmLista(jsonMap.get("Geral")));
        if (!listaJulgamento.contains("Julgamento realizado.")) {
            listaJulgamento.add(0, "Julgamento realizado.");
        }

        opcoesParaCombos.put("${julgamento}", listaJulgamento);
        opcoesParaCombos.put("${punicao}", transformarEmLista(jsonMap.get("Punições")));
        List<String> listaDias = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            listaDias.add(i + (i == 1 ? " dia" : " dias"));
        }
        opcoesParaCombos.put("${quantidadeDias}", listaDias);
        opcoesParaCombos.put("${enquadramento}", transformarEmLista(jsonMap.get("Anexo I")));
        List<String> artigos = Arrays.asList("Art. 14", "Art. 15", "Art. 18", "Art. 24", "Art. 47");
        opcoesParaCombos.put("${artigoRDE}", artigos);

        // 3. Renderiza o Documento (Editável + Combos)
        String cabecalho = gerarCabecalhoSuperior();
        templateCorpoUtilizado = gerarCorpoTexto();
        view.renderizarDocumentoInterativo(templateCorpoUtilizado, cabecalho, opcoesParaCombos);

        // 4. POPULA A BARRA LATERAL
        view.popularPaineisLaterais(jsonMap, textoClicado -> {
            view.inserirTextoNoCursor(textoClicado);
        });

        // 5. Listeners Botoes
        view.adicionarAcaoSalvar(e -> salvarDecisao());
        view.adicionarAcaoGerar(e -> gerarDocumento());
        view.adicionarAcaoFechar(e -> view.fechar());
    }

    private String gerarCabecalhoSuperior() {
        return String.format("(Continuação do processo nº %d/%d/ Cia Com PC, de %s .......................................................................................................Fl____)",
                fatdAlvo.getNup(),
                fatdAlvo.getDataProcesso().getYear(),
                fatdAlvo.getDataProcesso().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private String gerarCorpoTexto() {
        String dataAtual = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR")));

        String dataFato = (fatdAlvo.getDataProcesso() != null) ?
                fatdAlvo.getDataProcesso().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")) : "____";

        String referencia = fatdAlvo.getReferencia() != null ? fatdAlvo.getReferencia() : "____";
        String postoNomeGuerra = fatdAlvo.getMilitar().getPostoGraduacao() + " " + fatdAlvo.getMilitar().getNomeGuerra();
        String relato = fatdAlvo.getRelatoFato() != null ? fatdAlvo.getRelatoFato() : "...";

        return
                "Da análise do relato do fato, das razões de defesa e da oitiva do militar arrolado como autor da transgressão relatou, " +
                        "por meio do " + referencia + ", que o " + postoNomeGuerra + ", " + relato + ".\n\n" +

                        "Em suas justificativas/razões de defesa por escrito, que se encontram anexadas a este processo, " +
                        "e em sua oitiva o " + postoNomeGuerra +
                        " (CAMPO DE DEFESA/MILITAR ARROLADO).\n\n" +

                        "${julgamento}\n\n" +

                        "Passo a decidir.\n" +
                        "A ação praticada pelo " + postoNomeGuerra + " no dia " + dataFato +
                        " configura-se como transgressão disciplinar prevista \nno: ${enquadramento}.\n\n" +

                        "Assim, considero que, por não ter apresentado razões de defesa plausíveis que pudessem configurar " +
                        "causa de justificativa prevista no Art. 18 do RDE, fica ${punicao} por ${quantidadeDias} (${artigoRDE}), " +
                        "permanecendo no comportamento 'Bom'.\n\n" +

                        "Presenciou a oitiva do militar arrolado, em " + dataAtual + ", o (Testemunha se houver).\n\n" +

                        "Dê-se ciência da presente decisão ao militar arrolado, na forma do inciso VIII do § 2º do art. 35 do RDE.\n" +
                        "Publique-se e arquive-se.\n\n" +

                        "Curitiba-PR, " + dataAtual + "\n\n" +
                        "\n";
    }

    private void salvarDecisao() {
        String textoFinal = templateCorpoUtilizado;
        Map<String, JComboBox<String>> combos = view.getCombosDinamicos();

        for (Map.Entry<String, JComboBox<String>> entry : combos.entrySet()) {
            String tag = entry.getKey();
            JComboBox<String> combo = entry.getValue();
            String valorSelecionado = (String) combo.getSelectedItem();

            if (valorSelecionado != null) {
                textoFinal = textoFinal.replace(tag, valorSelecionado);
                atualizarCamposEspecificosDoModelo(tag, valorSelecionado);
            }
        }

        fatdAlvo.setDecisaoPunicao(textoFinal);
        fatdRepository.atualizarFatd(fatdAlvo);
        try {
            persistenceService.salvarDados();
            JOptionPane.showMessageDialog(view, "Decisão salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarCamposEspecificosDoModelo(String tag, String valor) {
        if (tag.equals("${punicao}")) {
            fatdAlvo.setPunicao(valor);
        } else if (tag.equals("${quantidadeDias}")) {
            try {
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(valor);
                if (m.find()) {
                    fatdAlvo.setQuantidadeDias(Integer.parseInt(m.group()));
                }
            } catch (Exception ignored) {}
        }
    }

    private void gerarDocumento() {
        // 1. Salva primeiro
        salvarDecisao();

        Autoridade aut = view.getAutoridadeSelecionada();
        if (aut == null) {
            JOptionPane.showMessageDialog(view, "Por favor, selecione uma Autoridade Signatária.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("template_decisao.docx")) {
            if (is == null) {
                throw new IOException("Arquivo 'template_decisao.docx' não encontrado em resources.");
            }

            String path = arquivoService.getCaminhoParaSalvarDecisao(fatdAlvo);

            // 2. CRIA O MAPA DE VALORES DA TELA PARA ENVIAR AO GERADOR
            Map<String, String> valoresTela = new HashMap<>();
            Map<String, JComboBox<String>> combos = view.getCombosDinamicos();

            for (Map.Entry<String, JComboBox<String>> entry : combos.entrySet()) {
                String tag = entry.getKey(); // Ex: ${julgamento}
                JComboBox<String> combo = entry.getValue();
                String valor = (String) combo.getSelectedItem();

                if (valor != null) {
                    valoresTela.put(tag, valor);
                }
            }

            // 3. PASSA O MAPA PARA O GERADOR
            documentGenerator.generate(fatdAlvo, aut, is, path, valoresTela);

            AuditService.getInstance().logAction("Gerou Documento Decisão", "FATD " + fatdAlvo.getNup());
            JOptionPane.showMessageDialog(view, "Documento gerado com sucesso em:\n" + path, "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Erro ao gerar documento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> transformarEmLista(Object obj) {
        if (obj instanceof List) return (List<String>) obj;
        return new ArrayList<>();
    }
}