package br.edu.ifpe.reservas.excecao;

public class ConflitoDeHorarioException extends Exception {
    public ConflitoDeHorarioException(String mensagem) {
        super(mensagem);
    }
}
