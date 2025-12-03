package br.com.tavora.sigfatd.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class PrazoService {

    private FeriadoRepository feriadoRepository;

    public PrazoService() {
        this.feriadoRepository = FeriadoRepository.getInstance();
    }


    public LocalDate adicionarDiasUteis(LocalDate dataInicial, int diasUteisParaAdicionar) {
        LocalDate dataResultado = dataInicial;
        int diasAdicionados = 0;

        while (diasAdicionados < diasUteisParaAdicionar) {
            dataResultado = dataResultado.plusDays(1);

            if (isDiaUtil(dataResultado)) {
                diasAdicionados++;
            }
        }
        return dataResultado;
    }

    private boolean isDiaUtil(LocalDate data) {
        boolean isFimDeSemana = data.getDayOfWeek() == DayOfWeek.SATURDAY ||
                data.getDayOfWeek() == DayOfWeek.SUNDAY;

        return !isFimDeSemana && !feriadoRepository.isFeriado(data);
    }
}