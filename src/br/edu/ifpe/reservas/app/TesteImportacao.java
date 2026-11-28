package br.edu.ifpe.reservas.app;

import br.edu.ifpe.reservas.modelo.Estufa;
import br.edu.ifpe.reservas.modelo.Horta;
import br.edu.ifpe.reservas.modelo.Reserva;
import br.edu.ifpe.reservas.modelo.ResultadoImportacao;
import br.edu.ifpe.reservas.modelo.Viveiro;
import br.edu.ifpe.reservas.persistencia.RepositorioCsv;
import br.edu.ifpe.reservas.servico.GerenciadorReservas;
import br.edu.ifpe.reservas.servico.ImportadorReservas;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class TesteImportacao {

    private static final LocalDate HOJE = LocalDate.of(2026, 11, 28);

    public static void main(String[] args) throws Exception {
        List<String> conteudo = List.of(
                "HOR01;Maria José;03/12/2026;09:00;10:30;Colheita de couve",
                "EST01;Ana;04/12/2026;10:00;11:00;Mudas de alface",
                "VIV01;Carlos;04/12/2026;08:00;09:30;Adubação do viveiro",
                "",
                "HOR01;Pedro;03/12/2026;10:00;11:00;Rega",
                "XXX99;Bia;05/12/2026;10:00;11:00;Área que não existe",
                "HOR01;Lia;31/02/2026;10:00;11:00;Data que não existe",
                "EST01;Rui;05/12/2026;10:00;12:00;Passa da duração máxima",
                "HOR01;Teo;05/12/2026;07:00;08:00;Antes de abrir",
                "linha;quebrada",
                "HOR01;Ivo;06/12/2026;9:00;10:00;Horário sem zero");

        Path pasta = Files.createTempDirectory("greensystem-importacao");
        Path arquivo = pasta.resolve("importacao.csv");
        Files.write(arquivo, conteudo, StandardCharsets.UTF_8);

        GerenciadorReservas g = new GerenciadorReservas();
        g.cadastrarArea(new Horta("HOR01", "Horta Central", 20));
        g.cadastrarArea(new Estufa("EST01", "Estufa de Mudas", 10));
        g.cadastrarArea(new Viveiro("VIV01", "Viveiro Norte", 15));

        RepositorioCsv repositorio = new RepositorioCsv(pasta);

        System.out.println("--- 1) Leitura: a linha em branco é ignorada ---");
        List<String> linhas = repositorio.lerLinhasDeImportacao(arquivo);
        System.out.println("  linhas lidas: " + linhas.size() + " (esperado 10)");

        System.out.println("--- 2) Importação (hoje = 28/11/2026) ---");
        ResultadoImportacao resultado = new ImportadorReservas(g).importar(linhas, HOJE);
        System.out.println("  importadas: " + resultado.importadas() + " (esperado 4)");
        System.out.println("  descartadas: " + resultado.descartadas() + " (esperado 6)");
        for (String motivo : resultado.motivos()) {
            System.out.println("    " + motivo);
        }

        System.out.println("--- 3) O que ficou no gerenciador ---");
        for (Reserva r : g.listarReservas()) {
            System.out.println("  #" + r.getNumero() + " " + r.getArea().getCodigo()
                    + " | " + Reserva.formatarData(r.getData())
                    + " " + Reserva.formatarHora(r.getInicio()) + "-" + Reserva.formatarHora(r.getFim())
                    + " | " + r.getSolicitante() + " | " + r.getFinalidade());
        }

        System.out.println("--- 4) Arquivo que não existe ---");
        try {
            repositorio.lerLinhasDeImportacao(pasta.resolve("nao-existe.csv"));
            System.out.println("  ERRO: devia ter dado exceção");
        } catch (NoSuchFileException e) {
            System.out.println("  tratado: arquivo não encontrado");
        }
    }
}