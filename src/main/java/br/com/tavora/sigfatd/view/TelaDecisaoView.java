package br.com.tavora.sigfatd.view;

import br.com.tavora.sigfatd.model.Autoridade;
import br.com.tavora.sigfatd.model.FATD;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelaDecisaoView extends JDialog {

    private JTextPane textPane;
    private JComboBox<Autoridade> comboAutoridade;
    private JButton botaoSalvar, botaoGerarDocumento, botaoFechar;
    private JTabbedPane painelTextosProntos;
    private Map<String, JComboBox<String>> combosDinamicos = new HashMap<>();

    public TelaDecisaoView(Frame owner, FATD fatd) {
        super(owner, String.format("Decisão de Punição - FATD %d/%d", fatd.getNup(), fatd.getDataProcesso().getYear()), true);
        setSize(1280, 900);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- 1. PAINEL CENTRAL (PAPEL) ---
        // Fundo cinza escuro para destacar o papel branco
        JPanel painelFundo = new JPanel(new GridBagLayout());
        painelFundo.setBackground(new Color(220, 220, 220));

        textPane = new JTextPane();
        // Tamanho fixo simulando A4 (aprox 595x842 pts, ajustado para tela)
        textPane.setPreferredSize(new Dimension(750, 1050));
        textPane.setBackground(Color.WHITE);
        // Margens internas do papel (Top, Left, Bottom, Right)
        textPane.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1), // Borda fina
                new EmptyBorder(60, 70, 60, 50)      // Margens ABNT/Militar
        ));
        textPane.setEditable(true);

        painelFundo.add(textPane); // Adiciona o papel centralizado no fundo cinza

        JScrollPane scrollDoc = new JScrollPane(painelFundo);
        scrollDoc.getVerticalScrollBar().setUnitIncrement(20);
        scrollDoc.setBorder(null);

        // --- 2. PAINEL LATERAL (TEXTOS) ---
        painelTextosProntos = new JTabbedPane();
        painelTextosProntos.setBorder(BorderFactory.createTitledBorder("Textos Auxiliares (Duplo Clique)"));
        painelTextosProntos.setPreferredSize(new Dimension(320, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollDoc, painelTextosProntos);
        splitPane.setResizeWeight(0.85); // Dá prioridade ao documento
        splitPane.setDividerSize(5);
        add(splitPane, BorderLayout.CENTER);

        // --- 3. BARRA INFERIOR ---
        JPanel painelInferior = new JPanel(new BorderLayout(15, 15));
        painelInferior.setBorder(new EmptyBorder(10, 15, 10, 15));
        painelInferior.setBackground(new Color(245, 245, 245));

        JPanel painelAutoridade = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAutoridade.setOpaque(false);
        painelAutoridade.add(new JLabel("Autoridade Signatária:"));
        comboAutoridade = new JComboBox<>();
        comboAutoridade.setPreferredSize(new Dimension(350, 25));
        painelAutoridade.add(comboAutoridade);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setOpaque(false);
        botaoSalvar = new JButton("Salvar Rascunho");
        botaoGerarDocumento = new JButton("Gerar Documento Final (PDF/Word)");
        botaoGerarDocumento.setBackground(new Color(40, 70, 120));
        botaoGerarDocumento.setForeground(Color.WHITE);
        botaoGerarDocumento.setFocusPainted(false);
        botaoFechar = new JButton("Fechar");

        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoGerarDocumento);
        painelBotoes.add(botaoFechar);

        painelInferior.add(painelAutoridade, BorderLayout.WEST);
        painelInferior.add(painelBotoes, BorderLayout.EAST);
        add(painelInferior, BorderLayout.SOUTH);

        // CONFIGURA OS ATALHOS (ESC e CTRL+ENTER)
        configurarAtalhosTeclado();
    }

    // --- MÉTODOS DE RENDERIZAÇÃO ---

    public void renderizarDocumentoInterativo(String templateTexto, String cabecalhoInfo, Map<String, List<String>> opcoesParaTags) {
        textPane.setText("");
        combosDinamicos.clear();
        StyledDocument doc = textPane.getStyledDocument();

        // Estilos
        SimpleAttributeSet estiloCorpo = new SimpleAttributeSet();
        StyleConstants.setFontFamily(estiloCorpo, "Times New Roman");
        StyleConstants.setFontSize(estiloCorpo, 12);
        StyleConstants.setAlignment(estiloCorpo, StyleConstants.ALIGN_JUSTIFIED);
        StyleConstants.setFirstLineIndent(estiloCorpo, 30f);
        StyleConstants.setSpaceAbove(estiloCorpo, 4f);

        SimpleAttributeSet estiloTitulo = new SimpleAttributeSet();
        StyleConstants.setFontFamily(estiloTitulo, "Times New Roman");
        StyleConstants.setFontSize(estiloTitulo, 12);
        StyleConstants.setBold(estiloTitulo, true);
        StyleConstants.setUnderline(estiloTitulo, true);
        StyleConstants.setAlignment(estiloTitulo, StyleConstants.ALIGN_CENTER);

        SimpleAttributeSet estiloCabecalho = new SimpleAttributeSet();
        StyleConstants.setFontFamily(estiloCabecalho, "Times New Roman");
        StyleConstants.setFontSize(estiloCabecalho, 10);
        StyleConstants.setItalic(estiloCabecalho, true);
        StyleConstants.setAlignment(estiloCabecalho, StyleConstants.ALIGN_LEFT);

        // Estilo para o bloco "Ciente" (sem recuo)
        SimpleAttributeSet estiloCiente = new SimpleAttributeSet();
        StyleConstants.setFontFamily(estiloCiente, "Times New Roman");
        StyleConstants.setFontSize(estiloCiente, 12);
        StyleConstants.setAlignment(estiloCiente, StyleConstants.ALIGN_LEFT);
        StyleConstants.setFirstLineIndent(estiloCiente, 0f);

        try {
            // Cabeçalho
            if (cabecalhoInfo != null) {
                doc.insertString(doc.getLength(), cabecalhoInfo + "\n\n", estiloCabecalho);
                doc.setParagraphAttributes(0, cabecalhoInfo.length(), estiloCabecalho, false);
            }

            // Título
            int inicioTitulo = doc.getLength();
            String tituloTexto = "DECISÃO DA AUTORIDADE\n\n";
            doc.insertString(doc.getLength(), tituloTexto, estiloTitulo);
            doc.setParagraphAttributes(inicioTitulo, tituloTexto.length(), estiloTitulo, false);

            // Corpo
            Pattern pattern = Pattern.compile("(\\$\\{.*?\\})");
            Matcher matcher = pattern.matcher(templateTexto);
            int lastIndex = 0;

            while (matcher.find()) {
                String textoAntes = templateTexto.substring(lastIndex, matcher.start());

                // Detecta se estamos na parte do "Ciente" para tirar o recuo
                boolean isCiente = textoAntes.contains("Ciente em") || textoAntes.contains("____/____/____");
                AttributeSet estiloAtual = isCiente ? estiloCiente : estiloCorpo;

                int pos = doc.getLength();
                doc.insertString(doc.getLength(), textoAntes, estiloAtual);
                if (textoAntes.contains("\n")) {
                    doc.setParagraphAttributes(pos, textoAntes.length(), estiloAtual, false);
                }

                String tag = matcher.group(1);

                if (opcoesParaTags.containsKey(tag)) {
                    // Passa a TAG para definir o tamanho correto
                    JComboBox<String> combo = criarComboEstiloDocumento(opcoesParaTags.get(tag), tag);
                    textPane.insertComponent(combo);
                    doc.insertString(doc.getLength(), " ", estiloAtual);
                    combosDinamicos.put(tag, combo);
                } else {
                    SimpleAttributeSet red = new SimpleAttributeSet(estiloAtual);
                    StyleConstants.setForeground(red, Color.RED);
                    doc.insertString(doc.getLength(), tag + " ", red);
                }
                lastIndex = matcher.end();
            }

            if (lastIndex < templateTexto.length()) {
                String resto = templateTexto.substring(lastIndex);
                boolean isCiente = resto.contains("Ciente em");
                AttributeSet estiloAtual = isCiente ? estiloCiente : estiloCorpo;

                doc.insertString(doc.getLength(), resto, estiloAtual);
                doc.setParagraphAttributes(doc.getLength() - resto.length(), resto.length(), estiloAtual, false);
            }

        } catch (BadLocationException e) { e.printStackTrace(); }
    }

    /**
     * Cria Combo com tamanho ajustado dependendo da TAG.
     */
    private JComboBox<String> criarComboEstiloDocumento(List<String> opcoes, String tag) {
        JComboBox<String> combo = new JComboBox<>(new Vector<>(opcoes));
        combo.setEditable(true);
        combo.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        combo.setBackground(new Color(250, 250, 250));
        combo.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));

        // --- LÓGICA DE TAMANHO ---
        int largura = 150; // Padrão

        if (tag.contains("Dias") || tag.contains("dias")) {
            largura = 50;  // Pequeno
        } else if (tag.contains("artigo") || tag.contains("Art")) {
            largura = 80;
        } else if (tag.contains("punicao")) {
            largura = 130;
        } else if (tag.contains("enquadramento")) {
            largura = 400; // Largo para o texto da transgressão
        } else if (tag.contains("julgamento")) {
            largura = 480;
        }

        combo.setPreferredSize(new Dimension(largura, 22));
        combo.setMaximumSize(new Dimension(largura, 22));

        return combo;
    }

    public void inserirTextoNoCursor(String texto) {
        try {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attrs, "Times New Roman");
            StyleConstants.setFontSize(attrs, 12);
            doc.insertString(textPane.getCaretPosition(), " " + texto, attrs);
            textPane.requestFocusInWindow();
        } catch (BadLocationException e) { e.printStackTrace(); }
    }

    // --- MÉTODOS AUXILIARES ---
    @SuppressWarnings("unchecked")
    public void popularPaineisLaterais(Map<String, Object> conteudos, java.util.function.Consumer<String> acaoClique) {
        painelTextosProntos.removeAll();
        for (Map.Entry<String, Object> entry : conteudos.entrySet()) {
            String titulo = entry.getKey();
            Object valor = entry.getValue();

            if (valor instanceof List) {
                JList<String> lista = criarLista((List<String>) valor, acaoClique);
                painelTextosProntos.addTab(titulo, new JScrollPane(lista));
            } else if (valor instanceof Map) {
                JTabbedPane subTab = new JTabbedPane();
                Map<String, List<String>> subMap = (Map<String, List<String>>) valor;
                for (Map.Entry<String, List<String>> sub : subMap.entrySet()) {
                    JList<String> lista = criarLista(sub.getValue(), acaoClique);
                    subTab.addTab(sub.getKey(), new JScrollPane(lista));
                }
                painelTextosProntos.addTab(titulo, subTab);
            }
        }
    }

    private JList<String> criarLista(List<String> itens, java.util.function.Consumer<String> acao) {
        JList<String> lista = new JList<>(new Vector<>(itens));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lista.setFixedCellHeight(25);
        lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String val = lista.getSelectedValue();
                    if (val != null) acao.accept(val);
                }
            }
        });
        return lista;
    }

    private void configurarAtalhosTeclado() {
        JRootPane rootPane = getRootPane();

        KeyStroke escKey = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escKey, "FECHAR");
        rootPane.getActionMap().put("FECHAR", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                botaoFechar.doClick();
            }
        });

        KeyStroke ctrlEnterKey = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_DOWN_MASK);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlEnterKey, "FINALIZAR");
        rootPane.getActionMap().put("FINALIZAR", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                botaoGerarDocumento.doClick();
            }
        });
    }

    public Map<String, JComboBox<String>> getCombosDinamicos() { return combosDinamicos; }
    public void popularAutoridades(List<Autoridade> lista) { comboAutoridade.setModel(new DefaultComboBoxModel<>(new Vector<>(lista))); }
    public Autoridade getAutoridadeSelecionada() { return (Autoridade) comboAutoridade.getSelectedItem(); }
    public void adicionarAcaoSalvar(ActionListener l) { botaoSalvar.addActionListener(l); }
    public void adicionarAcaoGerar(ActionListener l) { botaoGerarDocumento.addActionListener(l); }
    public void adicionarAcaoFechar(ActionListener l) { botaoFechar.addActionListener(l); }
    public void fechar() { dispose(); }
}