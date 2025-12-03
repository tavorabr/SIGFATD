package br.com.tavora.sigfatd.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NupCsvParser {

    public Map<Integer, String> parse(File csvFile) throws IOException {
        Map<Integer, String> nupMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile, StandardCharsets.UTF_8))) {
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split("[,;]");

                if (columns.length == 2) {
                    try {
                        int fatdNumero = Integer.parseInt(columns[0].trim());
                        String nupCompleto = columns[1].trim();
                        nupMap.put(fatdNumero, nupCompleto);
                    } catch (NumberFormatException e) {
                        System.err.println("Erro ao converter número na linha: " + line);
                    }
                }
            }
        }
        return nupMap;
    }
}