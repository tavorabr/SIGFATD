package br.com.tavora.sigfatd.service;

import br.com.tavora.sigfatd.model.Role;
import br.com.tavora.sigfatd.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private static UserRepository instance;
    private final List<User> users;

    private UserRepository() {
        this.users = new ArrayList<>();
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public void loadUsers(List<User> loadedUsers) {
        this.users.clear();
        if (loadedUsers != null) {
            this.users.addAll(loadedUsers);
        }
        if (this.users.isEmpty()) {
            createDefaultMasterUser();
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public void addUser(User user) {
        if (findByUsername(user.getUsername()).isEmpty()) {
            users.add(user);
        }
    }

    public void updateUser(String oldUsername, User updatedUser) {
        findByUsername(oldUsername).ifPresent(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setRole(updatedUser.getRole());
            if (updatedUser.getHashedPassword() != null && !updatedUser.getHashedPassword().isEmpty()) {
                user.setHashedPassword(updatedUser.getHashedPassword());
            }
        });
    }

    public void removeUser(User user) {
        if (user.getRole() == Role.MASTER && users.stream().filter(u -> u.getRole() == Role.MASTER).count() <= 1) {
            System.err.println("Não é possível excluir o último usuário MASTER.");
            return;
        }
        users.removeIf(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
    }

    private void createDefaultMasterUser() {
        System.out.println("Nenhum usuário encontrado. Criando usuário 'master' padrão.");
        String username = "master";
        String password = "123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User masterUser = new User(username, hashedPassword, Role.MASTER);
        this.users.add(masterUser);
        System.out.println("Usuário 'master' criado com a senha '123'. É altamente recomendável alterá-la.");
    }
}