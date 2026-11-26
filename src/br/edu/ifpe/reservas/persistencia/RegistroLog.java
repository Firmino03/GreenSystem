package br.edu.ifpe.reservas.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroLog {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Path arquivo;

    public RegistroLog(Path arquivo) {
        this.arquivo = arquivo;
    }

    public void registrar(LocalDateTime momento, String operacao) {
        String linha = momento.format(FORMATO) + " | " + operacao + System.lineSeparator();
        try {
            // CREATE cria o arquivo se não existir; APPEND acrescenta no final, sem apagar o que já tem
            Files.writeString(arquivo, linha, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // O log é um extra: se falhar, avisa e o programa continua
            System.out.println("Aviso: não foi possível escrever no log: " + e.getMessage());
        }
    }
}