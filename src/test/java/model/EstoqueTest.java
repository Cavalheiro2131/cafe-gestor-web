package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EstoqueTest {
    private Estoque estoque;

    @BeforeEach
    void preparar() {
        estoque = new Estoque(new TipoCafe(1, "Arábica", "Bourbon", null), 10);
    }

    @Test
    @DisplayName("adicionar soma a quantidade ao saldo")
    void adicionarSomaAoSaldo() {
        estoque.adicionar(5);
        assertEquals(15, estoque.getQuantidadeSacas());
    }

    @Test
    @DisplayName("retirar subtrai a quantidade do saldo")
    void retirarSubtraiDoSaldo() {
        estoque.retirar(4);
        assertEquals(6, estoque.getQuantidadeSacas());
    }

    @Test
    @DisplayName("retirar exatamente o saldo zera o estoque (valor limite)")
    void retirarTodoOSaldoZera() {
        estoque.retirar(10);
        assertEquals(0, estoque.getQuantidadeSacas());
    }

    @Test
    @DisplayName("retirar mais que o saldo lança exceção e não altera o saldo")
    void retirarMaisQueOSaldoLancaEMantemSaldo() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> estoque.retirar(11));
        assertTrue(erro.getMessage().contains("Estoque insuficiente"));
        assertEquals(10, estoque.getQuantidadeSacas());
    }

    @Test
    @DisplayName("a mensagem de estoque insuficiente informa saldo e quantidade pedida")
    void mensagemInformaSaldoEPedido() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> estoque.retirar(11));
        assertTrue(erro.getMessage().contains("10"));
        assertTrue(erro.getMessage().contains("11"));
        assertTrue(erro.getMessage().contains("Arábica"));
    }

    @Test
    @DisplayName("quantidade zero ou negativa é recusada na entrada")
    void entradaInvalidaELancada() {
        assertThrows(RegraNegocioException.class, () -> estoque.adicionar(0));
        assertThrows(RegraNegocioException.class, () -> estoque.adicionar(-1));
        assertEquals(10, estoque.getQuantidadeSacas());
    }

    @Test
    @DisplayName("quantidade zero ou negativa é recusada na saída")
    void saidaInvalidaELancada() {
        assertThrows(RegraNegocioException.class, () -> estoque.retirar(0));
        assertThrows(RegraNegocioException.class, () -> estoque.retirar(-1));
        assertEquals(10, estoque.getQuantidadeSacas());
    }
}
