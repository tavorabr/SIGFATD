package br.com.tavora.sigfatd.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Vector;

public class TelaAjudaView extends JDialog {

    private final JList<String> listaTopicos;
    private final DefaultListModel<String> listModel;
    private final JEditorPane areaTextoAjuda;

    public TelaAjudaView(Frame owner) {
        super(owner, "Ajuda do SIGFATD", true);
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        listaTopicos = new JList<>(listModel);
        listaTopicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTopicos.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollLista = new JScrollPane(listaTopicos);
        scrollLista.setMinimumSize(new Dimension(200, 0));

        areaTextoAjuda = new JEditorPane();
        areaTextoAjuda.setContentType("text/html");
        areaTextoAjuda.setEditable(false);
        areaTextoAjuda.setBorder(new EmptyBorder(15, 15, 15, 15));
        JScrollPane scrollTexto = new JScrollPane(areaTextoAjuda);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollLista, scrollTexto);
        splitPane.setDividerLocation(220);

        add(splitPane, BorderLayout.CENTER);
    }

    public void popularTopicos(Vector<String> topicos) {
        listModel.clear();
        listModel.addAll(topicos);
    }

    public void setTextoAjuda(String textoHtml) {
        areaTextoAjuda.setText(textoHtml);
        areaTextoAjuda.setCaretPosition(0);
    }

    public void adicionarAcaoSelecaoTopico(ListSelectionListener listener) {
        listaTopicos.getSelectionModel().addListSelectionListener(listener);
    }

    public String getTopicoSelecionado() {
        return listaTopicos.getSelectedValue();
    }
}