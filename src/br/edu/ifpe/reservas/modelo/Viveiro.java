package br.edu.ifpe.reservas.modelo;

public class Viveiro extends AreaDeCultivo {

    public Viveiro(String codigo, String nome, int capacidade) {
        super(codigo, nome, capacidade);
    }

    @Override
    public String tipo() {
        return "Viveiro";
    }

    @Override
    public int duracaoMaximaMinutos() {
        return 90; // 1 hora e 30 minutos
    }

    @Override
    public int aberturaMinutos() {
        return 6 * 60; // 6 horas
    }

    @Override
    public int fechamentoMinutos() {
        return 20 * 60; // 20 horas
    }
}