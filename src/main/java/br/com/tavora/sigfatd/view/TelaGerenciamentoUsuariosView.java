package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.Role;
import br.com.tavora.sigfatd.model.User;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class TelaGerenciamentoUsuariosView extends JDialog {

    private final JList<User> listaUsuarios;
    private final DefaultListModel<User> listModel;
    private final JTextField campoUsuario;
    private final JPasswordField campoSenha;
    private final JComboBox<Role> comboRole;
    private final JButton botaoSalvar, botaoExcluir, botaoFechar;

    public TelaGerenciamentoUsuariosView(Frame owner) {
        super(owner, "Gerenciar Usuários", true);
        setSize(800, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        listModel = new DefaultListModel<>();
        listaUsuarios = new JList<>(listModel);
        JScrollPane scrollLista = new JScrollPane(listaUsuarios);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Usuários Cadastrados"));

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.weightx = 0.3; painelFormulario.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; campoUsuario = new JTextField(); painelFormulario.add(campoUsuario, gbc);
        gbc.gridx = 0; gbc.gridy = 1; painelFormulario.add(new JLabel("Senha (deixe em branco para manter):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; campoSenha = new JPasswordField(); painelFormulario.add(campoSenha, gbc);
        gbc.gridx = 0; gbc.gridy = 2; painelFormulario.add(new JLabel("Nível de Acesso:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; comboRole = new JComboBox<>(Role.values()); painelFormulario.add(comboRole, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton("Salvar");
        botaoExcluir = new JButton("Excluir");
        botaoFechar = new JButton("Fechar");
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoFechar);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLista, painelFormulario);
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public void atualizarLista(List<User> usuarios) { listModel.clear(); listModel.addAll(usuarios); }
    public void setDadosFormulario(User user) { if (user != null) { campoUsuario.setText(user.getUsername()); comboRole.setSelectedItem(user.getRole()); campoSenha.setText(""); } else { campoUsuario.setText(""); comboRole.setSelectedIndex(0); campoSenha.setText(""); } }
    public User getUsuarioSelecionado() { return listaUsuarios.getSelectedValue(); }
    public String getUsernameForm() { return campoUsuario.getText(); }
    public char[] getPasswordForm() { return campoSenha.getPassword(); }
    public Role getRoleForm() { return (Role) comboRole.getSelectedItem(); }
    public void limparSelecaoLista() { listaUsuarios.clearSelection(); }
    public void adicionarAcaoSelecaoLista(ListSelectionListener listener) { listaUsuarios.getSelectionModel().addListSelectionListener(listener); }
    public void adicionarAcaoSalvar(ActionListener listener) { botaoSalvar.addActionListener(listener); }
    public void adicionarAcaoExcluir(ActionListener listener) { botaoExcluir.addActionListener(listener); }
    public void adicionarAcaoFechar(ActionListener listener) { botaoFechar.addActionListener(listener); }
    public void fechar() { dispose(); }
}