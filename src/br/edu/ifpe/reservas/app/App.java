package br.edu.ifpe.reservas.app;

import br.edu.ifpe.reservas.excecao.ConflitoDeHorarioException;
import br.edu.ifpe.reservas.excecao.RegraDeReservaException;
import br.edu.ifpe.reservas.modelo.AreaDeCultivo;
import br.edu.ifpe.reservas.modelo.Estufa;
import br.edu.ifpe.reservas.modelo.Horta;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.ResumoOcupacao;
import br.edu.ifpe.reservas.modelo.Viveiro;
import br.edu.ifpe.reservas.servico.GerenciadorReservas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner teclado = new Scanner(System.in);
    private static final GerenciadorReservas gerenciador = new GerenciadorReservas();

    public static void main(String[] args) {
        carregarAreasDeExemplo();

        boolean rodando = true;
        while (rodando) {
            mostrarMenu();
            if (!teclado.hasNextLine()) {
                break; 
            }
            String opcao = teclado.nextLine().trim();

            try {
                switch (opcao) {
                    case "1" -> cadastrarArea();
                    case "2" -> listarAreas();
                    case "3" -> reservar();
                    case "4" -> cancelar();
                    case "5" -> mostrarAgenda();
                    case "6" -> mostrarConsultas();
                    case "7" -> {
                        System.out.println("Até logo!");
                        rodando = false;
                    }
                    default -> System.out.println("Opção inválida. Escolha de 1 a 7.");
                }
            } catch (RegraDeReservaException | ConflitoDeHorarioException | IllegalArgumentException e) {
                System.out.println("Não foi possível: " + e.getMessage());
            }
        }
    }


    private static void mostrarMenu() {
        System.out.println();
        System.out.println("Hoje: " + Reserva.formatarData(LocalDate.now())
                + " | Áreas: " + gerenciador.totalAreas()
                + " | Reservas: " + gerenciador.totalReservas());
        System.out.println("1. Cadastrar área  2. Listar áreas  3. Reservar  4. Cancelar");
        System.out.println("5. Agenda do dia   6. Consultas     7. Sair");
        System.out.print("Opção: ");
    }


    private static void cadastrarArea() throws RegraDeReservaException {
        System.out.println("Tipo: 1. Horta  2. Estufa  3. Viveiro");
        String tipo = lerTexto("Tipo: ");
        String codigo = lerTexto("Código: ").toUpperCase();
        String nome = lerTexto("Nome: ");
        int capacidade = lerInteiro("Capacidade: ");

        AreaDeCultivo area = switch (tipo) {
            case "1" -> new Horta(codigo, nome, capacidade);
            case "2" -> new Estufa(codigo, nome, capacidade);
            case "3" -> new Viveiro(codigo, nome, capacidade);
            default -> throw new RegraDeReservaException("Tipo inválido. Escolha 1, 2 ou 3.");
        };

        gerenciador.cadastrarArea(area);
        System.out.println("Área cadastrada: " + descreverArea(area));
    }

    private static void listarAreas() {
        List<AreaDeCultivo> areas = gerenciador.listarAreas();
        if (areas.isEmpty()) {
            System.out.println("Nenhuma área cadastrada.");
            return;
        }
        for (AreaDeCultivo area : areas) {
            System.out.println("  " + descreverArea(area));
        }
    }

    private static void reservar() throws RegraDeReservaException, ConflitoDeHorarioException {
        listarAreas();
        String codigo = lerTexto("Área (código): ").toUpperCase();
        String solicitante = lerTexto("Solicitante: ");
        LocalDate data = Reserva.converterData(lerTexto("Data (dd/MM/yyyy): "));
        LocalTime inicio = Reserva.converterHora(lerTexto("Início (HH:mm): "));
        LocalTime fim = Reserva.converterHora(lerTexto("Fim (HH:mm): "));
        String finalidade = lerTexto("Finalidade: ");

        Reserva reserva = gerenciador.reservar(codigo, solicitante, data, inicio, fim, finalidade, LocalDate.now());
        System.out.println("Reserva confirmada: " + descreverReserva(reserva));
    }

    private static void cancelar() throws RegraDeReservaException {
        int numero = lerInteiro("Número da reserva: ");
        gerenciador.cancelar(numero);
        System.out.println("Reserva #" + numero + " cancelada.");
    }

    private static void mostrarAgenda() throws RegraDeReservaException {
        LocalDate data = Reserva.converterData(lerTexto("Data (dd/MM/yyyy): "));
        String codigo = lerTexto("Área (código, ou Enter para todas): ").toUpperCase();

        // Usa o método sobrecarregado: com ou sem o código da área
        List<Reserva> agenda = codigo.isEmpty()
                ? gerenciador.agendaDoDia(data)
                : gerenciador.agendaDoDia(data, codigo);

        if (agenda.isEmpty()) {
            System.out.println("Nenhuma reserva nesse dia.");
            return;
        }
        for (Reserva r : agenda) {
            System.out.println("  " + descreverReserva(r));
        }
    }

    private static void mostrarConsultas() {
        System.out.println("Ocupação por área:");
        for (ResumoOcupacao resumo : gerenciador.resumoDeOcupacao()) {
            System.out.println("  " + resumo.codigoArea() + " | " + resumo.nomeArea() + " | "
                    + resumo.totalReservas() + " reserva(s) | " + resumo.horasFormatadas());
        }
        System.out.println("Solicitantes diferentes: " + gerenciador.quantidadeSolicitantesDistintos());
    }


    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return teclado.nextLine().trim();
    }

    private static int lerInteiro(String mensagem) throws RegraDeReservaException {
        String texto = lerTexto(mensagem);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new RegraDeReservaException("Digite um número inteiro válido.");
        }
    }

    // ---------- Textos de saída ----------

    private static String descreverArea(AreaDeCultivo area) {
        return area.tipo() + " " + area.getCodigo() + " | " + area.getNome()
                + " | capacidade " + area.getCapacidade()
                + " | " + Reserva.formatarHora(area.abertura())
                + "-" + Reserva.formatarHora(area.fechamento())
                + " | máx. " + area.duracaoMaxima().toMinutes() + " min"
                + " | antecedência mín. " + area.antecedenciaMinimaDias() + " dia(s)";
    }

    private static String descreverReserva(Reserva r) {
        return "#" + r.getNumero() + " " + r.getArea().getNome()
                + " | " + Reserva.formatarData(r.getData()) + " "
                + Reserva.formatarHora(r.getInicio()) + "-" + Reserva.formatarHora(r.getFim())
                + " | " + r.getSolicitante() + " | " + r.getFinalidade();
    }

    // ---------- Dados iniciais (até a persistência em CSV existir) ----------

    private static void carregarAreasDeExemplo() {
        try {
            gerenciador.cadastrarArea(new Horta("HOR01", "Horta Central", 20));
            gerenciador.cadastrarArea(new Estufa("EST01", "Estufa de Mudas", 10));
            gerenciador.cadastrarArea(new Viveiro("VIV01", "Viveiro Norte", 15));
        } catch (RegraDeReservaException e) {
            System.out.println("Erro ao carregar áreas de exemplo: " + e.getMessage());
        }
    }
}