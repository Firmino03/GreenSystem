package br.edu.ifpe.reservas.app;

import br.edu.ifpe.reservas.excecao.ConflitoDeHorarioException;
import br.edu.ifpe.reservas.excecao.RegraDeReservaException;
import br.edu.ifpe.reservas.modelo.Estufa;
import br.edu.ifpe.reservas.modelo.Horta;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.Viveiro;
import br.edu.ifpe.reservas.servico.GerenciadorReservas;

public class Teste {

    public static void main(String[] args) throws Exception {
        GerenciadorReservas g = new GerenciadorReservas();
        g.cadastrarArea(new Horta("HOR01", "Horta Central", 20));
        g.cadastrarArea(new Estufa("EST01", "Estufa de Mudas", 10));
        g.cadastrarArea(new Viveiro("VIV01", "Viveiro Norte", 15));

        System.out.println("--- Reservas na Horta (120 min, 08:00 às 18:00) ---");
        tentar(g, "válida 14:00-15:30            ", "HOR01", "12/11/2026", 14 * 60, 15 * 60 + 30);
        tentar(g, "conflita 15:00-16:00          ", "HOR01", "12/11/2026", 15 * 60, 16 * 60);
        tentar(g, "só encosta 15:30-16:30        ", "HOR01", "12/11/2026", 15 * 60 + 30, 16 * 60 + 30);
        tentar(g, "mesmo horário, outro dia      ", "HOR01", "13/11/2026", 14 * 60, 15 * 60);
        tentar(g, "muito longa 08:00-11:00       ", "HOR01", "14/11/2026", 8 * 60, 11 * 60);
        tentar(g, "antes de abrir 07:00-08:00    ", "HOR01", "14/11/2026", 7 * 60, 8 * 60);
        tentar(g, "depois de fechar 17:30-18:30  ", "HOR01", "14/11/2026", 17 * 60 + 30, 18 * 60 + 30);

        System.out.println();
        System.out.println("--- Entradas inválidas ---");
        tentar(g, "área que não existe           ", "XXX99", "12/11/2026", 10 * 60, 11 * 60);
        tentar(g, "data com traço                ", "HOR01", "12-11-2026", 10 * 60, 11 * 60);
        tentar(g, "data com letra                ", "HOR01", "ab/11/2026", 10 * 60, 11 * 60);
        tentar(g, "mês 13                        ", "HOR01", "12/13/2026", 10 * 60, 11 * 60);
        tentar(g, "fim antes do início           ", "HOR01", "12/11/2026", 11 * 60, 10 * 60);

        System.out.println();
        System.out.println("--- Mesmo horário em áreas diferentes (Estufa 60 min, Viveiro 90 min) ---");
        tentar(g, "Estufa 10:00-11:00            ", "EST01", "12/11/2026", 10 * 60, 11 * 60);
        tentar(g, "Estufa 10:00-11:30 (passa)    ", "EST01", "12/11/2026", 10 * 60, 11 * 60 + 30);
        tentar(g, "Viveiro 10:00-11:30           ", "VIV01", "12/11/2026", 10 * 60, 11 * 60 + 30);

        System.out.println();
        System.out.println("--- Cancelamento ---");
        try {
            g.cancelar(1);
            System.out.println("OK   | reserva #1 cancelada");
        } catch (RegraDeReservaException e) {
            System.out.println("ERRO | " + e.getMessage());
        }
        try {
            g.cancelar(99);
        } catch (RegraDeReservaException e) {
            System.out.println("ERRO | cancelar #99 -> " + e.getMessage());
        }
        tentar(g, "14:00-15:00 depois do cancel  ", "HOR01", "12/11/2026", 14 * 60, 15 * 60);
    }

    private static void tentar(GerenciadorReservas g, String rotulo, String codigo, String data, int inicio, int fim) {
        
        try {
            Reserva r = g.reservar(codigo, "Ana", data, inicio, fim, "Teste");
            System.out.println("OK   | " + rotulo + " -> reserva #" + r.getNumero());
        } catch (RegraDeReservaException | ConflitoDeHorarioException e) {
            System.out.println("ERRO | " + rotulo + " -> " + e.getMessage());
        }
    }
}