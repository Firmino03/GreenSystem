package br.edu.ifpe.reservas.modelo;

public class Estufa extends AreaDeCultivo {

    public Estufa(String codigo, String nome, int capacidade) {
        super(codigo, nome, capacidade);
    }

    @Override
    public String tipo() {
        return "Estufa";
    }

    @Override
    public int duracaoMaximaMinutos() {
        return 60; // 1 hora
    }

    @Override
    public int aberturaMinutos() {
        return 7 * 60; // 7 horas
    }

    @Override
    public int fechamentoMinutos() {
        return 19 * 60; // 19 horas
    }
}