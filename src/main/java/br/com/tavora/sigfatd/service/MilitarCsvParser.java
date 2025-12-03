package br.com.tavora.sigfatd.service;

import br.com.tavora.sigfatd.model.Militar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MilitarCsvParser {
    public List<Militar> parse(File csvFile) throws IOException {
        List<Militar> militares = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile, StandardCharsets.UTF_8))) {
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split("[,;]");

                if (columns.length == 4) {
                    try {
                        String idtMilitar = columns[0].replace("\"", "").trim();
                        String postoGraduacao = columns[1].replace("\"", "").trim();
                        String nomeCompleto = columns[2].replace("\"", "").trim();
                        String nomeGuerra = columns[3].replace("\"", "").trim();

                        if (idtMilitar.length() < 10) {
                            idtMilitar = String.format("%010d", Long.parseLong(idtMilitar));
                        }

                        militares.add(new Militar(0, idtMilitar, nomeGuerra, nomeCompleto, postoGraduacao));
                    } catch (Exception e) {
                        System.err.println("Erro ao processar a linha: " + line + " | Erro: " + e.getMessage());
                    }
                } else {
                    System.err.println("Linha ignorada (número de colunas inválido: " + columns.length + "): " + line);
                }
            }
        }
        return militares;
    }
}