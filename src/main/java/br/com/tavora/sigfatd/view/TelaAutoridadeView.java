package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.Autoridade;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class TelaAutoridadeView extends JDialog {

    private final JList<Autoridade> listaAutoridades;
    private final DefaultListModel<Autoridade> listModel;
    private final JTextField campoPostoGraduacao, campoNomeCompleto, campoTitulacao;
    private final JButton botaoSalvar, botaoNovo, botaoExcluir, botaoFechar;

    public TelaAutoridadeView(Dialog owner) {
        super(owner, "Gerenciar Autoridades Competentes", true);
        setSize(800, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        listModel = new DefaultListModel<>();
        listaAutoridades = new JList<>(listModel);
        JScrollPane scrollLista = new JScrollPane(listaAutoridades);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Autoridades Salvas"));

        JPanel painelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados da Autoridade"));
        campoPostoGraduacao = new JTextField();
        campoNomeCompleto = new JTextField();
        campoTitulacao = new JTextField();
        painelFormulario.add(new JLabel("Posto/Graduação:"));
        painelFormulario.add(campoPostoGraduacao);
        painelFormulario.add(new JLabel("Nome Completo:"));
        painelFormulario.add(campoNomeCompleto);
        painelFormulario.add(new JLabel("Titulação (Ex: Cmt Cia):"));
        painelFormulario.add(campoTitulacao);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoNovo = new JButton("Novo");
        botaoSalvar = new JButton("Salvar");
        botaoExcluir = new JButton("Excluir");
        botaoFechar = new JButton("Fechar");
        painelBotoes.add(botaoNovo);
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoFechar);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLista, painelFormulario);
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public void atualizarLista(List<Autoridade> autoridades) {
        listModel.clear();
        listModel.addAll(autoridades);
    }

    public void setDadosFormulario(Autoridade a) {
        if (a != null) {
            campoPostoGraduacao.setText(a.getPostoGraduacao());
            campoNomeCompleto.setText(a.getNomeCompleto());
            campoTitulacao.setText(a.getTitulacao());
        } else {
            campoPostoGraduacao.setText("");
            campoNomeCompleto.setText("");
            campoTitulacao.setText("");
        }
    }

    public Autoridade getDadosFormulario() {
        Autoridade a = new Autoridade();
        a.setPostoGraduacao(campoPostoGraduacao.getText().trim());
        a.setNomeCompleto(campoNomeCompleto.getText().trim());
        a.setTitulacao(campoTitulacao.getText().trim());
        return a;
    }

    public Autoridade getAutoridadeSelecionada() {
        return listaAutoridades.getSelectedValue();
    }

    public void limparSelecaoLista() {
        listaAutoridades.clearSelection();
    }

    public void adicionarAcaoSelecaoLista(ListSelectionListener listener) {
        listaAutoridades.getSelectionModel().addListSelectionListener(listener);
    }

    public void adicionarAcaoSalvar(ActionListener listener) { botaoSalvar.addActionListener(listener); }
    public void adicionarAcaoNovo(ActionListener listener) { botaoNovo.addActionListener(listener); }
    public void adicionarAcaoExcluir(ActionListener listener) { botaoExcluir.addActionListener(listener); }
    public void adicionarAcaoFechar(ActionListener listener) { botaoFechar.addActionListener(listener); }
    public void fechar() { dispose(); }
}