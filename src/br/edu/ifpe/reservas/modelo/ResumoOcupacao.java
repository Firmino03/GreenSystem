package br.edu.ifpe.reservas.modelo;

import java.time.Duration;

public record ResumoOcupacao(String codigoArea, String nomeArea, int totalReservas, Duration total) {

    // 2 horas e 30 minutos -> "2h30"
    public String horasFormatadas() {
        return total.toHours() + "h" + String.format("%02d", total.toMinutesPart());
    }
}