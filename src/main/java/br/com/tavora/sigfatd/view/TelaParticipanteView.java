package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.Participante;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class TelaParticipanteView extends JDialog {

    private final JList<Participante> listaParticipantes;
    private final DefaultListModel<Participante> listModel;
    private final JTextField campoPostoGraduacao, campoNomeCompleto, campoIdtMil;
    private final JButton botaoSalvar, botaoNovo, botaoExcluir, botaoFechar;

    public TelaParticipanteView(Dialog owner) {
        super(owner, "Gerenciar Militares Participantes", true);
        setSize(800, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        listModel = new DefaultListModel<>();
        listaParticipantes = new JList<>(listModel);
        JScrollPane scrollLista = new JScrollPane(listaParticipantes);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Participantes Salvos"));

        JPanel painelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados do Participante"));
        campoPostoGraduacao = new JTextField();
        campoNomeCompleto = new JTextField();
        campoIdtMil = new JTextField();
        painelFormulario.add(new JLabel("Posto/Graduação:"));
        painelFormulario.add(campoPostoGraduacao);
        painelFormulario.add(new JLabel("Nome Completo:"));
        painelFormulario.add(campoNomeCompleto);
        painelFormulario.add(new JLabel("IDT Militar:"));
        painelFormulario.add(campoIdtMil);

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

    public void atualizarLista(List<Participante> participantes) {
        listModel.clear();
        listModel.addAll(participantes);
    }

    public void setDadosFormulario(Participante p) {
        if (p != null) {
            campoPostoGraduacao.setText(p.getPostoGraduacao());
            campoNomeCompleto.setText(p.getNomeCompleto());
            campoIdtMil.setText(p.getIdtMil());
        } else {
            campoPostoGraduacao.setText("");
            campoNomeCompleto.setText("");
            campoIdtMil.setText("");
        }
    }

    public Participante getDadosFormulario() {
        Participante p = new Participante();
        p.setPostoGraduacao(campoPostoGraduacao.getText().trim());
        p.setNomeCompleto(campoNomeCompleto.getText().trim());
        p.setIdtMil(campoIdtMil.getText().trim());
        return p;
    }

    public Participante getParticipanteSelecionado() {
        return listaParticipantes.getSelectedValue();
    }

    public void limparSelecaoLista() {
        listaParticipantes.clearSelection();
    }

    public void adicionarAcaoSelecaoLista(ListSelectionListener listener) {
        listaParticipantes.getSelectionModel().addListSelectionListener(listener);
    }

    public void adicionarAcaoSalvar(ActionListener listener) { botaoSalvar.addActionListener(listener); }
    public void adicionarAcaoNovo(ActionListener listener) { botaoNovo.addActionListener(listener); }
    public void adicionarAcaoExcluir(ActionListener listener) { botaoExcluir.addActionListener(listener); }
    public void adicionarAcaoFechar(ActionListener listener) { botaoFechar.addActionListener(listener); }

    public void fechar() { dispose(); }
}