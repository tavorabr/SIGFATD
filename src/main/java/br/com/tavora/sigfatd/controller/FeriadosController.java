package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.service.FeriadoRepository;
import br.com.tavora.sigfatd.view.TelaFeriadosView;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FeriadosController {

    private final TelaFeriadosView view;
    private final FeriadoRepository feriadoRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ObjectMapper mapper;
    private final HttpClient client;

    public FeriadosController(TelaFeriadosView view, FeriadoRepository feriadoRepository) {
        this.view = view;
        this.feriadoRepository = feriadoRepository;

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());

        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        initController();
    }

    private void initController() {
        view.adicionarAcaoAdicionar(e -> adicionarFeriado());
        view.adicionarAcaoRemover(e -> removerFeriado());
        view.adicionarAcaoFechar(e -> view.fechar());

        // Botão continua existindo para forçar atualização ou pegar outros anos
        view.adicionarAcaoImportarApi(e -> perguntarEImportarDaApi());

        // 1. Tenta carregar o que já tem
        atualizarListaDeFeriados();

        // 2. VERIFICAÇÃO AUTOMÁTICA AO INICIAR
        verificarFeriadosAnoAtual();
    }

    // --- LÓGICA AUTOMÁTICA ---
    private void verificarFeriadosAnoAtual() {
        int anoAtual = LocalDate.now().getYear();

        // Verifica se já existe algum feriado cadastrado para o ano atual
        boolean temFeriadosAnoAtual = feriadoRepository.getTodosFeriados().keySet().stream()
                .anyMatch(data -> data.getYear() == anoAtual);

        // Se não tiver (ou se a lista estiver vazia), importa automaticamente
        if (!temFeriadosAnoAtual) {
            System.out.println("Nenhum feriado de " + anoAtual + " encontrado. Importando automaticamente...");
            // Passa 'true' para ser silencioso (não mostrar mensagem de sucesso, só de erro)
            executarImportacaoApi(anoAtual, true);
        }
    }

    // --- LÓGICA MANUAL (BOTÃO) ---
    private void perguntarEImportarDaApi() {
        String inputAno = JOptionPane.showInputDialog(view, "Digite o ano para importar (ex: 2025):", LocalDate.now().getYear());
        if (inputAno == null || inputAno.trim().isEmpty()) return;

        try {
            int ano = Integer.parseInt(inputAno);
            // Passa 'false' para mostrar mensagem de sucesso ("Importação concluída")
            executarImportacaoApi(ano, false);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Ano inválido.");
        }
    }

    // --- MÉTODO CENTRAL DE IMPORTAÇÃO (REUTILIZÁVEL) ---
    private void executarImportacaoApi(int ano, boolean modoSilencioso) {
        new Thread(() -> {
            try {
                // Só muda o cursor se a tela estiver visível
                if (view.isVisible()) {
                    SwingUtilities.invokeLater(() -> view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)));
                }

                String url = "https://brasilapi.com.br/api/feriados/v1/" + ano;
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<FeriadoDto> feriadosApi = mapper.readValue(response.body(), new TypeReference<List<FeriadoDto>>(){});

                    SwingUtilities.invokeLater(() -> {
                        int contador = 0;
                        for (FeriadoDto dto : feriadosApi) {
                            if (!feriadoRepository.isFeriado(dto.getDate())) {
                                feriadoRepository.adicionarFeriado(dto.getDate(), dto.getName());
                                contador++;
                            }
                        }
                        atualizarListaDeFeriados();

                        // Só mostra mensagem de sucesso se NÃO for modo silencioso
                        if (!modoSilencioso) {
                            JOptionPane.showMessageDialog(view, "Importação concluída! " + contador + " novos feriados adicionados.");
                        }
                    });
                } else {
                    // Erros sempre devem ser mostrados
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(view, "Erro na API: " + response.statusCode()));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                // Erros de conexão podem ser ignorados na inicialização silenciosa se preferir,
                // mas aqui deixei visível para você saber se falhou.
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(view, "Erro ao conectar na API: " + ex.getMessage()));
            } finally {
                if (view.isVisible()) {
                    SwingUtilities.invokeLater(() -> view.setCursor(Cursor.getDefaultCursor()));
                }
            }
        }).start();
    }

    // ... (O RESTO DO CÓDIGO PERMANECE IGUAL: adicionarFeriado, removerFeriado, DTO, etc.) ...

    private void adicionarFeriado() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JFormattedTextField campoData = createFormattedDateField();
        JTextField campoNome = new JTextField();

        panel.add(new JLabel("Data (dd/MM/yyyy):"));
        panel.add(campoData);
        panel.add(new JLabel("Nome do Feriado:"));
        panel.add(campoNome);

        int result = JOptionPane.showConfirmDialog(view, panel, "Adicionar Novo Feriado",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String dataTexto = campoData.getText();
            String nomeTexto = campoNome.getText().trim();

            if (dataTexto.contains("_") || nomeTexto.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Ambos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalDate novoFeriado = LocalDate.parse(dataTexto, formatter);
                feriadoRepository.adicionarFeriado(novoFeriado, nomeTexto);
                atualizarListaDeFeriados();
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(view, "Formato de data inválido. Use dd/MM/yyyy.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JFormattedTextField createFormattedDateField() {
        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            return new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            System.err.println("Falha ao criar máscara de data: " + e.getMessage());
            return new JFormattedTextField();
        }
    }

    private void removerFeriado() {
        LocalDate feriadoSelecionado = view.getFeriadoSelecionado();
        if (feriadoSelecionado == null) return;

        String nomeFeriado = feriadoRepository.getTodosFeriados().get(feriadoSelecionado);
        String mensagem = String.format("Deseja remover o feriado '%s' (%s)?", nomeFeriado, feriadoSelecionado.format(formatter));

        int confirmacao = JOptionPane.showConfirmDialog(view, mensagem, "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            feriadoRepository.removerFeriado(feriadoSelecionado);
            atualizarListaDeFeriados();
        }
    }

    private void atualizarListaDeFeriados() {
        view.atualizarTabela(feriadoRepository.getTodosFeriados());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeriadoDto {
        @JsonProperty("date")
        private LocalDate date;

        @JsonProperty("name")
        private String name;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}