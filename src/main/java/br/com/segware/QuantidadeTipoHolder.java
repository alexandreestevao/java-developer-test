package br.com.segware;

public class QuantidadeTipoHolder implements Comparable<QuantidadeTipoHolder> {

    public Integer getQuantidade() {
        return quantidade;
    }

    public Tipo getTipo() {
        return tipo;
    }

    private Integer quantidade;
    private Tipo tipo;

    public QuantidadeTipoHolder(Integer quantidade, Tipo tipo){
        this.quantidade = quantidade;
        this.tipo = tipo;
    }

    @Override
    public int compareTo(QuantidadeTipoHolder other) {
        return getQuantidade().compareTo(other.getQuantidade());
    }
}
