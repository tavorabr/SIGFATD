package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.FATD;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class TelaArquivoView extends JDialog {

    private final JList<FATD> listaFatds;
    private final DefaultListModel<FATD> fatdListModel;
    private final JList<File> listaArquivos;
    private final DefaultListModel<File> fileListModel;
    private final JButton botaoAbrirArquivo;
    private final JButton botaoAnexarArquivo;
    private final JButton botaoExcluirFatd;

    public TelaArquivoView(Frame owner) {
        super(owner, "Arquivo de FATDs", true);
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        fatdListModel = new DefaultListModel<>();
        listaFatds = new JList<>(fatdListModel);
        JScrollPane scrollFatds = new JScrollPane(listaFatds);
        scrollFatds.setBorder(BorderFactory.createTitledBorder("FATDs Gerados"));

        JPanel painelDireito = new JPanel(new BorderLayout(5, 5));
        painelDireito.setBorder(BorderFactory.createTitledBorder("Documentos do FATD Selecionado"));

        fileListModel = new DefaultListModel<>();
        listaArquivos = new JList<>(fileListModel);
        listaArquivos.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof File) {
                    setText(((File) value).getName());
                }
                return c;
            }
        });
        painelDireito.add(new JScrollPane(listaArquivos), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoAbrirArquivo = new JButton("Abrir Arquivo");
        botaoAnexarArquivo = new JButton("Adicionar Anexo...");
        botaoExcluirFatd = new JButton("Excluir FATD");

        painelBotoes.add(botaoAnexarArquivo);
        painelBotoes.add(botaoAbrirArquivo);
        painelBotoes.add(botaoExcluirFatd);
        painelDireito.add(painelBotoes, BorderLayout.SOUTH);

        botaoAbrirArquivo.setEnabled(false);
        botaoExcluirFatd.setEnabled(false);
        botaoExcluirFatd.setVisible(false);
        botaoAnexarArquivo.setVisible(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollFatds, painelDireito);
        splitPane.setDividerLocation(350);

        add(splitPane, BorderLayout.CENTER);

        listaArquivos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selecionado = listaArquivos.getSelectedValue() != null;
                botaoAbrirArquivo.setEnabled(selecionado);
            }
        });
    }

    public void popularListaFatds(List<FATD> fatds) {
        fatdListModel.clear();
        fatdListModel.addAll(fatds);
    }

    public void popularListaArquivos(List<File> arquivos) {
        fileListModel.clear();
        fileListModel.addAll(arquivos);
    }

    public FATD getFatdSelecionado() {
        return listaFatds.getSelectedValue();
    }

    public File getArquivoSelecionado() {
        return listaArquivos.getSelectedValue();
    }

    public void adicionarAcaoSelecaoFatd(ListSelectionListener listener) {
        listaFatds.getSelectionModel().addListSelectionListener(listener);
    }

    public void adicionarAcaoAbrirArquivo(ActionListener listener) {
        botaoAbrirArquivo.addActionListener(listener);
    }

    public void adicionarAcaoAnexarArquivo(ActionListener listener) {
        botaoAnexarArquivo.addActionListener(listener);
    }

    public void adicionarAcaoExcluirFatd(ActionListener listener) {
        botaoExcluirFatd.addActionListener(listener);
    }

    public void setExcluirFatdButtonVisible(boolean visible) {
        botaoExcluirFatd.setVisible(visible);
    }

    public void setAnexarArquivoButtonVisible(boolean visible) {
        botaoAnexarArquivo.setVisible(visible);
    }

    public void habilitarBotaoExcluir(boolean habilitar) {
        botaoExcluirFatd.setEnabled(habilitar);
    }
}