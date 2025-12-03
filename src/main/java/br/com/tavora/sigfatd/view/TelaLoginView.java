package br.com.tavora.sigfatd.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TelaLoginView extends JDialog {
    private final JTextField campoUsuario;
    private final JPasswordField campoSenha;
    private final JButton botaoLogin;

    public TelaLoginView(Frame owner) {
        super(owner, "Login - SIGFATD", true);
        setSize(450, 350);
        setMinimumSize(new Dimension(500, 280));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Color backgroundColor = new Color(245, 247, 250);
        Color panelColor = Color.WHITE;
        Color textColor = new Color(51, 51, 51);
        Color accentColor = new Color(75, 105, 79);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(panelColor);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("LOGIN SIGFATD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0,0,20,0);
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel labelUsuario = new JLabel("Usuário:");
        labelUsuario.setForeground(textColor);
        labelUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(labelUsuario, gbc);

        campoUsuario = new JTextField(20);
        campoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        formPanel.add(campoUsuario, gbc);

        JLabel labelSenha = new JLabel("Senha:");
        labelSenha.setForeground(textColor);
        labelSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(labelSenha, gbc);

        campoSenha = new JPasswordField(20);
        campoSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(campoSenha, gbc);

        botaoLogin = new JButton("Entrar");
        botaoLogin.setBackground(accentColor);
        botaoLogin.setForeground(Color.WHITE);
        botaoLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 0, 5);
        formPanel.add(botaoLogin, gbc);

        getRootPane().setDefaultButton(botaoLogin);
        mainPanel.add(formPanel, new GridBagConstraints());
    }

    public String getUsername() { return campoUsuario.getText().trim(); }
    public char[] getPassword() { return campoSenha.getPassword(); }
    public void adicionarAcaoLogin(ActionListener listener) { botaoLogin.addActionListener(listener); }
    public void fechar() { dispose(); }
    public void exibirMensagemErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro de Login", JOptionPane.ERROR_MESSAGE);
    }
}