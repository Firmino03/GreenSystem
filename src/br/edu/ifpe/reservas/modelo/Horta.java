package br.edu.ifpe.reservas.modelo;

public class Horta extends AreaDeCultivo {

    public Horta(String codigo, String nome, int capacidade) {
        super(codigo, nome, capacidade);
    }

    @Override
    public String tipo() {
        return "Horta";
    }

    @Override
    public int duracaoMaximaMinutos() {
        return 120; // 2 horas
    }

    @Override
    public int aberturaMinutos() {
        return 8 * 60; // 8 horas
    }

    @Override
    public int fechamentoMinutos() {
        return 18 * 60; // 18 horas
    }
}