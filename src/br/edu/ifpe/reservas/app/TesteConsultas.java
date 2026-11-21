package br.edu.ifpe.reservas.app;

import br.edu.ifpe.reservas.modelo.Estufa;
import br.edu.ifpe.reservas.modelo.Horta;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.ResumoOcupacao;
import br.edu.ifpe.reservas.modelo.Viveiro;
import br.edu.ifpe.reservas.servico.GerenciadorReservas;

public class TesteConsultas {

    public static void main(String[] args) throws Exception {
        GerenciadorReservas g = new GerenciadorReservas();
        g.cadastrarArea(new Horta("HOR01", "Horta Central", 20));
        g.cadastrarArea(new Estufa("EST01", "Estufa de Mudas", 10));
        g.cadastrarArea(new Viveiro("VIV01", "Viveiro Norte", 15));

        g.reservar("HOR01", "Ana", "12/11/2026", 14 * 60, 16 * 60, "Plantio");
        g.reservar("HOR01", "Carlos", "12/11/2026", 9 * 60, 10 * 60, "Rega");
        g.reservar("EST01", "Ana", "12/11/2026", 10 * 60, 11 * 60, "Mudas");
        g.reservar("VIV01", "Bia", "13/11/2026", 8 * 60, 9 * 60 + 30, "Adubação");

        System.out.println("--- Agenda de 12/11/2026 (todas as áreas, ordenada por horário) ---");
        for (Reserva r : g.agendaDoDia("12/11/2026")) {
            System.out.println("  #" + r.getNumero() + " " + r.getArea().getNome()
                    + " | " + r.getInicioMinutos() / 60 + "h-" + r.getFimMinutos() / 60 + "h"
                    + " | " + r.getSolicitante());
        }

        System.out.println("--- Agenda de 12/11/2026 só da Horta ---");
        for (Reserva r : g.agendaDoDia("12/11/2026", "HOR01")) {
            System.out.println("  #" + r.getNumero() + " " + r.getSolicitante());
        }

        System.out.println("--- Data sem zero (1 dígito) ---");
        System.out.println("  reservas em 13/11: " + g.agendaDoDia("13/11/2026").size());

        System.out.println("--- Solicitantes distintos ---");
        System.out.println("  " + g.quantidadeSolicitantesDistintos());

        System.out.println("--- Resumo de ocupação ---");
        for (ResumoOcupacao r : g.resumoDeOcupacao()) {
            System.out.println("  " + r.codigoArea() + " | " + r.nomeArea()
            + " | " + r.totalReservas() + " reserva(s) | " + r.horasFormatadas());
        }
    }
}