package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.RegraNegocioException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConversorTest {
    @Test
    @DisplayName("converte inteiro válido, ignorando espaços")
    void inteiroValido() {
        assertEquals(12, Conversor.paraInteiroPositivo(" 12 ", "quantidade"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "", "  ", "1.5", "0", "-3"})
    @DisplayName("recusa entradas que não são inteiros positivos")
    void inteiroInvalido(String entrada) {
        assertThrows(RegraNegocioException.class, () -> Conversor.paraInteiroPositivo(entrada, "quantidade"));
    }

    @Test
    @DisplayName("texto nulo é recusado")
    void inteiroNulo() {
        assertThrows(RegraNegocioException.class, () -> Conversor.paraInteiroPositivo(null, "quantidade"));
    }

    @Test
    @DisplayName("a mensagem de erro cita o nome do campo")
    void mensagemCitaOCampo() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> Conversor.paraInteiroPositivo("abc", "quantidade"));
        assertTrue(erro.getMessage().contains("quantidade"));
    }

    @Test
    @DisplayName("converte data no formato dd/mm/aaaa")
    void dataValida() {
        assertEquals(LocalDate.of(2026, 10, 9), Conversor.paraData("09/10/2026", "data"));
    }

    @Test
    @DisplayName("29/02 só existe em ano bissexto")
    void anoBissexto() {
        assertEquals(LocalDate.of(2028, 2, 29), Conversor.paraData("29/02/2028", "data"));
        assertThrows(RegraNegocioException.class, () -> Conversor.paraData("29/02/2026", "data"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"31/02/2026", "2026-10-09", "9/10/2026", "32/01/2026", "", "abc"})
    @DisplayName("recusa datas inexistentes ou em outro formato")
    void dataInvalida(String entrada) {
        assertThrows(RegraNegocioException.class, () -> Conversor.paraData(entrada, "data"));
    }

    @Test
    @DisplayName("data nula é recusada")
    void dataNula() {
        assertThrows(RegraNegocioException.class, () -> Conversor.paraData(null, "data"));
    }
}
