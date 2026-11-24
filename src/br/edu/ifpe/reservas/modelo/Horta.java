package br.edu.ifpe.reservas.modelo;

import java.time.Duration;
import java.time.LocalTime;

public class Horta extends AreaDeCultivo {

    public Horta(String codigo, String nome, int capacidade) {
        super(codigo, nome, capacidade);
    }

    @Override
    public String tipo() {
        return "Horta";
    }

    @Override
    public Duration duracaoMaxima() {
        return Duration.ofHours(2);
    }

    @Override
    public LocalTime abertura() {
        return LocalTime.of(8, 0);
    }

    @Override
    public LocalTime fechamento() {
        return LocalTime.of(18, 0);
    }

    @Override
    public int antecedenciaMinimaDias() {
        return 0;
    }
}