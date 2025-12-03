package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.Role;
import br.com.tavora.sigfatd.model.User;
import br.com.tavora.sigfatd.service.AuditService;
import org.mindrot.jbcrypt.BCrypt;
import br.com.tavora.sigfatd.service.PersistenceService;
import br.com.tavora.sigfatd.service.UserRepository;
import br.com.tavora.sigfatd.view.TelaGerenciamentoUsuariosView;
import javax.swing.*;

public class GerenciamentoUsuariosController {
    private final TelaGerenciamentoUsuariosView view;
    private final UserRepository userRepository;
    private final PersistenceService persistenceService;

    public GerenciamentoUsuariosController(TelaGerenciamentoUsuariosView view, UserRepository userRepository, PersistenceService persistenceService) {
        this.view = view;
        this.userRepository = userRepository;
        this.persistenceService = persistenceService;
        initController();
    }

    private void initController() {
        view.atualizarLista(userRepository.getAllUsers());
        view.adicionarAcaoSalvar(e -> salvar());
        view.adicionarAcaoExcluir(e -> excluir());
        view.adicionarAcaoFechar(e -> view.fechar());
        view.adicionarAcaoSelecaoLista(e -> {
            if (!e.getValueIsAdjusting()) {
                exibirDetalhes();
            }
        });
    }

    private void exibirDetalhes() { view.setDadosFormulario(view.getUsuarioSelecionado()); }

    private void salvar() {
        String username = view.getUsernameForm();
        char[] password = view.getPasswordForm();
        Role role = view.getRoleForm();
        User selecionado = view.getUsuarioSelecionado();

        if (username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "O nome de usuário é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = (password.length > 0) ? BCrypt.hashpw(new String(password), BCrypt.gensalt()) : null;

        if (selecionado != null) {
            AuditService.getInstance().logAction("Atualizou o usuário", selecionado.getUsername());
            userRepository.updateUser(selecionado.getUsername(), new User(username, hashedPassword, role));
        } else {
            if (hashedPassword == null) {
                JOptionPane.showMessageDialog(view, "A senha é obrigatória para novos usuários.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (userRepository.findByUsername(username).isPresent()) {
                JOptionPane.showMessageDialog(view, "Este nome de usuário já existe.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            AuditService.getInstance().logAction("Criou o novo usuário", username);
            userRepository.addUser(new User(username, hashedPassword, role));
        }

        salvarEAtualizarTela("Usuário salvo com sucesso!");
    }

    private void excluir() {
        User selecionado = view.getUsuarioSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(view, "Selecione um usuário para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Tem certeza que deseja excluir o usuário '" + selecionado.getUsername() + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AuditService.getInstance().logAction("Excluiu o usuário", selecionado.getUsername());
            userRepository.removeUser(selecionado);
            salvarEAtualizarTela("Usuário excluído com sucesso!");
        }
    }

    private void salvarEAtualizarTela(String mensagem) {
        try {
            persistenceService.salvarDados();
            view.atualizarLista(userRepository.getAllUsers());
            JOptionPane.showMessageDialog(view, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar os dados.", "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
        }
    }
}