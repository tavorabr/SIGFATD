package br.com.tavora.sigfatd.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TelaConfiguracoesView extends JDialog {

    private final JTextField campoProximoNup;
    private final JButton botaoSalvar;
    private final JButton botaoGerirFeriados;
    private final JButton botaoMilitarParticipante;
    private final JButton botaoAutoridadeCompetente;

    public TelaConfiguracoesView(Frame owner) {
        super(owner, "Configurações", true);
        setSize(500, 390);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Definir próximo Nº de Processo (FATD):"), gbc);

        gbc.gridx = 1;
        campoProximoNup = new JTextField(10);
        painel.add(campoProximoNup, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        botaoGerirFeriados = new JButton("Gerir Feriados");
        painel.add(botaoGerirFeriados, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        botaoMilitarParticipante = new JButton("Gerenciar Militar Participante");
        painel.add(botaoMilitarParticipante, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        botaoAutoridadeCompetente = new JButton("Gerenciar Autoridade Competente");
        painel.add(botaoAutoridadeCompetente, gbc);

        JPanel painelInferior = new JPanel(new BorderLayout());

        JPanel painelBotaoSalvar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton("Salvar e Fechar");
        painelBotaoSalvar.add(botaoSalvar);

        painelInferior.add(painelBotaoSalvar, BorderLayout.EAST);

        add(painel, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);
    }

    public String getProximoNup() { return campoProximoNup.getText(); }
    public void setProximoNup(int nup) { campoProximoNup.setText(String.valueOf(nup)); }
    public void adicionarAcaoSalvar(ActionListener acao) { botaoSalvar.addActionListener(acao); }
    public void adicionarAcaoGerirFeriados(ActionListener acao) { botaoGerirFeriados.addActionListener(acao); }
    public void adicionarAcaoMilitarParticipante(ActionListener acao) { botaoMilitarParticipante.addActionListener(acao); }
    public void adicionarAcaoAutoridadeCompetente(ActionListener acao) { botaoAutoridadeCompetente.addActionListener(acao); }

    public void fechar() { dispose(); }
}