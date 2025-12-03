package br.com.tavora.sigfatd.service;

import br.com.tavora.sigfatd.model.FATD;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArquivoService {
    private static ArquivoService instance;
    private final String diretorioRaiz;

    private ArquivoService() {
        File documentos = FileSystemView.getFileSystemView().getDefaultDirectory();
        this.diretorioRaiz = documentos.getAbsolutePath() + File.separator + "SIGFATD_Arquivos";
        new File(diretorioRaiz).mkdirs();
    }

    public static synchronized ArquivoService getInstance() {
        if (instance == null) {
            instance = new ArquivoService();
        }
        return instance;
    }

    public String getPastaDoFatd(FATD fatd) {
        String pastaFatd = diretorioRaiz + File.separator + fatd.getNup();
        new File(pastaFatd).mkdirs();
        return pastaFatd;
    }

    public String getCaminhoParaSalvarFatd(FATD fatd) {
        String nomeArquivo = String.format("FATD_%d_%s.docx", fatd.getNup(), fatd.getMilitar().getNomeGuerra());
        return getPastaDoFatd(fatd) + File.separator + nomeArquivo;
    }

    public String getCaminhoParaSalvarDecisao(FATD fatd) {
        String nomeArquivo = String.format("Decisao_FATD_%d_%s.docx", fatd.getNup(), fatd.getMilitar().getNomeGuerra());
        return getPastaDoFatd(fatd) + File.separator + nomeArquivo;
    }

    public List<File> listarArquivos(FATD fatd) {
        File pastaDoFatd = new File(getPastaDoFatd(fatd));
        if (pastaDoFatd.exists() && pastaDoFatd.isDirectory()) {
            File[] arquivos = pastaDoFatd.listFiles();
            if (arquivos != null) {
                return Arrays.asList(arquivos);
            }
        }
        return Collections.emptyList();
    }

    public void abrirArquivo(File arquivo) throws IOException {
        if (Desktop.isDesktopSupported() && arquivo.exists()) {
            Desktop.getDesktop().open(arquivo);
        } else {
            throw new IOException("Não foi possível abrir o arquivo.");
        }
    }

    public void excluirAnexo(File arquivoParaExcluir) throws IOException {
        if (arquivoParaExcluir == null || !arquivoParaExcluir.exists()) {
            throw new IOException("O arquivo não existe ou é inválido.");
        }

        if (!arquivoParaExcluir.delete()) {
            throw new IOException("Não foi possível excluir o arquivo. Verifique as permissões.");
        }
    }

    public Optional<File> anexarArquivo(FATD fatd, Component parent) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o arquivo para anexar");
        fileChooser.setMultiSelectionEnabled(false);

        int resultado = fileChooser.showOpenDialog(parent);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivoSelecionado = fileChooser.getSelectedFile();
            File pastaDestino = new File(getPastaDoFatd(fatd));
            File arquivoDestino = new File(pastaDestino, arquivoSelecionado.getName());

            Files.copy(arquivoSelecionado.toPath(), arquivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return Optional.of(arquivoDestino);
        }
        return Optional.empty();
    }

    public void excluirPastaFatd(FATD fatd) throws IOException {
        File pastaDoFatd = new File(getPastaDoFatd(fatd));
        if (pastaDoFatd.exists() && pastaDoFatd.isDirectory()) {
            File[] arquivos = pastaDoFatd.listFiles();
            if (arquivos != null) {
                for (File f : arquivos) {
                    if (!f.delete()) {
                        throw new IOException("Falha ao apagar o arquivo interno: " + f.getName());
                    }
                }
            }
            if (!pastaDoFatd.delete()) {
                throw new IOException("Falha ao apagar a pasta principal. Verifique se não há arquivos abertos.");
            }
        }
    }
}