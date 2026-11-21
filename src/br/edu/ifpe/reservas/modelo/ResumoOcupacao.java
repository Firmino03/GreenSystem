package br.edu.ifpe.reservas.modelo;

public record ResumoOcupacao(String codigoArea, String nomeArea, int totalReservas, int totalMinutos) {

    public String horasFormatadas() {
        return String.format("%dh%02d", totalMinutos / 60, totalMinutos % 60);
    }
}