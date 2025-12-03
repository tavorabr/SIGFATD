package br.com.tavora.sigfatd.controller;

import br.com.tavora.sigfatd.model.User;
import br.com.tavora.sigfatd.service.AuditService;
import org.mindrot.jbcrypt.BCrypt;
import br.com.tavora.sigfatd.service.UserRepository;
import br.com.tavora.sigfatd.view.TelaLoginView;
import java.util.Optional;

public class LoginController {
    private final TelaLoginView view;
    private final UserRepository userRepository;
    private User usuarioLogado = null;

    public LoginController(TelaLoginView view, UserRepository userRepository) {
        this.view = view;
        this.userRepository = userRepository;
        this.view.adicionarAcaoLogin(e -> tentarLogin());
    }

    private void tentarLogin() {
        String username = view.getUsername();
        char[] password = view.getPassword();

        if (username.isEmpty() || password.length == 0) {
            view.exibirMensagemErro("Usuário e senha são obrigatórios.");
            return;
        }

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (BCrypt.checkpw(new String(password), user.getHashedPassword())) {
                this.usuarioLogado = user;
                AuditService.getInstance().logAction("Login bem-sucedido.");
                view.fechar();
            } else {
                view.exibirMensagemErro("Senha incorreta.");
            }
        } else {
            view.exibirMensagemErro("Usuário não encontrado.");
        }
    }

    public Optional<User> getUsuarioLogado() {
        return Optional.ofNullable(usuarioLogado);
    }
}