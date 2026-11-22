package br.edu.ifpe.reservas.modelo;

public abstract class AreaDeCultivo implements Exportavel {
    private final String codigo;
    private final String nome;
    private final int capacidade;

    public AreaDeCultivo(String codigo, String nome, int capacidade) {
        this.codigo = validarTexto(codigo, "código"); // Validar o código antes de atribuir
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

    public abstract String tipo();

    public abstract int duracaoMaximaMinutos();

    public abstract int aberturaMinutos();

    public abstract int fechamentoMinutos();

    @Override
    public String paraCsv() {
        return String.join(";", tipo(), codigo, nome, String.valueOf(capacidade));
    }
}