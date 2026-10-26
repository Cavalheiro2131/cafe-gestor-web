package model;

import exception.RegraNegocioException;

/**
 * Saldo de sacas de um tipo de café. As invariantes do estoque (quantidade positiva e saldo
 * nunca negativo) ficam aqui, no próprio objeto de domínio, e não na tela.
 */
public class Estoque implements Identificavel {
    private int id;
    private final TipoCafe tipoCafe;
    private int quantidadeSacas;

    public Estoque(TipoCafe tipoCafe, int quantidadeSacas) {
        this(0, tipoCafe, quantidadeSacas);
    }

    public Estoque(int id, TipoCafe tipoCafe, int quantidadeSacas) {
        this.id = id;
        this.tipoCafe = tipoCafe;
        this.quantidadeSacas = quantidadeSacas;
    }

    public void adicionar(int quantidade) {
        exigirQuantidadePositiva(quantidade);
        quantidadeSacas += quantidade;
    }

    public void retirar(int quantidade) {
        exigirQuantidadePositiva(quantidade);
        if (quantidade > quantidadeSacas) {
            throw new RegraNegocioException("Estoque insuficiente de " + tipoCafe.getNome()
                    + ": saldo de " + quantidadeSacas + " saca(s), saída solicitada de " + quantidade + ".");
        }
        quantidadeSacas -= quantidade;
    }

    private static void exigirQuantidadePositiva(int quantidade) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }
    public TipoCafe getTipoCafe() { return tipoCafe; }
    public int getQuantidadeSacas() { return quantidadeSacas; }
}
