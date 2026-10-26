package model;

/** Tipo/variedade de café colhido e armazenado. */
public class TipoCafe implements Identificavel {
    private int id;
    private final String nome;
    private final String variedade;
    private final String observacoes;

    public TipoCafe(String nome, String variedade, String observacoes) {
        this(0, nome, variedade, observacoes);
    }

    public TipoCafe(int id, String nome, String variedade, String observacoes) {
        this.id = id;
        this.nome = nome;
        this.variedade = variedade;
        this.observacoes = observacoes;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public String getVariedade() { return variedade; }
    public String getObservacoes() { return observacoes; }

    @Override
    public String toString() {
        return nome + (variedade == null ? "" : " (" + variedade + ")");
    }
}
