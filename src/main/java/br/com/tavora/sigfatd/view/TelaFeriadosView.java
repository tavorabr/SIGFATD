package br.com.tavora.sigfatd.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TelaFeriadosView extends JDialog {

    private JTable tabelaFeriados;
    private DefaultTableModel tableModel;
    private JButton botaoAdicionar;
    private JButton botaoRemover;
    private JButton botaoImportarApi;
    private JButton botaoFechar;

    public TelaFeriadosView(Dialog owner) {
        super(owner, "Gerir Feriados", true);
        setSize(750, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCentral = new JPanel(new BorderLayout(5, 5));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = {"Data", "Nome do Feriado"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaFeriados = new JTable(tableModel);
        tabelaFeriados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        botaoAdicionar = new JButton("Adicionar");
        botaoRemover = new JButton("Remover");
        botaoImportarApi = new JButton("Sincronizar API");

        botaoImportarApi.setBackground(new Color(75, 105, 79));
        botaoImportarApi.setForeground(Color.WHITE);
        botaoImportarApi.setFont(botaoImportarApi.getFont().deriveFont(Font.BOLD));
        botaoImportarApi.setFocusPainted(false);

        botaoRemover.setEnabled(false);

        painelAcoes.add(botaoAdicionar);
        painelAcoes.add(botaoRemover);
        painelAcoes.add(botaoImportarApi);

        JPanel painelFechar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoFechar = new JButton("Fechar");
        painelFechar.add(botaoFechar);

        painelCentral.add(new JLabel("Feriados Cadastrados:"), BorderLayout.NORTH);
        painelCentral.add(new JScrollPane(tabelaFeriados), BorderLayout.CENTER);
        painelCentral.add(painelAcoes, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);
        add(painelFechar, BorderLayout.SOUTH);

        tabelaFeriados.getSelectionModel().addListSelectionListener(e -> {
            botaoRemover.setEnabled(tabelaFeriados.getSelectedRow() != -1);
        });
    }

    public void atualizarTabela(Map<LocalDate, String> feriados) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Map.Entry<LocalDate, String>> sortedList = feriados.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
        for (Map.Entry<LocalDate, String> entry : sortedList) {
            tableModel.addRow(new Object[]{entry.getKey().format(formatter), entry.getValue()});
        }
    }

    public LocalDate getFeriadoSelecionado() {
        int selectedRow = tabelaFeriados.getSelectedRow();
        if (selectedRow < 0) return null;
        String dataTexto = (String) tableModel.getValueAt(selectedRow, 0);
        return LocalDate.parse(dataTexto, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void adicionarAcaoAdicionar(ActionListener acao) { botaoAdicionar.addActionListener(acao); }
    public void adicionarAcaoRemover(ActionListener acao) { botaoRemover.addActionListener(acao); }
    public void adicionarAcaoFechar(ActionListener acao) { botaoFechar.addActionListener(acao); }
    public void adicionarAcaoImportarApi(ActionListener acao) { botaoImportarApi.addActionListener(acao); }
    public void fechar() { dispose(); }
}