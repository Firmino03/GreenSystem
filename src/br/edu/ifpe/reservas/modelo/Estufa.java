package br.edu.ifpe.reservas.modelo;

import java.time.Duration;
import java.time.LocalTime;

public class Estufa extends AreaDeCultivo {

    public Estufa(String codigo, String nome, int capacidade) {
        super(codigo, nome, capacidade);
    }

    @Override
    public String tipo() {
        return "Estufa";
    }

    @Override
    public Duration duracaoMaxima() {
        return Duration.ofHours(1);
    }

    @Override
    public LocalTime abertura() {
        return LocalTime.of(7, 0);
    }

    @Override
    public LocalTime fechamento() {
        return LocalTime.of(19, 0);
    }

    @Override
    public int antecedenciaMinimaDias() {
        return 1;
    }
}