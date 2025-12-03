package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.Militar;
import javax.swing.*;
import javax.swing.border.EmptyBorder; // IMPORT ADICIONADO
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class TelaGerenciamentoView extends JDialog {

    private JTable tabelaMilitares;
    private DefaultTableModel tableModel;
    private JButton botaoAdicionar;
    private JButton botaoEditar;
    private JButton botaoExcluir;
    private JButton botaoFechar;
    private JTextField campoPesquisa;

    public TelaGerenciamentoView(Frame owner) {
        super(owner, "Gerenciamento de Militares", true);
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel painelPesquisa = new JPanel(new BorderLayout(5, 5));
        painelPesquisa.setBorder(new EmptyBorder(10, 10, 0, 10)); // Margem
        campoPesquisa = new JTextField();
        painelPesquisa.add(new JLabel("Pesquisar:"), BorderLayout.WEST);
        painelPesquisa.add(campoPesquisa, BorderLayout.CENTER);

        add(painelPesquisa, BorderLayout.NORTH); // Adiciona o painel no topo da tela

        String[] colunas = {"ID", "Nome Completo", "Nome de Guerra", "Posto/Grad.", "Identidade"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaMilitares = new JTable(tableModel);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoAdicionar = new JButton("Adicionar Novo");
        botaoEditar = new JButton("Editar Selecionado");
        botaoExcluir = new JButton("Excluir Selecionado");
        botaoFechar = new JButton("Fechar");

        botaoEditar.setEnabled(false);
        botaoExcluir.setEnabled(false);

        painelBotoes.add(botaoAdicionar);
        painelBotoes.add(botaoEditar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(new JSeparator(SwingConstants.VERTICAL));
        painelBotoes.add(botaoFechar);

        add(new JScrollPane(tabelaMilitares), BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        tabelaMilitares.getSelectionModel().addListSelectionListener(e -> {
            boolean selecionado = tabelaMilitares.getSelectedRow() != -1;
            botaoEditar.setEnabled(selecionado);
            botaoExcluir.setEnabled(selecionado);
        });
    }

    public JTextField getCampoPesquisa() {
        return campoPesquisa;
    }

    public void atualizarTabela(List<Militar> militares) {
        tableModel.setRowCount(0);
        if (militares != null) {
            for (Militar m : militares) {
                tableModel.addRow(new Object[]{
                        m.getId(), m.getNomeCompleto(), m.getNomeGuerra(),
                        m.getPostoGraduacao(), m.getIdtMilitar()
                });
            }
        }
        tableModel.fireTableDataChanged();
    }

    public int getLinhaSelecionada() {
        return tabelaMilitares.getSelectedRow();
    }
    public JTable getTabelaMilitares() {
        return tabelaMilitares;
    }
    public void adicionarAcaoAdicionar(ActionListener acao) { botaoAdicionar.addActionListener(acao); }
    public void adicionarAcaoEditar(ActionListener acao) { botaoEditar.addActionListener(acao); }
    public void adicionarAcaoExcluir(ActionListener acao) { botaoExcluir.addActionListener(acao); }
    public void adicionarAcaoFechar(ActionListener acao) { botaoFechar.addActionListener(acao); }
    public void fechar() { dispose(); }
}