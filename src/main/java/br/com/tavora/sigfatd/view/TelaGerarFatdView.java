package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.Militar;
import br.com.tavora.sigfatd.model.Participante; // Import necessário

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class TelaGerarFatdView extends JDialog {

    private final JList<Militar> listaMilitares;
    private final DefaultListModel<Militar> listModel;
    private final JTextField campoPesquisa;
    private final JTextField txtNupCompleto;
    private final JTextField txtNumeroFatd;
    private final JTextField txtReferencia;
    private final JTextArea txtRelatoFatos;
    private final JComboBox<Participante> comboParticipante;

    private final JLabel lblNomeMilitarDetalhes;
    private final JLabel lblPostoGradMilitarDetalhes;
    private final JLabel lblIdtMilitarDetalhes;
    private final JButton btnGerarDocumento;
    private final JButton btnInserirNup;

    public TelaGerarFatdView(JFrame parent) {
        super(parent, "Gerar FATD", true);
        setSize(900, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        listModel = new DefaultListModel<>();
        listaMilitares = new JList<>(listModel);
        JScrollPane scrollMilitares = new JScrollPane(listaMilitares);
        scrollMilitares.setPreferredSize(new Dimension(300, 0));

        campoPesquisa = new JTextField("Pesquisar militar...");
        campoPesquisa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { if (campoPesquisa.getText().equals("Pesquisar militar...")) campoPesquisa.setText(""); }
            public void focusLost(java.awt.event.FocusEvent evt) { if (campoPesquisa.getText().isEmpty()) campoPesquisa.setText("Pesquisar militar..."); }
        });

        JPanel painelEsquerda = new JPanel(new BorderLayout());
        painelEsquerda.add(campoPesquisa, BorderLayout.NORTH);
        painelEsquerda.add(scrollMilitares, BorderLayout.CENTER);
        add(painelEsquerda, BorderLayout.WEST);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; painelFormulario.add(new JLabel("NUP Completo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; txtNupCompleto = new JTextField(20); txtNupCompleto.setEditable(false); painelFormulario.add(txtNupCompleto, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1; btnInserirNup = new JButton("Inserir NUP"); painelFormulario.add(btnInserirNup, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; painelFormulario.add(new JLabel("Nº FATD:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; txtNumeroFatd = new JTextField(20); txtNumeroFatd.setEditable(false); painelFormulario.add(txtNumeroFatd, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; painelFormulario.add(new JLabel("Referência:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; txtReferencia = new JTextField(20); painelFormulario.add(txtReferencia, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHWEST; painelFormulario.add(new JLabel("Relato dos Fatos:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        txtRelatoFatos = new JTextArea(10, 20); txtRelatoFatos.setLineWrap(true); txtRelatoFatos.setWrapStyleWord(true);
        painelFormulario.add(new JScrollPane(txtRelatoFatos), gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 0; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.HORIZONTAL; painelFormulario.add(new JLabel("Militar Participante:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; comboParticipante = new JComboBox<>(); painelFormulario.add(comboParticipante, gbc);
        row++;

        add(new JScrollPane(painelFormulario), BorderLayout.CENTER);

        JPanel painelDetalhesMilitar = new JPanel();
        painelDetalhesMilitar.setLayout(new BoxLayout(painelDetalhesMilitar, BoxLayout.Y_AXIS));
        painelDetalhesMilitar.setBorder(BorderFactory.createTitledBorder("Militar Selecionado"));
        lblNomeMilitarDetalhes = new JLabel("Nome: ");
        lblPostoGradMilitarDetalhes = new JLabel("Posto/Grad: ");
        lblIdtMilitarDetalhes = new JLabel("IDT: ");
        painelDetalhesMilitar.add(lblNomeMilitarDetalhes);
        painelDetalhesMilitar.add(lblPostoGradMilitarDetalhes);
        painelDetalhesMilitar.add(lblIdtMilitarDetalhes);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGerarDocumento = new JButton("Gerar FATD");
        painelBotoes.add(btnGerarDocumento);

        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(painelDetalhesMilitar, BorderLayout.CENTER);
        painelInferior.add(painelBotoes, BorderLayout.SOUTH);
        add(painelInferior, BorderLayout.SOUTH);
    }

    public void setParticipantes(List<Participante> participantes) {
        comboParticipante.setModel(new DefaultComboBoxModel<>(new Vector<>(participantes)));
    }

    public Participante getParticipanteSelecionado() {
        return (Participante) comboParticipante.getSelectedItem();
    }

    public DefaultListModel<Militar> getListModel() { return listModel; }
    public JTextField getCampoPesquisa() { return campoPesquisa; }
    public JList<Militar> getListaMilitares() { return listaMilitares; }
    public Militar getMilitarSelecionado() { return listaMilitares.getSelectedValue(); }
    public String getNupCompleto() { return txtNupCompleto.getText(); }
    public void setNupCompleto(String nupCompleto) { txtNupCompleto.setText(nupCompleto); }
    public String getNumeroFatdTexto() { return txtNumeroFatd.getText(); }
    public void setNumeroFatd(String numeroFatd) { txtNumeroFatd.setText(numeroFatd); }
    public String getRelatoFatos() { return txtRelatoFatos.getText(); }
    public String getReferencia() { return txtReferencia.getText(); }
    public void exibirDetalhesMilitar(Militar militar) {
        if (militar != null) {
            lblNomeMilitarDetalhes.setText("Nome: " + militar.getNomeCompleto());
            lblPostoGradMilitarDetalhes.setText("Posto/Grad: " + militar.getPostoGraduacao());
            lblIdtMilitarDetalhes.setText("IDT: " + militar.getIdtMilitar());
        } else {
            lblNomeMilitarDetalhes.setText("Nome: ");
            lblPostoGradMilitarDetalhes.setText("Posto/Grad: ");
            lblIdtMilitarDetalhes.setText("IDT: ");
        }
    }
    public void adicionarAcaoGerarDocumento(ActionListener listener) { btnGerarDocumento.addActionListener(listener); }
    public void adicionarAcaoInserirNup(ActionListener listener) { btnInserirNup.addActionListener(listener); }
    public void fechar() { dispose(); }
}