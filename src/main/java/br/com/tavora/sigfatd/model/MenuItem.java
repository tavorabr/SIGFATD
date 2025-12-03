package br.com.tavora.sigfatd.model;

public class MenuItem {
    private final String titulo;
    private final String iconPath;
    private final Runnable acao;

    public MenuItem(String titulo, String iconPath, Runnable acao) {
        this.titulo = titulo;
        this.iconPath = iconPath;
        this.acao = acao;
    }

    public String getTitulo() { return titulo; }
    public String getIconPath() { return iconPath; }
    public void executarAcao() {
        if (acao != null) {
            acao.run();
        }
    }

    @Override
    public String toString() {
        return titulo;
    }
}