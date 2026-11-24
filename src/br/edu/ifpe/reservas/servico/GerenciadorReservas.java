package br.edu.ifpe.reservas.servico;

import br.edu.ifpe.reservas.excecao.ConflitoDeHorarioException;
import br.edu.ifpe.reservas.excecao.RegraDeReservaException;
import br.edu.ifpe.reservas.modelo.AreaDeCultivo;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.ResumoOcupacao;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GerenciadorReservas {
    private final HashMap<String, AreaDeCultivo> areas = new HashMap<>();
    private final ArrayList<Reserva> reservas = new ArrayList<>();
    private int proximoNumero = 1;


    public void cadastrarArea(AreaDeCultivo area) throws RegraDeReservaException {
        if (area == null) {
            throw new RegraDeReservaException("A área de cultivo não pode ser nula.");
        }
        if (areas.containsKey(area.getCodigo())) {
            throw new RegraDeReservaException("Já existe uma área com o código " + area.getCodigo() + ".");
        }
        areas.put(area.getCodigo(), area);
    }

    // "hoje" vem de fora (só o App chama LocalDate.now()), para as regras serem testáveis
    public Reserva reservar(String codigoArea, String solicitante, LocalDate data,
            LocalTime inicio, LocalTime fim, String finalidade, LocalDate hoje)
            throws RegraDeReservaException, ConflitoDeHorarioException {

        AreaDeCultivo area = areas.get(codigoArea);
        if (area == null) {
            throw new RegraDeReservaException("Área de cultivo não encontrada: " + codigoArea);
        }

        // O construtor valida textos, data, horários e fim depois do início
        Reserva nova = new Reserva(proximoNumero, area, solicitante, data, inicio, fim, finalidade);

        // Data passada e antecedência mínima (a antecedência vem da subclasse: polimorfismo)
        if (data.isBefore(hoje)) {
            throw new RegraDeReservaException("Não é possível reservar uma data que já passou.");
        }
        long diasDeAntecedencia = ChronoUnit.DAYS.between(hoje, data);
        if (diasDeAntecedencia < area.antecedenciaMinimaDias()) {
            throw new RegraDeReservaException(area.tipo() + " exige antecedência mínima de "
                    + area.antecedenciaMinimaDias() + " dia(s).");
        }

        // RN6: duração máxima e horário de funcionamento (polimorfismo)
        if (nova.duracao().compareTo(area.duracaoMaxima()) > 0) {
            throw new RegraDeReservaException("A duração máxima para " + area.tipo()
                    + " é de " + area.duracaoMaxima().toMinutes() + " minutos.");
        }
        if (inicio.isBefore(area.abertura())) {
            throw new RegraDeReservaException(area.tipo() + " só abre às "
                    + Reserva.formatarHora(area.abertura()) + ".");
        }
        if (fim.isAfter(area.fechamento())) {
            throw new RegraDeReservaException(area.tipo() + " fecha às "
                    + Reserva.formatarHora(area.fechamento()) + ".");
        }

        // RN5: conflito com reservas já existentes
        for (Reserva existente : reservas) {
            if (existente.conflitaCom(nova)) {
                throw new ConflitoDeHorarioException("Conflito com a reserva #" + existente.getNumero()
                        + " (" + Reserva.formatarData(existente.getData()) + " "
                        + Reserva.formatarHora(existente.getInicio()) + "-"
                        + Reserva.formatarHora(existente.getFim()) + ").");
            }
        }

        reservas.add(nova);
        proximoNumero++;
        return nova;
    }

    public void cancelar(int numero) throws RegraDeReservaException {
        Reserva reserva = buscarReserva(numero);
        reservas.remove(reserva);
    }

    // ---------- Consultas (RN9) ----------

    // Método SOBRECARREGADO (RT6): mesmo nome, parâmetros diferentes.

    // Agenda de um dia, de todas as áreas
    public List<Reserva> agendaDoDia(LocalDate data) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getData().equals(data)) {
                resultado.add(r);
            }
        }
        resultado.sort(Comparator.comparing(Reserva::getInicio));
        return resultado;
    }

    // Agenda de um dia, só de uma área
    public List<Reserva> agendaDoDia(LocalDate data, String codigoArea) throws RegraDeReservaException {
        if (!areas.containsKey(codigoArea)) {
            throw new RegraDeReservaException("Área de cultivo não encontrada: " + codigoArea);
        }
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : agendaDoDia(data)) {
            if (r.getArea().getCodigo().equals(codigoArea)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    // Total reservado por área: código -> Duration (HashMap, RT8)
    public HashMap<String, Duration> totalPorArea() {
        HashMap<String, Duration> totais = new HashMap<>();
        for (Reserva r : reservas) {
            String codigo = r.getArea().getCodigo();
            totais.put(codigo, totais.getOrDefault(codigo, Duration.ZERO).plus(r.duracao()));
        }
        return totais;
    }

    // Quantidade de solicitantes diferentes (HashSet não guarda repetidos, RT7)
    public int quantidadeSolicitantesDistintos() {
        HashSet<String> solicitantes = new HashSet<>();
        for (Reserva r : reservas) {
            solicitantes.add(r.getSolicitante());
        }
        return solicitantes.size();
    }

    // Resumo de ocupação de cada área, usando o record
    public List<ResumoOcupacao> resumoDeOcupacao() {
        HashMap<String, Duration> totais = totalPorArea();
        List<ResumoOcupacao> resumos = new ArrayList<>();

        for (AreaDeCultivo area : areas.values()) {
            int totalReservas = 0;
            for (Reserva r : reservas) {
                if (r.getArea().getCodigo().equals(area.getCodigo())) {
                    totalReservas++;
                }
            }
            Duration total = totais.getOrDefault(area.getCodigo(), Duration.ZERO);
            resumos.add(new ResumoOcupacao(area.getCodigo(), area.getNome(), totalReservas, total));
        }

        resumos.sort(Comparator.comparing(ResumoOcupacao::codigoArea));
        return resumos;
    }

    // ---------- Apoio para o menu ----------

    public List<AreaDeCultivo> listarAreas() {
        List<AreaDeCultivo> lista = new ArrayList<>(areas.values());
        lista.sort(Comparator.comparing(AreaDeCultivo::getCodigo));
        return lista;
    }

    public int totalAreas() {
        return areas.size();
    }

    public int totalReservas() {
        return reservas.size();
    }


    private Reserva buscarReserva(int numero) throws RegraDeReservaException {
        for (Reserva r : reservas) {
            if (r.getNumero() == numero) {
                return r;
            }
        }
        throw new RegraDeReservaException("Reserva #" + numero + " não encontrada.");
    }
}