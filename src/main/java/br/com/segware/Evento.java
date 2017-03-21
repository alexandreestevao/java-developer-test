package br.com.segware;

import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;

public class Evento {

    private Integer codigoSequencial;
    private String codigoCliente;
    private String codigoEvento;
    private Tipo tipoEvento;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String codigoAtendente;

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public Tipo getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(Tipo tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getCodigoAtendente() {
        return codigoAtendente;
    }

    public void setCodigoAtendente(String codigoAtendente) {
        this.codigoAtendente = codigoAtendente;
    }

    public Integer getCodigoSequencial() {
        return codigoSequencial;
    }

    public void setCodigoSequencial(Integer codigoSequencial) {
        this.codigoSequencial = codigoSequencial;
    }

    public Long getDiferencaFimInicio() {
        Integer diferencaInt = Seconds.secondsBetween(dataInicio, dataFim).getSeconds();
        return new Long(diferencaInt);
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Evento evento = (Evento) o;

        return codigoSequencial.equals(evento.getCodigoSequencial());
    }

    @Override
    public int hashCode() {
        return codigoSequencial.hashCode();
    }

}