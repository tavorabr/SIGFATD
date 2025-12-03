package br.com.tavora.sigfatd.controller;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import br.com.tavora.sigfatd.service.PersistenceService;
import br.com.tavora.sigfatd.service.SessionManager;
import br.com.tavora.sigfatd.service.SettingsService;
import br.com.tavora.sigfatd.service.UserRepository;
import br.com.tavora.sigfatd.view.TelaLoginView;
import br.com.tavora.sigfatd.view.TelaMenuView;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) {

        try {
            PersistenceService.getInstance().carregarDados();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Falha crítica ao carregar dados. O programa será fechado.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fontFamily = "Roboto";
        try {
            InputStream is = Main.class.getResourceAsStream("/fonts/Roboto-Regular.ttf");
            if (is == null) {
                throw new Exception("Não foi possível encontrar a fonte: /fonts/Roboto-Regular.ttf");
            }
            Font roboto = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(roboto);

        } catch (Exception e) {
            System.err.println("Falha ao carregar e registrar a fonte Roboto. Usando fonte padrão do sistema.");
            fontFamily = UIManager.getSystemLookAndFeelClassName();
            e.printStackTrace();
        }

        FlatLaf.setPreferredFontFamily(fontFamily);

        int zoom = SettingsService.getInstance().getSettings().getFontSizeModifier();
        int newBaseSize = 12 + zoom;

        UIManager.put("defaultFont", new Font(fontFamily, Font.PLAIN, newBaseSize));

        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            iniciarAplicacao();
        });
    }

    public static void iniciarAplicacao() {
        TelaLoginView loginView = new TelaLoginView(null);

        UserRepository userRepository = UserRepository.getInstance();
        LoginController loginController = new LoginController(loginView, userRepository);

        loginView.setVisible(true);

        loginController.getUsuarioLogado().ifPresent(usuario -> {
            SessionManager.getInstance().setCurrentUser(usuario);
            TelaMenuView menuView = new TelaMenuView();
            new MenuController(menuView);
            menuView.setVisible(true);
        });
    }
}