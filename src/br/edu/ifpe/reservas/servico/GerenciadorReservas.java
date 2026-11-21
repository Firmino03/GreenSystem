package br.edu.ifpe.reservas.servico;

import br.edu.ifpe.reservas.excecao.ConflitoDeHorarioException;
import br.edu.ifpe.reservas.excecao.RegraDeReservaException;
import br.edu.ifpe.reservas.modelo.AreaDeCultivo;
import br.edu.ifpe.reservas.modelo.Reserva;
import java.util.ArrayList;
import java.util.HashMap;

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

        // O construtor da Reserva valida data, textos e fim depois do início (RN2, RN3, RN4)
        Reserva nova = new Reserva(proximoNumero, area, solicitante, data, inicio, fim, finalidade);

        // RN6: cada subclasse responde com os próprios limites (polimorfismo, sem if/switch no tipo)
        if (nova.duracaoMinutos() > area.duracaoMaximaMinutos()) {
            throw new RegraDeReservaException("A duração máxima para " + area.tipo() 
            + " é de " + area.duracaoMaximaMinutos() + " minutos.");
        }
        if (inicio < area.aberturaMinutos()) {
            throw new RegraDeReservaException(area.tipo() + " só abre às " + formatarHora(area.aberturaMinutos()) + ".");
        }
        if (fim > area.fechamentoMinutos()) {
            throw new RegraDeReservaException(area.tipo() + " fecha às " + formatarHora(area.fechamentoMinutos()) + ".");
        }

        // RN5: conflito com reservas já existentes
        for (Reserva existente : reservas) {
            if (existente.conflitaCom(nova)) {
                throw new ConflitoDeHorarioException("Conflito com a reserva #" + existente.getNumero()
                        + " (" + existente.getData() + " "
                        + formatarHora(existente.getInicioMinutos()) + "-"
                        + formatarHora(existente.getFimMinutos()) + ").");
            }
        }

        // Só chega aqui se tudo passou: a reserva recusada nunca ocupa um número
        reservas.add(nova);
        proximoNumero++;
        return nova;
    }

    public void cancelar(int numero) throws RegraDeReservaException {
        Reserva reserva = buscarReserva(numero);
        reservas.remove(reserva);
    }

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