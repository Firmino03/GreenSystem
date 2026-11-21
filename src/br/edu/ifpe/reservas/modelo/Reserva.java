package br.edu.ifpe.reservas.modelo;

import br.edu.ifpe.reservas.excecao.RegraDeReservaException;

public class Reserva {
    private final int numero;
    // AreaDeCultivo é a superclasse de Horta, Estufa e Viveiro: por polimorfismo,
    // a reserva guarda qualquer uma delas sem saber qual é.
    private final AreaDeCultivo area;
    private final String solicitante;
    private final String data; 
    private final int inicioMinutos;
    private final int fimMinutos;
    private final String finalidade;

    public Reserva(int numero, AreaDeCultivo area, String solicitante, String data, int inicioMinutos, 
        int fimMinutos, String finalidade)
            
            throws RegraDeReservaException {
                if (area == null) {
                    throw new RegraDeReservaException("A área de cultivo não pode ser nula.");
                }
                if (inicioMinutos < 0 || fimMinutos > 24 * 60) {
                    throw new RegraDeReservaException("Os horários devem estar entre 00:00 e 24:00.");
                }
                if (fimMinutos <= inicioMinutos) {
                    throw new RegraDeReservaException("O horário de fim deve ser depois do início.");
                }

                this.numero = numero;
                this.area = area;
                this.solicitante = validarTexto(solicitante, "solicitante");
                this.data = validarData(data);
                this.inicioMinutos = inicioMinutos;
                this.fimMinutos = fimMinutos;
                this.finalidade = validarTexto(finalidade, "finalidade");
            }

    private static String validarTexto(String valor, String campo) throws RegraDeReservaException {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeReservaException("O campo " + campo + " não pode ser vazio.");
        }
        valor = valor.trim();
        if (valor.contains(";")) {
            throw new RegraDeReservaException("O campo " + campo + " não pode conter ponto e vírgula.");
        }
        return valor;
    }

    private static String validarData(String data) throws RegraDeReservaException {
        if (data == null) {
            throw new RegraDeReservaException("A data não pode ser vazia.");
        }

        String[] partes = data.trim().split("/");
        if (partes.length != 3) {
            throw new RegraDeReservaException("Data inválida: use o formato dd/MM/yyyy.");
        }

        try {
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int ano = Integer.parseInt(partes[2]);

            if (dia < 1 || dia > 31) {
                throw new RegraDeReservaException("Dia inválido: use um valor de 1 a 31.");
            }
            if (mes < 1 || mes > 12) {
                throw new RegraDeReservaException("Mês inválido: use um valor de 1 a 12.");
            }
            if (ano < 1) {
                throw new RegraDeReservaException("Ano inválido.");
            }

            return String.format("%02d/%02d/%04d", dia, mes, ano);

        } 
        catch (NumberFormatException e) {
            throw new RegraDeReservaException("Data inválida: use apenas números, no formato dd/MM/yyyy.");
        }
    }

    public int duracaoMinutos() {
        return fimMinutos - inicioMinutos;
    }

    public boolean conflitaCom(Reserva outra) {
        return this.area.getCodigo().equals(outra.area.getCodigo())
                && this.data.equals(outra.data)
                && this.inicioMinutos < outra.fimMinutos
                && outra.inicioMinutos < this.fimMinutos;
    }

    public int getNumero() {
        return numero;
    }

    public AreaDeCultivo getArea() {
        return area;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public String getData() {
        return data;
    }

    public int getInicioMinutos() {
        return inicioMinutos;
    }

    public int getFimMinutos() {
        return fimMinutos;
    }

    public String getFinalidade() {
        return finalidade;
    }
}