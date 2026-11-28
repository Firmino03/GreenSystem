package br.edu.ifpe.reservas.servico;

import br.edu.ifpe.reservas.excecao.ConflitoDeHorarioException;
import br.edu.ifpe.reservas.excecao.RegraDeReservaException;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.ResultadoImportacao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ImportadorReservas {
    private final GerenciadorReservas gerenciador;

    public ImportadorReservas(GerenciadorReservas gerenciador) {
        this.gerenciador = gerenciador;
    }

    // "hoje" vem de fora, como no reservar, para dar para testar com um dia fixo
    public ResultadoImportacao importar(List<String> linhas, LocalDate hoje) {
        int importadas = 0;
        List<String> motivos = new ArrayList<>();

        for (String linha : linhas) {
            try {
                importarLinha(linha, hoje);
                importadas++;
            } catch (RegraDeReservaException | ConflitoDeHorarioException e) {
                // Aqui o erro não derruba nada: a linha é descartada e a importação segue
                motivos.add(linha + "  ->  " + e.getMessage());
            }
        }
        return new ResultadoImportacao(importadas, motivos.size(), motivos);
    }

    private void importarLinha(String linha, LocalDate hoje)
            throws RegraDeReservaException, ConflitoDeHorarioException {

        String[] campos = linha.split(";", -1);
        if (campos.length != 6) {
            throw new RegraDeReservaException("esperava 6 campos separados por ';', mas encontrei " + campos.length);
        }

        String codigoArea = campos[0].trim().toUpperCase();
        String solicitante = campos[1];
        LocalDate data = Reserva.converterData(campos[2]);
        LocalTime inicio = Reserva.converterHora(campos[3]);
        LocalTime fim = Reserva.converterHora(campos[4]);
        String finalidade = campos[5];

        // Passa pelas mesmas regras de uma reserva digitada no menu
        gerenciador.reservar(codigoArea, solicitante, data, inicio, fim, finalidade, hoje);
    }
}