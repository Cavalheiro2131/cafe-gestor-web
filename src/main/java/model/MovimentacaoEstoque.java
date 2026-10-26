package model;

import java.time.LocalDate;

/** Histórico de uma entrada ou saída de sacas. O campo {@code destino} guarda o destino (saída) ou a nota (entrada). */
public class MovimentacaoEstoque implements Identificavel {
    private int id;
    private final TipoMovimentacao tipo;
    private final int quantidade;
    private final LocalDate data;
    private final String destino;
    private final TipoCafe tipoCafe;

    public MovimentacaoEstoque(TipoMovimentacao tipo, int quantidade, LocalDate data, String destino, TipoCafe tipoCafe) {
        this(0, tipo, quantidade, data, destino, tipoCafe);
    }

    public MovimentacaoEstoque(int id, TipoMovimentacao tipo, int quantidade, LocalDate data, String destino, TipoCafe tipoCafe) {
        this.id = id;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.data = data;
        this.destino = destino;
        this.tipoCafe = tipoCafe;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }
    public TipoMovimentacao getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public LocalDate getData() { return data; }
    public String getDestino() { return destino; }
    public TipoCafe getTipoCafe() { return tipoCafe; }
}
