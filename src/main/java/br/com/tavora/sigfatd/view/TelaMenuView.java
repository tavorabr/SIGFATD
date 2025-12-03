package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.MenuItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TelaMenuView extends JFrame {

    private JPanel cardsPanel;
    private JLabel lblUsuarioLogado;

    public TelaMenuView() {
        super("SIGFATD - Sistema de Gestão de FATD");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);

        Color headerBgColor = new Color(75, 105, 79);
        Color mainBgColor = new Color(245, 247, 250);
        Color titleColor = Color.WHITE;

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(mainBgColor);
        setContentPane(contentPane);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerBgColor);
        headerPanel.setBorder(new EmptyBorder(15, 15, 10, 15));

        JLabel lblTitulo = new JLabel("SIGFATD");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(titleColor);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        lblUsuarioLogado = new JLabel("Usuário: ");
        lblUsuarioLogado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuarioLogado.setForeground(titleColor);

        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        headerPanel.add(lblUsuarioLogado, BorderLayout.SOUTH);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(mainBgColor);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        cardsPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        cardsPanel.setBackground(mainBgColor);
        cardsPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        centerPanel.add(cardsPanel, new GridBagConstraints());
    }

    public void setUsuarioLogado(String nomeUsuario) {
        if (nomeUsuario != null) {
            lblUsuarioLogado.setText("Usuário: " + nomeUsuario);
        } else {
            lblUsuarioLogado.setText("Usuário: N/A");
        }
    }

    public void setMenuItems(List<MenuItem> items) {
        cardsPanel.removeAll();

        Color cardBgColor = Color.WHITE;
        Color cardBorderColor = new Color(220, 220, 220);
        Color itemTextColor = new Color(51, 51, 51);

        for (MenuItem item : items) {
            JButton cardButton = new JButton();
            cardButton.setLayout(new BorderLayout(0, 10));
            cardButton.setBackground(cardBgColor);
            cardButton.setForeground(itemTextColor);
            cardButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(cardBorderColor, 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            cardButton.setFocusPainted(false);
            cardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (item.getIconPath() != null && !item.getIconPath().isEmpty()) {
                try {
                    ImageIcon originalIcon = new ImageIcon(getClass().getClassLoader().getResource(item.getIconPath()));
                    Image scaledImage = originalIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                    JLabel iconLabel = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);
                    cardButton.add(iconLabel, BorderLayout.CENTER);
                } catch (Exception e) {
                    cardButton.add(new JLabel("?", SwingConstants.CENTER), BorderLayout.CENTER);
                    System.err.println("Erro ao carregar ícone: " + item.getIconPath());
                }
            }

            JLabel textLabel = new JLabel(item.getTitulo(), SwingConstants.CENTER);
            textLabel.setForeground(itemTextColor);
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            cardButton.add(textLabel, BorderLayout.SOUTH);

            cardButton.addActionListener(e -> item.executarAcao());

            cardsPanel.add(cardButton);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
}