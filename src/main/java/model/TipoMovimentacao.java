package model;

/**
 * Tipo de movimentação do estoque. Cada constante sabe como se aplicar a um {@link Estoque},
 * o que elimina o if/else por tipo no serviço (Open/Closed: novo tipo = nova constante).
 */
public enum TipoMovimentacao {
    ENTRADA {
        @Override
        public void aplicar(Estoque estoque, int quantidade) {
            estoque.adicionar(quantidade);
        }
    },
    SAIDA {
        @Override
        public void aplicar(Estoque estoque, int quantidade) {
            estoque.retirar(quantidade);
        }
    };

    public abstract void aplicar(Estoque estoque, int quantidade);
}
