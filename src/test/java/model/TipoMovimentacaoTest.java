package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TipoMovimentacaoTest {
    private Estoque estoque;

    @BeforeEach
    void preparar() {
        estoque = new Estoque(new TipoCafe(1, "Conilon", null, null), 20);
    }

    @Test
    @DisplayName("ENTRADA aumenta o saldo")
    void entradaAumentaSaldo() {
        TipoMovimentacao.ENTRADA.aplicar(estoque, 7);
        assertEquals(27, estoque.getQuantidadeSacas());
    }

    @Test
    @DisplayName("SAIDA diminui o saldo")
    void saidaDiminuiSaldo() {
        TipoMovimentacao.SAIDA.aplicar(estoque, 7);
        assertEquals(13, estoque.getQuantidadeSacas());
    }

    @Test
    @DisplayName("SAIDA acima do saldo é recusada")
    void saidaAcimaDoSaldoELancada() {
        assertThrows(RegraNegocioException.class, () -> TipoMovimentacao.SAIDA.aplicar(estoque, 21));
        assertEquals(20, estoque.getQuantidadeSacas());
    }

    @Test
    @DisplayName("existem exatamente os tipos ENTRADA e SAIDA")
    void tiposExistentes() {
        assertEquals(2, TipoMovimentacao.values().length);
    }
}
