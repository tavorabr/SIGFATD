package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.view.dto.PrazoInfo;
import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TelaPrazosView extends JDialog {

    private final JTable tabelaPrazos;
    private final DefaultTableModel tableModel;
    private final JButton btnFechar;
    private final JButton btnEditar;
    private final JButton btnConcluido;

    private static final int COLUNA_STATUS_DEFESA = 4;
    private static final int COLUNA_STATUS_DECISAO = 6;
    private static final int COLUNA_NOTA_BI = 7;
    private static final int COLUNA_BI = 8;
    private static final int COLUNA_QTD_DIAS = 9;
    private static final int COLUNA_PUNICAO = 10;

    private List<PrazoInfo> prazosAtuais;
    private boolean isEditavel = false;

    private final Color COR_VENCIDO = new Color(255, 204, 204);
    private final Color COR_PROXIMO = new Color(255, 255, 204);
    private final Color COR_CONCLUIDO = new Color(204, 255, 204);
    private final Color COR_ENTREGUE = new Color(204, 230, 255);

    public TelaPrazosView(JFrame parent) {
        super(parent, "Controle de Prazos", true);

        setSize(1400, 800);

        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        this.prazosAtuais = new ArrayList<>();

        String[] colunas = {"Nº FATD", "Militar", "Data de Início", "Prazo Defesa", "Status Defesa",
                "Prazo Decisão", "Status Decisão", "Nota ao BI", "BI", "Qtd Dias", "Punição"};

        this.tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (isEditavel) {
                    return column == COLUNA_STATUS_DEFESA || column == COLUNA_STATUS_DECISAO ||
                            column == COLUNA_NOTA_BI || column == COLUNA_BI ||
                            column == COLUNA_QTD_DIAS || column == COLUNA_PUNICAO;
                }
                return false;
            }
        };

        this.tabelaPrazos = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (row < 0 || row >= prazosAtuais.size()) {
                    return c;
                }

                PrazoInfo info = prazosAtuais.get(row);
                Object cellValue = getValueAt(row, column);
                String status = (cellValue != null) ? cellValue.toString() : "";

                if (isRowSelected(row)) {
                    c.setBackground(tabelaPrazos.getSelectionBackground());
                    c.setForeground(tabelaPrazos.getSelectionForeground());
                }
                else if (info.isConcluido()) {
                    c.setBackground(COR_CONCLUIDO);
                    c.setForeground(Color.BLACK);
                }
                else {
                    if ("ENTREGUE".equals(status) || "DECISÃO FEITA".equals(status)) {
                        c.setBackground(COR_ENTREGUE);
                        c.setForeground(Color.BLACK);
                    } else if ("VENCIDO".equals(status)) {
                        c.setBackground(COR_VENCIDO);
                        c.setForeground(Color.BLACK);
                    } else if ("PRÓXIMO DO VENCIMENTO".equals(status)) {
                        c.setBackground(COR_PROXIMO);
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(tabelaPrazos.getBackground());
                        c.setForeground(tabelaPrazos.getForeground());
                    }
                }
                return c;
            }
        };

        tabelaPrazos.setRowHeight(25);
        tabelaPrazos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarEditoresDeTabela();

        JScrollPane scrollPane = new JScrollPane(tabelaPrazos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelBotoes = new JPanel(new BorderLayout());
        this.btnFechar = new JButton("Fechar");
        this.btnEditar = new JButton("Editar Data de Início");
        this.btnConcluido = new JButton("Concluído");
        btnEditar.setVisible(false);

        JPanel painelEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelEsquerda.add(btnEditar);
        painelEsquerda.add(btnConcluido);

        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelDireita.add(btnFechar);

        painelBotoes.add(painelEsquerda, BorderLayout.WEST);
        painelBotoes.add(painelDireita, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void configurarEditoresDeTabela() {
        String[] opcoesStatusDefesa = {"EM DIA", "PRÓXIMO DO VENCIMENTO", "VENCIDO", "ENTREGUE"};
        TableColumn defesaColumn = tabelaPrazos.getColumnModel().getColumn(COLUNA_STATUS_DEFESA);
        defesaColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(opcoesStatusDefesa)));

        String[] opcoesStatusDecisao = {"EM DIA", "PRÓXIMO DO VENCIMENTO", "VENCIDO", "DECISÃO FEITA"};
        TableColumn decisaoColumn = tabelaPrazos.getColumnModel().getColumn(COLUNA_STATUS_DECISAO);
        decisaoColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(opcoesStatusDecisao)));

        String[] punicoes = {
                "", "Advertencia", "Impedimento Disciplinar", "Repreensão",
                "Detido Disciplinar", "Prisão"
        };
        TableColumn punicaoColumn = tabelaPrazos.getColumnModel().getColumn(COLUNA_PUNICAO);
        JComboBox<String> comboBoxPunicao = new JComboBox<>(punicoes);
        punicaoColumn.setCellEditor(new DefaultCellEditor(comboBoxPunicao));

        punicaoColumn.setPreferredWidth(180);
    }

    public void atualizarTabela(List<PrazoInfo> prazos) {
        this.prazosAtuais = prazos;

        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (PrazoInfo prazo : prazos) {
            tableModel.addRow(new Object[]{
                    prazo.getNup(),
                    prazo.getNomeGuerra(),
                    prazo.getDataInicio().format(formatter),
                    prazo.getDataPrazoDefesa().format(formatter),
                    prazo.getStatusDefesa(),
                    prazo.getDataPrazoDecisao().format(formatter),
                    prazo.getStatusDecisao(),
                    prazo.getNotaBi(),
                    prazo.getBi(),
                    prazo.getQuantidadeDias() == 0 ? "" : prazo.getQuantidadeDias(),
                    prazo.getPunicao()
            });
        }
    }

    public Integer getNupSelecionado() {
        int selectedRow = tabelaPrazos.getSelectedRow();
        if (selectedRow >= 0) {
            Object nupValue = tableModel.getValueAt(selectedRow, 0);
            if (nupValue instanceof Integer) {
                return (Integer) nupValue;
            }
        }
        return null;
    }

    public void adicionarAcaoEdicaoTabela(TableModelListener listener) {
        tableModel.addTableModelListener(listener);
    }

    public void setEdicaoCamposHabilitada(boolean habilitada) {
        this.isEditavel = habilitada;
    }

    public void adicionarAcaoFechar(ActionListener actionListener) {
        btnFechar.addActionListener(actionListener);
    }

    public void adicionarAcaoEditar(ActionListener actionListener) {
        btnEditar.addActionListener(actionListener);
    }

    public void adicionarAcaoConcluido(ActionListener actionListener) {
        btnConcluido.addActionListener(actionListener);
    }

    public void setBotaoEditarVisivel(boolean visivel) {
        btnEditar.setVisible(visivel);
        btnConcluido.setVisible(visivel);
    }

    public void fechar() {
        dispose();
    }
}