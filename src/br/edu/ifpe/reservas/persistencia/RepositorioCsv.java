package br.edu.ifpe.reservas.persistencia;

import br.edu.ifpe.reservas.excecao.RegraDeReservaException;
import br.edu.ifpe.reservas.modelo.AreaDeCultivo;
import br.edu.ifpe.reservas.modelo.Estufa;
import br.edu.ifpe.reservas.modelo.Exportavel;
import br.edu.ifpe.reservas.modelo.Horta;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.Viveiro;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RepositorioCsv {
    private final Path pasta;
    private final Path arquivoAreas;
    private final Path arquivoReservas;

    public RepositorioCsv(Path pasta) {
        this.pasta = pasta;
        this.arquivoAreas = pasta.resolve("areas.csv");
        this.arquivoReservas = pasta.resolve("reservas.csv");
    }


    public void salvar(List<AreaDeCultivo> areas, List<Reserva> reservas) throws IOException {
        Files.createDirectories(pasta); // se a pasta já existe, não dá erro
        gravar(arquivoAreas, areas);
        gravar(arquivoReservas, reservas);
    }

    // Grava o arquivo em UTF-8, sobrescrevendo o que já existia.
    private void gravar(Path arquivo, List<? extends Exportavel> itens) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (Exportavel item : itens) {
            linhas.add(item.paraCsv());
        }
        Files.write(arquivo, linhas, StandardCharsets.UTF_8);
    }


    public List<AreaDeCultivo> carregarAreas() throws IOException, RegraDeReservaException {
        List<AreaDeCultivo> areas = new ArrayList<>();

        for (String linha : lerLinhas(arquivoAreas)) {
            String[] campos = linha.split(";", -1);
            if (campos.length != 4) {
                throw linhaInvalida(arquivoAreas, linha);
            }
            try {
                String codigo = campos[1];
                String nome = campos[2];
                int capacidade = Integer.parseInt(campos[3].trim());

                // O primeiro campo decide qual subclasse criar
                AreaDeCultivo area = switch (campos[0].trim()) {
                    case "Horta" -> new Horta(codigo, nome, capacidade);
                    case "Estufa" -> new Estufa(codigo, nome, capacidade);
                    case "Viveiro" -> new Viveiro(codigo, nome, capacidade);
                    default -> throw new RegraDeReservaException("Tipo de área desconhecido: " + campos[0]);
                };
                areas.add(area);
            } catch (IllegalArgumentException e) {
                // pega o número inválido e também o construtor recusando os dados
                throw linhaInvalida(arquivoAreas, linha);
            }
        }
        return areas;
    }

    // Recebe as áreas já carregadas, para ligar cada reserva à sua área pelo código
    public List<Reserva> carregarReservas(List<AreaDeCultivo> areas) throws IOException, RegraDeReservaException {
        HashMap<String, AreaDeCultivo> areasPorCodigo = new HashMap<>();
        for (AreaDeCultivo area : areas) {
            areasPorCodigo.put(area.getCodigo(), area);
        }

        List<Reserva> reservas = new ArrayList<>();

        for (String linha : lerLinhas(arquivoReservas)) {
            String[] campos = linha.split(";", -1);
            if (campos.length != 7) {
                throw linhaInvalida(arquivoReservas, linha);
            }
            try {
                int numero = Integer.parseInt(campos[0].trim());
                AreaDeCultivo area = areasPorCodigo.get(campos[1].trim());
                if (area == null) {
                    throw new RegraDeReservaException("Reserva #" + numero + " usa uma área que não existe: " + campos[1]);
                }
                LocalDate data = LocalDate.parse(campos[3].trim());   // formato ISO: 2026-11-12
                LocalTime inicio = LocalTime.parse(campos[4].trim()); // 14:00
                LocalTime fim = LocalTime.parse(campos[5].trim());

                reservas.add(new Reserva(numero, area, campos[2], data, inicio, fim, campos[6]));
            } catch (NumberFormatException | DateTimeParseException e) {
                throw linhaInvalida(arquivoReservas, linha);
            }
        }
        return reservas;
    }

    // Lê o arquivo em UTF-8, ignorando linhas em branco.
    // Se o arquivo não existe (primeira execução), devolve lista vazia: não é erro.
    private List<String> lerLinhas(Path arquivo) throws IOException {
        List<String> linhas = new ArrayList<>();
        if (!Files.exists(arquivo)) {
            return linhas;
        }
        // try-with-resources: significa que o leitor será fechado automaticamente no final do bloco, mesmo se der erro.
        try (BufferedReader leitor = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (!linha.isBlank()) {
                    linhas.add(linha);
                }
            }
        }
        return linhas;
    }

    private RegraDeReservaException linhaInvalida(Path arquivo, String linha) {
        return new RegraDeReservaException("Linha inválida em " + arquivo + ": " + linha);
    }
}