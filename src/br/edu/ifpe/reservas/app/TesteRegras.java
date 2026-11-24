package br.edu.ifpe.reservas.app;

import br.edu.ifpe.reservas.excecao.ConflitoDeHorarioException;
import br.edu.ifpe.reservas.excecao.RegraDeReservaException;
import br.edu.ifpe.reservas.modelo.Estufa;
import br.edu.ifpe.reservas.modelo.Horta;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.Viveiro;
import br.edu.ifpe.reservas.servico.GerenciadorReservas;

import java.time.LocalDate;

public class TesteRegras {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 10);

    public static void main(String[] args) throws Exception {
        GerenciadorReservas g = new GerenciadorReservas();
        g.cadastrarArea(new Horta("HOR01", "Horta Central", 20));
        g.cadastrarArea(new Estufa("EST01", "Estufa de Mudas", 10));
        g.cadastrarArea(new Viveiro("VIV01", "Viveiro Norte", 15));

        System.out.println("Hoje fixado em 10/11/2026");
        System.out.println();
        System.out.println("--- Conflitos (Horta) ---");
        tentar(g, "válida 12/11 14:00-15:30       ", "HOR01", "12/11/2026", "14:00", "15:30");
        tentar(g, "conflita 15:00-16:00           ", "HOR01", "12/11/2026", "15:00", "16:00");
        tentar(g, "só encosta 15:30-16:30         ", "HOR01", "12/11/2026", "15:30", "16:30");

        System.out.println();
        System.out.println("--- Data passada e antecedência (Horta 0, Estufa 1, Viveiro 2 dias) ---");
        tentar(g, "Horta ontem (09/11)            ", "HOR01", "09/11/2026", "10:00", "11:00");
        tentar(g, "Horta hoje (10/11)             ", "HOR01", "10/11/2026", "08:00", "09:00");
        tentar(g, "Estufa hoje (10/11)            ", "EST01", "10/11/2026", "10:00", "11:00");
        tentar(g, "Estufa amanhã (11/11)          ", "EST01", "11/11/2026", "10:00", "11:00");
        tentar(g, "Viveiro amanhã (11/11)         ", "VIV01", "11/11/2026", "10:00", "11:00");
        tentar(g, "Viveiro depois de amanhã (12/11)", "VIV01", "12/11/2026", "10:00", "11:30");

        System.out.println();
        System.out.println("--- Limites do tipo (Horta 2h, 08:00 às 18:00) ---");
        tentar(g, "3 horas seguidas               ", "HOR01", "13/11/2026", "09:00", "12:00");
        tentar(g, "antes de abrir 07:00-08:00     ", "HOR01", "13/11/2026", "07:00", "08:00");
        tentar(g, "depois de fechar 17:30-18:30   ", "HOR01", "13/11/2026", "17:30", "18:30");

        System.out.println();
        System.out.println("--- Datas (STRICT) ---");
        tentar(g, "31/02/2026 (não existe)        ", "HOR01", "31/02/2026", "10:00", "11:00");
        tentar(g, "29/02/2027 (não é bissexto)    ", "HOR01", "29/02/2027", "10:00", "11:00");
        tentar(g, "29/02/2028 (bissexto)          ", "HOR01", "29/02/2028", "10:00", "11:00");
        tentar(g, "1/12/2026 (sem zero)           ", "HOR01", "1/12/2026", "10:00", "11:00");
        tentar(g, "data com traço                 ", "HOR01", "12-11-2026", "10:00", "11:00");
        tentar(g, "data com letra                 ", "HOR01", "ab/11/2026", "10:00", "11:00");

        System.out.println();
        System.out.println("--- Horários ---");
        tentar(g, "25:99                          ", "HOR01", "14/11/2026", "25:99", "26:00");
        tentar(g, "24:00                          ", "HOR01", "14/11/2026", "10:00", "24:00");
        tentar(g, "abc                            ", "HOR01", "14/11/2026", "abc", "11:00");
        tentar(g, "fim antes do início            ", "HOR01", "14/11/2026", "11:00", "10:00");
        tentar(g, "9:00 (sem zero)                ", "HOR01", "14/11/2026", "9:00", "10:00");

        System.out.println();
        System.out.println("--- Área inexistente ---");
        tentar(g, "XXX99                          ", "XXX99", "14/11/2026", "10:00", "11:00");
    }

    // Faz o mesmo caminho do App: converte os textos e chama o gerenciador
    private static void tentar(GerenciadorReservas g, String rotulo, String codigo,
        String data, String inicio, String fim) {
        try {
            Reserva r = g.reservar(codigo, "Ana",
                    Reserva.converterData(data),
                    Reserva.converterHora(inicio),
                    Reserva.converterHora(fim),
                    "Teste", HOJE);
            System.out.println("OK   | " + rotulo + " -> reserva #" + r.getNumero());
        } catch (RegraDeReservaException | ConflitoDeHorarioException e) {
            System.out.println("ERRO | " + rotulo + " -> " + e.getMessage());
        }
    }
}