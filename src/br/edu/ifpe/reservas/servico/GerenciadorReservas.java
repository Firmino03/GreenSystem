package br.edu.ifpe.reservas.servico;

import br.edu.ifpe.reservas.excecao.ConflitoDeHorarioException;
import br.edu.ifpe.reservas.excecao.RegraDeReservaException;
import br.edu.ifpe.reservas.modelo.AreaDeCultivo;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.ResumoOcupacao;
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

    public Reserva reservar(String codigoArea, String solicitante, String data, int inicio, int fim, String finalidade)
            throws RegraDeReservaException, ConflitoDeHorarioException {

            AreaDeCultivo area = areas.get(codigoArea);
            if (area == null) {
                throw new RegraDeReservaException("Área de cultivo não encontrada: " + codigoArea);
            }

            Reserva nova = new Reserva(proximoNumero, area, solicitante, data, inicio, fim, finalidade);

            if (nova.duracaoMinutos() > area.duracaoMaximaMinutos()) {
                throw new RegraDeReservaException("A duração máxima para " + area.tipo()
                + " é de " + area.duracaoMaximaMinutos() + " minutos.");
            }

            if (inicio < area.aberturaMinutos()) {
                throw new RegraDeReservaException(area.tipo() + " só abre às "
                + formatarHora(area.aberturaMinutos()) + ".");
            }
            if (fim > area.fechamentoMinutos()) {
                throw new RegraDeReservaException(area.tipo() + " fecha às "
                + formatarHora(area.fechamentoMinutos()) + ".");
            }

            for (Reserva existente : reservas) {
                if (existente.conflitaCom(nova)) {
                    throw new ConflitoDeHorarioException("Conflito com a reserva #" + existente.getNumero()
                    + " (" + existente.getData() + " " 
                    + formatarHora(existente.getInicioMinutos()) + "-"
                    + formatarHora(existente.getFimMinutos()) + ").");
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
    public List<Reserva> agendaDoDia(String data) throws RegraDeReservaException {

        String dataNormalizada = Reserva.validarData(data);
        List<Reserva> resultado = new ArrayList<>();
        
        for (Reserva r : reservas) {
            if (r.getData().equals(dataNormalizada)) {
                resultado.add(r);
            }
        }
        resultado.sort(Comparator.comparingInt(Reserva::getInicioMinutos));
        return resultado;
    }

    public List<Reserva> agendaDoDia(String data, String codigoArea) throws RegraDeReservaException {
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

    // Total de minutos reservados por área: código -> minutos (HashMap, RT8)
    public HashMap<String, Integer> totalMinutosPorArea() {
        HashMap<String, Integer> totais = new HashMap<>();
        for (Reserva r : reservas) {
            String codigo = r.getArea().getCodigo();
            totais.put(codigo, totais.getOrDefault(codigo, 0) + r.duracaoMinutos());
        }
        return totais;
    }

    public int quantidadeSolicitantesDistintos() {
        HashSet<String> solicitantes = new HashSet<>();
        for (Reserva r : reservas) {
            solicitantes.add(r.getSolicitante());
        }
        return solicitantes.size();
    }

    public List<ResumoOcupacao> resumoDeOcupacao() {
        HashMap<String, Integer> minutos = totalMinutosPorArea();
        List<ResumoOcupacao> resumos = new ArrayList<>();

        for (AreaDeCultivo area : areas.values()) {
            int totalReservas = 0;
            for (Reserva r : reservas) {
                if (r.getArea().getCodigo().equals(area.getCodigo())) {
                    totalReservas++;
                }
            }
            int totalMinutos = minutos.getOrDefault(area.getCodigo(), 0);
            resumos.add(new ResumoOcupacao(area.getCodigo(), area.getNome(), totalReservas, totalMinutos));
        }

        resumos.sort(Comparator.comparing(ResumoOcupacao::codigoArea));
        return resumos;
    }


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

    // ---------- Métodos internos ----------

    private Reserva buscarReserva(int numero) throws RegraDeReservaException {
        for (Reserva r : reservas) {
            if (r.getNumero() == numero) {
                return r;
            }
        }
        throw new RegraDeReservaException("Reserva #" + numero + " não encontrada.");
    }

    private static String formatarHora(int minutos) {
        return String.format("%02d:%02d", minutos / 60, minutos % 60);
    }
}