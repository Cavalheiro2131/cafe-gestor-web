package model;

import java.time.LocalDate;

/** Registro de produção: quantos balaios um funcionário colheu de um tipo de café em uma data. */
public class Balaio implements Identificavel {
    private int id;
    private final Funcionario funcionario;
    private final TipoCafe tipoCafe;
    private final int quantidade;
    private final LocalDate data;

    public Balaio(Funcionario funcionario, TipoCafe tipoCafe, int quantidade, LocalDate data) {
        this(0, funcionario, tipoCafe, quantidade, data);
    }

    public Balaio(int id, Funcionario funcionario, TipoCafe tipoCafe, int quantidade, LocalDate data) {
        this.id = id;
        this.funcionario = funcionario;
        this.tipoCafe = tipoCafe;
        this.quantidade = quantidade;
        this.data = data;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }
    public Funcionario getFuncionario() { return funcionario; }
    public TipoCafe getTipoCafe() { return tipoCafe; }
    public int getQuantidade() { return quantidade; }
    public LocalDate getData() { return data; }
}
