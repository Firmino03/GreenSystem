package br.edu.ifpe.reservas.app;

import br.edu.ifpe.reservas.modelo.AreaDeCultivo;
import br.edu.ifpe.reservas.modelo.Horta;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.Viveiro;
import br.edu.ifpe.reservas.persistencia.RepositorioCsv;
import br.edu.ifpe.reservas.servico.GerenciadorReservas;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestePersistencia {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 10);

    public static void main(String[] args) throws Exception {
        Path pasta = Files.createTempDirectory("greensystem-teste");
        RepositorioCsv repo = new RepositorioCsv(pasta);

        System.out.println("--- 1) Primeira execução: sem arquivos não é erro ---");
        System.out.println("  áreas lidas: " + repo.carregarAreas().size());

        System.out.println("--- 2) Gravar com acentos ---");
        GerenciadorReservas g = new GerenciadorReservas();
        g.cadastrarArea(new Horta("HOR01", "Horta Água Viva", 20));
        g.cadastrarArea(new Viveiro("VIV01", "Viveiro São João", 15));
        g.reservar("HOR01", "Maria José", LocalDate.of(2026, 11, 12),
                LocalTime.of(14, 0), LocalTime.of(15, 30), "Colheita de couve e açaí", HOJE);
        g.reservar("VIV01", "Ana", LocalDate.of(2026, 11, 13),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Mudas", HOJE);
        repo.salvar(g.listarAreas(), g.listarReservas());
        System.out.println(Files.readString(pasta.resolve("areas.csv")));
        System.out.println(Files.readString(pasta.resolve("reservas.csv")));

        System.out.println("--- 3) Linhas em branco são ignoradas ---");
        Files.writeString(pasta.resolve("reservas.csv"), "\n\n", StandardOpenOption.APPEND);

        System.out.println("--- 4) Reabrir: tudo volta, com os acentos certos ---");
        List<AreaDeCultivo> areas = repo.carregarAreas();
        List<Reserva> reservas = repo.carregarReservas(areas);
        GerenciadorReservas g2 = new GerenciadorReservas();
        g2.carregarDados(areas, reservas);
        System.out.println("  áreas: " + g2.totalAreas() + " | reservas: " + g2.totalReservas());
        for (AreaDeCultivo a : g2.listarAreas()) {
            System.out.println("  " + a.tipo() + " " + a.getCodigo() + " | " + a.getNome());
        }
        for (Reserva r : g2.listarReservas()) {
            System.out.println("  #" + r.getNumero() + " " + r.getSolicitante() + " | " + r.getFinalidade());
        }

        System.out.println("--- 5) RN7: o próximo número continua de onde parou (esperado #3) ---");
        Reserva nova = g2.reservar("HOR01", "Carlos", LocalDate.of(2026, 11, 14),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Rega", HOJE);
        System.out.println("  nova reserva: #" + nova.getNumero());
    }
}