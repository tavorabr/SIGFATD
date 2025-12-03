package br.com.tavora.sigfatd.service;

import br.com.tavora.sigfatd.model.Autoridade;
import br.com.tavora.sigfatd.model.FATD;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DecisaoDocumentGenerator {

    /**
     * Agora aceita um mapa de 'valoresTela' para substituir as tags pelos valores exatos da interface.
     */
    public void generate(FATD fatd, Autoridade autoridade, InputStream templateInputStream, String outputPath, Map<String, String> valoresTela) throws IOException {
        try (XWPFDocument document = new XWPFDocument(templateInputStream)) {
            // Mapa de Substituição mesclando dados do FATD com dados da Tela
            Map<String, String> replacements = createReplacementsMap(fatd, autoridade, valoresTela);

            // 1. Substituir nos Parágrafos Principais
            for (XWPFParagraph p : document.getParagraphs()) {
                replaceInParagraph(p, replacements);
            }

            // 2. Substituir em TODAS as Tabelas
            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            replaceInParagraph(p, replacements);
                        }
                    }
                }
            }

            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }
        }
    }

    private Map<String, String> createReplacementsMap(FATD fatd, Autoridade autoridade, Map<String, String> valoresTela) {
        Map<String, String> map = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));

        // --- 1. PREENCHE COM VALORES DA TELA (Prioridade Alta) ---
        // Isso garante que o que você vê no Combo é o que vai para o Word
        if (valoresTela != null) {
            map.putAll(valoresTela);
        }

        // --- 2. DADOS DO PROCESSO (Que não estão nos combos) ---
        map.put("${processo}", String.valueOf(fatd.getNup()));
        map.put("${referencia}", fatd.getReferencia() != null ? fatd.getReferencia() : "");
        map.put("${dataProcesso}", fatd.getDataProcesso() != null ? fatd.getDataProcesso().format(DateTimeFormatter.ofPattern("yyyy")) : "");
        map.put("${relato do fato}", fatd.getRelatoFato() != null ? fatd.getRelatoFato() : "");

        // --- 3. ACUSADO ---
        if (fatd.getMilitar() != null) {
            map.put("${postoGraduacao}", fatd.getMilitar().getPostoGraduacao());
            map.put("${nomeCompleto}", fatd.getMilitar().getNomeCompleto());
            map.put("${nomeGuerra}", fatd.getMilitar().getNomeGuerra().toUpperCase());
        }

        // --- 4. DATA ATUAL E AUTORIDADE ---
        map.put("${data}", java.time.LocalDate.now().format(dtf));

        if (autoridade != null) {
            map.put("${nomeCompletoAutoridade}", autoridade.getNomeCompleto().toUpperCase());
            map.put("${postoAutoridade}", autoridade.getPostoGraduacao());
            map.put("${titulacao}", autoridade.getTitulacao());
        } else {
            map.put("${nomeCompletoAutoridade}", "AUTORIDADE NÃO SELECIONADA");
            map.put("${postoAutoridade}", "");
            map.put("${titulacao}", "");
        }

        // --- GARANTIA DE PREENCHIMENTO ---
        // Caso alguma tag importante não tenha vindo da tela (ex: erro no combo), colocamos um fallback
        map.putIfAbsent("${julgamento}", "Julgamento realizado.");
        map.putIfAbsent("${enquadramento}", "_________________");
        map.putIfAbsent("${punicao}", "_________________");

        return map;
    }

    private void replaceInParagraph(XWPFParagraph p, Map<String, String> replacements) {
        String text = p.getText();
        if (text == null || text.isEmpty()) return;

        boolean hasMatch = false;
        // Estratégia de Replace: Primeiro as tags maiores para evitar conflitos
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String key = entry.getKey();
            if (text.contains(key)) {
                hasMatch = true;
                text = text.replace(key, entry.getValue());
            }
        }

        if (hasMatch) {
            // Remove runs antigos e insere o texto novo mantendo formatação básica
            // Nota: Se o template Word tiver formatações complexas no MEIO da linha, isso pode simplificar para Times New Roman 12
            while (p.getRuns().size() > 0) {
                p.removeRun(0);
            }
            XWPFRun run = p.createRun();
            run.setText(text);
            run.setFontFamily("Times New Roman");
            run.setFontSize(12);
        }
    }
}