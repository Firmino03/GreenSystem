package br.edu.ifpe.reservas.app;

import br.edu.ifpe.reservas.persistencia.RegistroLog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class TesteLog {

    public static void main(String[] args) throws Exception {
        Path pasta = Files.createTempDirectory("greensystem-log");
        Path arquivo = pasta.resolve("log.txt");

        System.out.println("O arquivo existe antes? " + Files.exists(arquivo));

        // Primeira execução (o arquivo ainda não existe: deve ser criado)
        RegistroLog execucao1 = new RegistroLog(arquivo);
        execucao1.registrar(LocalDateTime.of(2026, 11, 10, 9, 30, 15), "Programa iniciado");
        execucao1.registrar(LocalDateTime.of(2026, 11, 10, 9, 31, 2), "Reserva criada: #1 HOR01");
        execucao1.registrar(LocalDateTime.of(2026, 11, 10, 9, 32, 40), "Programa encerrado");

        // Segunda execução (novo objeto, mesmo arquivo: deve acrescentar)
        RegistroLog execucao2 = new RegistroLog(arquivo);
        execucao2.registrar(LocalDateTime.of(2026, 11, 11, 14, 0, 5), "Programa iniciado");
        execucao2.registrar(LocalDateTime.of(2026, 11, 11, 14, 1, 20), "Área cadastrada: Horta HOR02");

        System.out.println();
        System.out.println(Files.readString(arquivo));
    }
}