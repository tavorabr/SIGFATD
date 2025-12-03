package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.Militar;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.Optional;

public class TelaMilitarFormView extends JDialog {

    private JTextField campoNomeCompleto, campoNomeGuerra;
    private JComboBox<String> campoPostoGraduacao;
    private JFormattedTextField campoIdentidade;
    private JButton botaoSalvar, botaoCancelar;

    private Militar militar;
    private boolean salvo = false;

    public TelaMilitarFormView(Dialog owner, Militar militar) {
        super(owner, "Formulário de Militar", true);
        this.militar = militar;

        setLayout(new BorderLayout(10, 10));
        add(criarPainelFormulario(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
        popularFormulario();
        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridLayout(4, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        campoNomeCompleto = new JTextField(30);
        campoNomeGuerra = new JTextField();
        String[] opcoesPostoGrad = {"Selecione...", "3º Sgt", "Cb", "Sd EP", "Sd EV"};
        campoPostoGraduacao = new JComboBox<>(opcoesPostoGrad);
        try {
            MaskFormatter mascaraIdt = new MaskFormatter("###.###.###-#");
            mascaraIdt.setPlaceholderCharacter('_');
            campoIdentidade = new JFormattedTextField(mascaraIdt);
        } catch (ParseException e) {
            e.printStackTrace();
            campoIdentidade = new JFormattedTextField();
        }

        painel.add(new JLabel("Nome Completo:"));
        painel.add(campoNomeCompleto);
        painel.add(new JLabel("Nome de Guerra:"));
        painel.add(campoNomeGuerra);
        painel.add(new JLabel("Posto/Graduação:"));
        painel.add(campoPostoGraduacao);
        painel.add(new JLabel("Identidade Militar:"));
        painel.add(campoIdentidade);

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

    private void popularFormulario() {
        if (militar.getId() != 0) {
            campoNomeCompleto.setText(militar.getNomeCompleto());
            campoNomeGuerra.setText(militar.getNomeGuerra());
            campoPostoGraduacao.setSelectedItem(militar.getPostoGraduacao());
            campoIdentidade.setText(militar.getIdtMilitar());
        }
    }

    private void salvar() {
        System.out.println("\n--- DIAGNÓSTICO DO MÉTODO SALVAR ---");
        System.out.println("1. Botão 'Salvar' do formulário foi clicado.");

        // Coleta os dados dos campos para análise
        String nomeCompleto = campoNomeCompleto.getText();
        String nomeGuerra = campoNomeGuerra.getText();
        String identidade = campoIdentidade.getText();
        int postoIndex = campoPostoGraduacao.getSelectedIndex();
        Object postoItem = campoPostoGraduacao.getSelectedItem();

        System.out.println("2. Verificando os campos:");
        System.out.println("   - Nome Completo: '" + nomeCompleto + "' (Está vazio? " + nomeCompleto.trim().isEmpty() + ")");
        System.out.println("   - Nome de Guerra: '" + nomeGuerra + "' (Está vazio? " + nomeGuerra.trim().isEmpty() + ")");
        System.out.println("   - Identidade: '" + identidade + "' (É a máscara vazia? " + identidade.equals("   .   .   - ") + ")");
        System.out.println("   - Posto/Graduação: '" + postoItem + "' (Índice é 0? " + (postoIndex == 0) + ")");

        if (nomeCompleto.trim().isEmpty() ||
                nomeGuerra.trim().isEmpty() ||
                identidade.equals("   .   .   - ") ||
                postoIndex == 0) {

            System.out.println("3. ERRO: A validação falhou. Exibindo mensagem de erro e interrompendo o salvamento.");
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            System.out.println("--- FIM DO DIAGNÓSTICO ---");
            return;
        }

        System.out.println("3. SUCESSO: Validação passou.");

        militar.setNomeCompleto(nomeCompleto);
        militar.setNomeGuerra(nomeGuerra);
        militar.setPostoGraduacao((String) postoItem);
        militar.setIdtMilitar(identidade);

        System.out.println("4. Objeto Militar atualizado. Marcando como 'salvo'.");
        this.salvo = true;
        System.out.println("5. Fechando o formulário.");
        System.out.println("--- FIM DO DIAGNÓSTICO ---");
        dispose();
    }

    public Optional<Militar> getMilitar() {
        return salvo ? Optional.of(militar) : Optional.empty();
    }
}