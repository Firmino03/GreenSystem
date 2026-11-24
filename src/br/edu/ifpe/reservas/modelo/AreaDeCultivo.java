package br.edu.ifpe.reservas.modelo;

import java.time.Duration;
import java.time.LocalTime;

public abstract class AreaDeCultivo implements Exportavel {
    private final String codigo;
    private final String nome;
    private final int capacidade;

    public AreaDeCultivo(String codigo, String nome, int capacidade) {
        this.codigo = validarTexto(codigo, "código");
        this.nome = validarTexto(nome, "nome");

        if (capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade deve ser maior que zero.");
        }
        this.capacidade = capacidade;
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O " + campo + " não pode ser vazio.");
        }
        valor = valor.trim();
        if (valor.contains(";")) {
            throw new IllegalArgumentException("O " + campo + " não pode conter ponto e vírgula.");
        }
        return valor;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    @Override
    public String paraCsv() { //paracsv é o método que vai ser chamado para exportar os dados 
    // da área de cultivo em formato CSV
        return String.join(";", tipo(), codigo, nome, String.valueOf(capacidade));
    }
    //o ; é usado como separador de campos no CSV, e o método String.join() 
    // é usado para concatenar os valores dos campos em uma única string, separados por ponto e vírgula.
    public abstract String tipo();

    public abstract Duration duracaoMaxima();

    public abstract LocalTime abertura();

    public abstract LocalTime fechamento();

    public abstract int antecedenciaMinimaDias();
}