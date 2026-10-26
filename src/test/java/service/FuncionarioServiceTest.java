package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.RegraNegocioException;
import model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.memory.MemoriaFuncionarioRepository;

class FuncionarioServiceTest {
    private FuncionarioService servico;

    @BeforeEach
    void preparar() {
        servico = new FuncionarioService(new MemoriaFuncionarioRepository());
    }

    @Test
    @DisplayName("cadastra, remove espaços das pontas e gera id")
    void cadastraComTrim() {
        Funcionario f = servico.cadastrar("  Maria  ", "Colhedora", "99999-0000");
        assertTrue(f.getId() > 0);
        assertEquals("Maria", f.getNome());
        assertTrue(f.isAtivo());
    }

    @Test
    @DisplayName("nome, função e contato são obrigatórios")
    void camposObrigatorios() {
        assertThrows(RegraNegocioException.class, () -> servico.cadastrar("", "Colhedora", "1"));
        assertThrows(RegraNegocioException.class, () -> servico.cadastrar("Maria", "  ", "1"));
        assertThrows(RegraNegocioException.class, () -> servico.cadastrar("Maria", "Colhedora", null));
    }

    @Test
    @DisplayName("editar altera os dados do funcionário")
    void edita() {
        Funcionario f = servico.cadastrar("Maria", "Colhedora", "99999-0000");
        servico.editar(f.getId(), "Maria Silva", "Encarregada", "88888-0000");
        Funcionario atualizado = servico.buscar(f.getId());
        assertEquals("Maria Silva", atualizado.getNome());
        assertEquals("Encarregada", atualizado.getFuncao());
    }

    @Test
    @DisplayName("editar ou buscar funcionário inexistente é recusado")
    void inexistente() {
        assertThrows(RegraNegocioException.class, () -> servico.editar(999, "A", "B", "C"));
        assertThrows(RegraNegocioException.class, () -> servico.buscar(999));
    }

    @Test
    @DisplayName("inativar tira da lista de ativos e reativar devolve")
    void inativaEReativa() {
        Funcionario f = servico.cadastrar("Maria", "Colhedora", "1");
        servico.inativar(f.getId());
        assertFalse(servico.buscar(f.getId()).isAtivo());
        assertEquals(0, servico.listarAtivos().size());
        assertEquals(1, servico.listar().size());

        servico.reativar(f.getId());
        assertEquals(1, servico.listarAtivos().size());
    }
}
