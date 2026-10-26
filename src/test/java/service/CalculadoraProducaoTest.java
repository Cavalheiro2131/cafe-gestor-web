package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CalculadoraProducaoTest {
    private CalculadoraProducao calculadora;

    @BeforeEach
    void preparar() {
        calculadora = new CalculadoraProducao(8); // exemplo: 8 balaios formam 1 saca
    }

    @Test
    @DisplayName("20 balaios rendem 2 sacas completas")
    void vinteBalaiosDuasSacas() {
        assertEquals(2, calculadora.sacasCompletas(20));
    }

    @Test
    @DisplayName("7 balaios ainda não formam uma saca (limite inferior)")
    void seteBalaiosNenhumaSaca() {
        assertEquals(0, calculadora.sacasCompletas(7));
    }

    @Test
    @DisplayName("8 balaios formam exatamente uma saca (valor limite)")
    void oitoBalaiosUmaSaca() {
        assertEquals(1, calculadora.sacasCompletas(8));
    }

    @Test
    @DisplayName("zero balaios não rendem nada")
    void zeroBalaios() {
        assertEquals(0, calculadora.sacasCompletas(0));
        assertEquals(0, calculadora.balaiosRestantes(0));
    }

    @Test
    @DisplayName("20 balaios deixam 4 de sobra")
    void sobraDeVinteBalaios() {
        assertEquals(4, calculadora.balaiosRestantes(20));
    }

    @Test
    @DisplayName("múltiplo exato não deixa sobra")
    void multiploExatoSemSobra() {
        assertEquals(0, calculadora.balaiosRestantes(16));
    }

    @Test
    @DisplayName("sacas * balaios por saca + sobra reconstitui o total")
    void sacasESobraReconstituemOTotal() {
        for (int balaios = 0; balaios <= 100; balaios++) {
            int reconstruido = calculadora.sacasCompletas(balaios) * 8 + calculadora.balaiosRestantes(balaios);
            assertEquals(balaios, reconstruido);
        }
    }

    @Test
    @DisplayName("com 1 balaio por saca, cada balaio é uma saca")
    void umBalaioPorSaca() {
        CalculadoraProducao um = new CalculadoraProducao(1);
        assertEquals(13, um.sacasCompletas(13));
        assertEquals(0, um.balaiosRestantes(13));
    }

    @Test
    @DisplayName("quantidade negativa de balaios é recusada")
    void balaiosNegativosSaoRecusados() {
        assertThrows(RegraNegocioException.class, () -> calculadora.sacasCompletas(-1));
        assertThrows(RegraNegocioException.class, () -> calculadora.balaiosRestantes(-1));
    }

    @Test
    @DisplayName("balaios por saca zero ou negativo é recusado na criação")
    void configuracaoInvalida() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> new CalculadoraProducao(0));
        assertTrue(erro.getMessage().contains("balaios por saca"));
        assertThrows(RegraNegocioException.class, () -> new CalculadoraProducao(-8));
    }

    @Test
    @DisplayName("média por dia: 35 balaios em 7 dias = 5,0")
    void mediaExata() {
        assertEquals(5.0, calculadora.mediaPorDia(35, 7), 0.0001);
    }

    @Test
    @DisplayName("média por dia com casa decimal: 10 balaios em 4 dias = 2,5")
    void mediaComDecimal() {
        assertEquals(2.5, calculadora.mediaPorDia(10, 4), 0.0001);
    }

    @Test
    @DisplayName("média de zero balaios é zero")
    void mediaDeZero() {
        assertEquals(0.0, calculadora.mediaPorDia(0, 5), 0.0001);
    }

    @Test
    @DisplayName("média com zero dias ou total negativo é recusada")
    void mediaInvalida() {
        assertThrows(RegraNegocioException.class, () -> calculadora.mediaPorDia(10, 0));
        assertThrows(RegraNegocioException.class, () -> calculadora.mediaPorDia(-1, 5));
    }
}
