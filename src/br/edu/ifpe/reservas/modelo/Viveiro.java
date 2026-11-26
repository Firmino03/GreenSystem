package br.edu.ifpe.reservas.modelo;

import java.time.Duration;
import java.time.LocalTime;

public class Viveiro extends AreaDeCultivo {

    public Viveiro(String codigo, String nome, int capacidade) {
        super(codigo, nome, capacidade);
    }

    @Override
    public String tipo() {
        return "Viveiro";
    }

    @Override
    public Duration duracaoMaxima() {
        return Duration.ofMinutes(90);
    }

    @Override
    public LocalTime abertura() {
        return LocalTime.of(6, 0);
    }

    @Override
    public LocalTime fechamento() {
        return LocalTime.of(20, 0);
    }

    @Override
    public int antecedenciaMinimaDias() {
        return 2;
    }
}