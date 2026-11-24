package br.edu.ifpe.reservas.modelo;

import br.edu.ifpe.reservas.excecao.RegraDeReservaException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class Reserva implements Exportavel {

    // STRICT recusa datas que não existem (31/02) e horas inválidas (24:00).
    // Com STRICT o ano precisa ser "uuuu" (e não "yyyy").

    private static final DateTimeFormatter ENTRADA_DATA =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ENTRADA_HORA =
            DateTimeFormatter.ofPattern("H:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter SAIDA_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter SAIDA_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final int numero;
    // AreaDeCultivo é a superclasse de Horta, Estufa e Viveiro: por polimorfismo,
    // a reserva guarda qualquer uma delas sem saber qual é.
    private final AreaDeCultivo area;
    private final String solicitante;
    private final LocalDate data;
    private final LocalTime inicio;
    private final LocalTime fim;
    private final String finalidade;

    public Reserva(int numero, AreaDeCultivo area, String solicitante, LocalDate data,
            LocalTime inicio, LocalTime fim, String finalidade)
            throws RegraDeReservaException {

        if (area == null) {
            throw new RegraDeReservaException("A área de cultivo não pode ser nula.");
        }
        if (data == null) {
            throw new RegraDeReservaException("A data não pode ser vazia.");
        }
        if (inicio == null || fim == null) {
            throw new RegraDeReservaException("Os horários de início e fim são obrigatórios.");
        }
        if (!fim.isAfter(inicio)) {
            throw new RegraDeReservaException("O horário de fim deve ser depois do início.");
        }

        this.numero = numero;
        this.area = area;
        this.solicitante = validarTexto(solicitante, "solicitante");
        this.data = data;
        this.inicio = inicio;
        this.fim = fim;
        this.finalidade = validarTexto(finalidade, "finalidade");
    }

    // Valida um texto (não vazio, sem ';') e devolve ele sem espaços nas pontas
    private static String validarTexto(String valor, String campo) throws RegraDeReservaException {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeReservaException("O campo " + campo + " não pode ser vazio.");
        }
        valor = valor.trim();
        if (valor.contains(";")) {
            throw new RegraDeReservaException("O campo " + campo + " não pode conter ponto e vírgula.");
        }
        return valor;
    }

    // "12/11/2026" -> LocalDate (recusa datas que não existem)
    public static LocalDate converterData(String texto) throws RegraDeReservaException {
        if (texto == null || texto.isBlank()) {
            throw new RegraDeReservaException("A data não pode ser vazia.");
        }
        try {
            return LocalDate.parse(texto.trim(), ENTRADA_DATA);
        } catch (DateTimeParseException e) {
            throw new RegraDeReservaException("Data inválida: use uma data que exista, no formato dd/MM/yyyy.");
        }
    }

    public static LocalTime converterHora(String texto) throws RegraDeReservaException {
        if (texto == null || texto.isBlank()) {
            throw new RegraDeReservaException("O horário não pode ser vazio.");
        }
        try {
            return LocalTime.parse(texto.trim(), ENTRADA_HORA);
        } catch (DateTimeParseException e) {
            throw new RegraDeReservaException("Horário inválido: use o formato HH:mm, de 00:00 a 23:59.");
        }
    }

    public static String formatarData(LocalDate data) {
        return data.format(SAIDA_DATA);
    }

    public static String formatarHora(LocalTime hora) {
        return hora.format(SAIDA_HORA);
    }

    public Duration duracao() {
        return Duration.between(inicio, fim);
    }

    // Conflito: mesma área, mesmo dia e horários que se sobrepõem
    public boolean conflitaCom(Reserva outra) {
        return this.area.getCodigo().equals(outra.area.getCodigo())
                && this.data.equals(outra.data)
                && this.inicio.isBefore(outra.fim)
                && outra.inicio.isBefore(this.fim);
    }

    // No CSV a data fica no formato ISO (2026-11-12) e os horários como HH:mm
    @Override
    public String paraCsv() {
        return String.join(";", String.valueOf(numero), area.getCodigo(), solicitante,
                data.toString(), inicio.toString(), fim.toString(), finalidade);
    }

    public int getNumero() {
        return numero;
    }

    public AreaDeCultivo getArea() {
        return area;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFim() {
        return fim;
    }

    public String getFinalidade() {
        return finalidade;
    }
}