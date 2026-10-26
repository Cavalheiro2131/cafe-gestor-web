package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidacoesTest {
    @Test
    @DisplayName("texto obrigatório remove espaços das pontas")
    void textoObrigatorioRemoveEspacos() {
        assertEquals("Maria", Validacoes.textoObrigatorio("  Maria  ", "nome", 100));
    }

    @Test
    @DisplayName("texto obrigatório recusa nulo, vazio e só espaços")
    void textoObrigatorioRecusaVazios() {
        assertThrows(RegraNegocioException.class, () -> Validacoes.textoObrigatorio(null, "nome", 100));
        assertThrows(RegraNegocioException.class, () -> Validacoes.textoObrigatorio("", "nome", 100));
        assertThrows(RegraNegocioException.class, () -> Validacoes.textoObrigatorio("   ", "nome", 100));
    }

    @Test
    @DisplayName("a mensagem cita o campo")
    void mensagemCitaOCampo() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> Validacoes.textoObrigatorio("", "função", 50));
        assertTrue(erro.getMessage().contains("função"));
    }

    @Test
    @DisplayName("tamanho máximo: exatamente no limite passa, um a mais não")
    void tamanhoMaximo() {
        assertEquals("abcde", Validacoes.textoObrigatorio("abcde", "campo", 5));
        assertThrows(RegraNegocioException.class, () -> Validacoes.textoObrigatorio("abcdef", "campo", 5));
    }

    @Test
    @DisplayName("texto opcional devolve null quando vazio ou nulo")
    void textoOpcionalVazio() {
        assertNull(Validacoes.textoOpcional(null, "nota", 10));
        assertNull(Validacoes.textoOpcional("   ", "nota", 10));
    }

    @Test
    @DisplayName("texto opcional preenchido é aparado e respeita o limite")
    void textoOpcionalPreenchido() {
        assertEquals("ok", Validacoes.textoOpcional(" ok ", "nota", 10));
        assertThrows(RegraNegocioException.class, () -> Validacoes.textoOpcional("12345678901", "nota", 10));
    }

    @Test
    @DisplayName("inteiro positivo: 1 passa, 0 e negativos não")
    void inteiroPositivo() {
        assertEquals(1, Validacoes.inteiroPositivo(1, "quantidade"));
        assertThrows(RegraNegocioException.class, () -> Validacoes.inteiroPositivo(0, "quantidade"));
        assertThrows(RegraNegocioException.class, () -> Validacoes.inteiroPositivo(-5, "quantidade"));
    }
}
