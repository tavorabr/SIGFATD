package br.com.tavora.sigfatd.service;

import br.com.tavora.sigfatd.model.FATD;
import br.com.tavora.sigfatd.model.Militar;
import br.com.tavora.sigfatd.model.Participante;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FatdDocumentGenerator {

    public void generate(FATD fatdData, Participante participante, InputStream templateInputStream, String outputPath) throws IOException {
        if (templateInputStream == null) {
            throw new IOException("O arquivo de template não foi encontrado no classpath.");
        }
        try (XWPFDocument document = new XWPFDocument(templateInputStream)) {
            Map<String, String> replacements = createReplacementsMap(fatdData, participante);

            document.getParagraphs().forEach(p -> replaceInParagraph(p, replacements));
            document.getTables().forEach(table -> table.getRows().forEach(row -> row.getTableCells().forEach(cell ->
                    cell.getParagraphs().forEach(p -> replaceInParagraph(p, replacements))
            )));

            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }
        }
    }

    private Map<String, String> createReplacementsMap(FATD fatdData, Participante participante) {
        Map<String, String> replacements = new HashMap<>();
        Militar militarArrolado = fatdData.getMilitar();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        String dataFormatada = fatdData.getDataProcesso() != null ? fatdData.getDataProcesso().format(formatter) : "";
        String nupFormatado = String.format("%d/%d", fatdData.getNup(), fatdData.getDataProcesso().getYear());

        replacements.put("${processo}", nupFormatado);
        replacements.put("${nup}", fatdData.getNupCompleto());
        replacements.put("${dataProcesso}", dataFormatada);
        replacements.put("${referencia}", fatdData.getReferencia());
        replacements.put("${relatoFato}", fatdData.getRelatoFato());

        if (militarArrolado != null) {
            replacements.put("${postoGraduacao}", militarArrolado.getPostoGraduacao());
            replacements.put("${nomeCompleto}", militarArrolado.getNomeCompleto());
            replacements.put("${IDT_MILITAR_FORMATADO}", formatarIdtMilitar(militarArrolado.getIdtMilitar()));
        }

        if (participante != null) {
            replacements.put("${postoGraduacaoParticipante}", participante.getPostoGraduacao());
            replacements.put("${nomeCompletoParticipante}", participante.getNomeCompleto());
            replacements.put("${idtMilParticipante}", participante.getIdtMil());
        }
        return replacements;
    }

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        String paragraphText = paragraph.getText();
        boolean textChanged = false; for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue() : "";
            if (key != null && paragraphText.contains(key)) {
                paragraphText = paragraphText.replace(key, value); textChanged = true;
            }
        }
        if (textChanged) {
            while (!paragraph.getRuns().isEmpty()) {
                paragraph.removeRun(0);
            }
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(paragraphText);
            newRun.setFontFamily("Times New Roman");
            newRun.setFontSize(12);
        }
    }

    private String formatarIdtMilitar(String idtMilitar) {
        if (idtMilitar == null) return "";
        String numerosApenas = idtMilitar.replaceAll("[^0-9]", "");
        if (numerosApenas.length() == 9) {
            String idtComZero = "0" + numerosApenas;
            return String.format("%s.%s.%s-%s", idtComZero.substring(0, 3), idtComZero.substring(3, 6), idtComZero.substring(6, 9), idtComZero.substring(9));
        }
        if (numerosApenas.length() == 10) {
            return String.format("%s.%s.%s-%s", numerosApenas.substring(0, 3), numerosApenas.substring(3, 6),
                    numerosApenas.substring(6, 9), numerosApenas.substring(9));
        } return idtMilitar;
    }
}