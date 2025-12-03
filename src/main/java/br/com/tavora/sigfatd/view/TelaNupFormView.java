package br.com.tavora.sigfatd.view;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class TelaNupFormView extends JDialog {

    private JTextField campoNumeroFatd, campoNupCompleto;
    private JButton botaoSalvar, botaoCancelar;
    private boolean salvo = false;

    public TelaNupFormView(Dialog owner) {
        super(owner, "Inserir FATD e NUP Manualmente", true);
        setSize(400, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        add(criarPainelFormulario(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridLayout(2, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        campoNumeroFatd = new JTextField();
        campoNupCompleto = new JTextField();

        painel.add(new JLabel("Nº da FATD:"));
        painel.add(campoNumeroFatd);
        painel.add(new JLabel("NUP Correspondente:"));
        painel.add(campoNupCompleto);

        return painel;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton("Salvar");
        botaoCancelar = new JButton("Cancelar");

        botaoSalvar.addActionListener(e -> salvar());
        botaoCancelar.addActionListener(e -> dispose());

        painel.add(botaoSalvar);
        painel.add(botaoCancelar);
        return painel;
    }

    private void salvar() {
        if (campoNumeroFatd.getText().trim().isEmpty() || campoNupCompleto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ambos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Integer.parseInt(campoNumeroFatd.getText().trim());
            this.salvo = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O Nº da FATD deve ser um número.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Optional<Integer> getNumeroFatd() {
        return salvo ? Optional.of(Integer.parseInt(campoNumeroFatd.getText().trim())) : Optional.empty();
    }

    public Optional<String> getNupCompleto() {
        return salvo ? Optional.of(campoNupCompleto.getText().trim()) : Optional.empty();
    }
}