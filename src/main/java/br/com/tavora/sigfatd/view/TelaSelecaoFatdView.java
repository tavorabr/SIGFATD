package br.com.tavora.sigfatd.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class TelaSelecaoFatdView extends JDialog {

    private JTextField campoNup;
    private JButton botaoSelecionar;
    private JButton botaoCancelar;
    private JTable tabelaFatds;
    private DefaultTableModel tableModel;

    public TelaSelecaoFatdView(Frame owner) {
        super(owner, "Selecionar FATD para Julgamento", true);
        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelSuperior.setBorder(BorderFactory.createTitledBorder("Digite o NUP ou selecione na lista abaixo"));

        painelSuperior.add(new JLabel("NUP da FATD:"));
        campoNup = new JTextField(10);
        botaoSelecionar = new JButton("Julgar Selecionada");

        painelSuperior.add(campoNup);
        painelSuperior.add(botaoSelecionar);

        String[] colunas = {"NUP", "Ano", "Militar", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaFatds = new JTable(tableModel);
        tabelaFatds.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaFatds.setRowHeight(25);

        tabelaFatds.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaFatds.getColumnModel().getColumn(1).setPreferredWidth(50);
        tabelaFatds.getColumnModel().getColumn(2).setPreferredWidth(300);
        tabelaFatds.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tabelaFatds);

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoCancelar = new JButton("Cancelar");
        painelInferior.add(botaoCancelar);

        add(painelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTabela() { return tabelaFatds; }
    public String getNupDigitado() { return campoNup.getText().trim(); }

    public Integer getNupSelecionadoNaTabela() {
        int row = tabelaFatds.getSelectedRow();
        if (row != -1) {
            return Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        }
        return null;
    }

    public void adicionarAcaoSelecionar(ActionListener acao) {
        botaoSelecionar.addActionListener(acao);
        campoNup.addActionListener(acao);
    }
    public void adicionarAcaoCancelar(ActionListener acao) { botaoCancelar.addActionListener(acao); }
    public void adicionarListenerTabela(MouseAdapter adapter) { tabelaFatds.addMouseListener(adapter); }
    public void fechar() { dispose(); }
}