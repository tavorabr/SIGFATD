package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.FATD; // Importe FATD se for usar na tabela
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter; // Importar para formatar a data
import java.util.List;

public class TelaRelatorioView extends JDialog {

    private JTable tabelaRelatorios;
    private DefaultTableModel tableModel;
    private JButton btnFechar;

    public TelaRelatorioView(JFrame parent) {
        super(parent, "Relatório de FATDs", true); // Modal
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        String[] colunas = {"Nº FATD", "NUP Completo", "Militar", "Data Processo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaRelatorios = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelaRelatorios);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnFechar = new JButton("Fechar");
        painelBotoes.add(btnFechar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public void adicionarAcaoFechar(ActionListener listener) {
        btnFechar.addActionListener(listener);
    }

    public void atualizarTabela(List<FATD> fatds) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (FATD fatd : fatds) {
            tableModel.addRow(new Object[]{
                    fatd.getNup(),
                    fatd.getNupCompleto(),
                    fatd.getMilitar() != null ? fatd.getMilitar().getNomeGuerra() : "N/A",
                    fatd.getDataProcesso() != null ? fatd.getDataProcesso().format(formatter) : "N/A"
            });
        }
    }
}