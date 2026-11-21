package br.edu.ifpe.reservas.app;

import br.edu.ifpe.reservas.excecao.ConflitoDeHorarioException;
import br.edu.ifpe.reservas.excecao.RegraDeReservaException;
import br.edu.ifpe.reservas.modelo.Estufa;
import br.edu.ifpe.reservas.modelo.Horta;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.Viveiro;
import br.edu.ifpe.reservas.servico.GerenciadorReservas;

public class TesteConflitos {

    private static final String DIA = "12/11/2026";

    public static void main(String[] args) throws Exception {
        GerenciadorReservas g = new GerenciadorReservas();
        g.cadastrarArea(new Horta("HOR01", "Horta Central", 20));
        g.cadastrarArea(new Estufa("EST01", "Estufa de Mudas", 10));
        g.cadastrarArea(new Viveiro("VIV01", "Viveiro Norte", 15));

        System.out.println("--- 1) Mesmo dia e horário (10:00-11:00) em áreas diferentes: tudo deve passar ---");
        tentar(g, "Horta   10:00-11:00", "HOR01", 10 * 60, 11 * 60);
        tentar(g, "Estufa  10:00-11:00", "EST01", 10 * 60, 11 * 60);
        tentar(g, "Viveiro 10:00-11:00", "VIV01", 10 * 60, 11 * 60);

        System.out.println();
        System.out.println("--- 2) Sobreposição dentro de cada área: tudo deve dar conflito ---");
        tentar(g, "Horta   10:30-11:30", "HOR01", 10 * 60 + 30, 11 * 60 + 30);
        tentar(g, "Estufa  10:30-11:30", "EST01", 10 * 60 + 30, 11 * 60 + 30);
        tentar(g, "Viveiro 10:30-11:30", "VIV01", 10 * 60 + 30, 11 * 60 + 30);

        System.out.println();
        System.out.println("--- 3) Reserva que engloba a existente (09:30-11:30 na Horta): conflito ---");
        tentar(g, "Horta   09:30-11:30", "HOR01", 9 * 60 + 30, 11 * 60 + 30);

        System.out.println();
        System.out.println("--- 4) Só encostando (11:00-12:00): tudo deve passar ---");
        tentar(g, "Horta   11:00-12:00", "HOR01", 11 * 60, 12 * 60);
        tentar(g, "Estufa  11:00-12:00", "EST01", 11 * 60, 12 * 60);
        tentar(g, "Viveiro 11:00-12:00", "VIV01", 11 * 60, 12 * 60);

        System.out.println();
        System.out.println("--- 5) Encostando antes (09:00-10:00): tudo deve passar ---");
        tentar(g, "Horta   09:00-10:00", "HOR01", 9 * 60, 10 * 60);
        tentar(g, "Estufa  09:00-10:00", "EST01", 9 * 60, 10 * 60);
        tentar(g, "Viveiro 09:00-10:00", "VIV01", 9 * 60, 10 * 60);
    }

    private static void tentar(GerenciadorReservas g, String rotulo, String codigo, int inicio, int fim) {
        try {
            Reserva r = g.reservar(codigo, "Ana", DIA, inicio, fim, "Teste");
            System.out.println("OK   | " + rotulo + " -> reserva #" + r.getNumero());
        } catch (RegraDeReservaException | ConflitoDeHorarioException e) {
            System.out.println("ERRO | " + rotulo + " -> " + e.getMessage());
        }
    }
}