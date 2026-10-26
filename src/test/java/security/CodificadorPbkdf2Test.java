package security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CodificadorPbkdf2Test {
    private CodificadorPbkdf2 codificador;

    @BeforeEach
    void preparar() {
        codificador = new CodificadorPbkdf2();
    }

    @Test
    @DisplayName("a senha codificada não contém a senha original")
    void naoGuardaTextoPuro() {
        String codificada = codificador.codificar("segredo123");
        assertFalse(codificada.contains("segredo123"));
        assertTrue(codificada.startsWith("pbkdf2$"));
    }

    @Test
    @DisplayName("a senha correta confere e a errada não")
    void conferenciaCorretaEIncorreta() {
        String codificada = codificador.codificar("segredo123");
        assertTrue(codificador.confere("segredo123", codificada));
        assertFalse(codificador.confere("Segredo123", codificada));
        assertFalse(codificador.confere("", codificada));
    }

    @Test
    @DisplayName("a mesma senha gera códigos diferentes (sal aleatório), e ambos conferem")
    void salAleatorio() {
        String primeira = codificador.codificar("segredo123");
        String segunda = codificador.codificar("segredo123");
        assertNotEquals(primeira, segunda);
        assertTrue(codificador.confere("segredo123", primeira));
        assertTrue(codificador.confere("segredo123", segunda));
    }

    @Test
    @DisplayName("valores guardados em formato inválido nunca conferem")
    void formatoInvalido() {
        assertFalse(codificador.confere("x", "texto-qualquer"));
        assertFalse(codificador.confere("x", ""));
        assertFalse(codificador.confere("x", "pbkdf2$abc$AAAA$AAAA"));
        assertFalse(codificador.confere("x", "pbkdf2$65536$@@@$###"));
        assertFalse(codificador.confere("x", "md5$65536$AAAA$AAAA"));
    }

    @Test
    @DisplayName("entradas nulas não conferem e não lançam exceção")
    void entradasNulas() {
        assertFalse(codificador.confere(null, "pbkdf2$65536$AAAA$AAAA"));
        assertFalse(codificador.confere("x", null));
    }
}
