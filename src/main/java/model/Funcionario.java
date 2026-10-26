package model;

/** Funcionário que colhe café (colhedor, encarregado etc.). */
public class Funcionario implements Identificavel {
    private int id;
    private String nome;
    private String funcao;
    private String contato;
    private boolean ativo;

    public Funcionario(String nome, String funcao, String contato) {
        this(0, nome, funcao, contato, true);
    }

    public Funcionario(int id, String nome, String funcao, String contato, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.funcao = funcao;
        this.contato = contato;
        this.ativo = ativo;
    }

    public void atualizarDados(String nome, String funcao, String contato) {
        this.nome = nome;
        this.funcao = funcao;
        this.contato = contato;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public String getFuncao() { return funcao; }
    public String getContato() { return contato; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return nome + " - " + funcao + (ativo ? "" : " (inativo)");
    }
}
