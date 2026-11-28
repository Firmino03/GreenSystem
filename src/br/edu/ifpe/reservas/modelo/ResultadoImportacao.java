package br.edu.ifpe.reservas.modelo;

import java.util.List;

public record ResultadoImportacao(int importadas, int descartadas, List<String> motivos) {

    // Construtor compacto: copia a lista para ela não poder ser alterada de fora
    public ResultadoImportacao {
        motivos = List.copyOf(motivos);
    }
}