package model;

/** Contrato de qualquer entidade que possua identificador numérico (chave primária). */
public interface Identificavel {
    int getId();
    void setId(int id);
}
