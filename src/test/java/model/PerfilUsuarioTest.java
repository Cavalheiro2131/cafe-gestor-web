package model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PerfilUsuarioTest {
    @Test
    @DisplayName("ADMIN pode alterar dados")
    void adminAltera() {
        assertTrue(PerfilUsuario.ADMIN.podeAlterarDados());
    }

    @Test
    @DisplayName("GERENTE pode alterar dados")
    void gerenteAltera() {
        assertTrue(PerfilUsuario.GERENTE.podeAlterarDados());
    }

    @Test
    @DisplayName("OPERADOR só consulta")
    void operadorSoConsulta() {
        assertFalse(PerfilUsuario.OPERADOR.podeAlterarDados());
    }
}
