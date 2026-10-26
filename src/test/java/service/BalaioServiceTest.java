package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.RegraNegocioException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import model.Balaio;
import model.Funcionario;
import model.TipoCafe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.memory.MemoriaBalaioRepository;
import repository.memory.MemoriaFuncionarioRepository;
import repository.memory.MemoriaTipoCafeRepository;

class BalaioServiceTest {
    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-10-09T12:00:00Z"), ZoneOffset.UTC);
    private static final LocalDate HOJE = LocalDate.of(2026, 10, 9);

    private MemoriaFuncionarioRepository funcionarios;
    private BalaioService servico;
    private Funcionario maria;
    private TipoCafe tipo;

    @BeforeEach
    void preparar() {
        funcionarios = new MemoriaFuncionarioRepository();
        MemoriaTipoCafeRepository tipos = new MemoriaTipoCafeRepository();
        servico = new BalaioService(new MemoriaBalaioRepository(), funcionarios, tipos, RELOGIO);
        maria = funcionarios.salvar(new Funcionario("Maria", "Colhedora", "99999-0000"));
        tipo = tipos.salvar(new TipoCafe("Arábica", null, null));
    }

    @Test
    @DisplayName("registra um balaio válido e devolve com id")
    void registraBalaioValido() {
        Balaio balaio = servico.registrar(maria.getId(), tipo.getId(), 20, HOJE);
        assertTrue(balaio.getId() > 0);
        assertEquals(20, balaio.getQuantidade());
        assertEquals(1, servico.listar().size());
    }

    @Test
    @DisplayName("aceita a data de hoje (valor limite)")
    void aceitaDataDeHoje() {
        servico.registrar(maria.getId(), tipo.getId(), 1, HOJE);
        assertEquals(1, servico.listar().size());
    }

    @Test
    @DisplayName("data futura é recusada")
    void recusaDataFutura() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> servico.registrar(maria.getId(), tipo.getId(), 5, HOJE.plusDays(1)));
        assertTrue(erro.getMessage().contains("futuro"));
    }

    @Test
    @DisplayName("data ausente é recusada")
    void recusaDataAusente() {
        assertThrows(RegraNegocioException.class, () -> servico.registrar(maria.getId(), tipo.getId(), 5, null));
    }

    @Test
    @DisplayName("quantidade zero ou negativa é recusada")
    void recusaQuantidadeInvalida() {
        assertThrows(RegraNegocioException.class, () -> servico.registrar(maria.getId(), tipo.getId(), 0, HOJE));
        assertThrows(RegraNegocioException.class, () -> servico.registrar(maria.getId(), tipo.getId(), -4, HOJE));
    }

    @Test
    @DisplayName("funcionário inexistente é recusado")
    void recusaFuncionarioInexistente() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> servico.registrar(999, tipo.getId(), 5, HOJE));
        assertTrue(erro.getMessage().contains("Funcionário não encontrado"));
    }

    @Test
    @DisplayName("tipo de café inexistente é recusado")
    void recusaTipoInexistente() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> servico.registrar(maria.getId(), 999, 5, HOJE));
        assertTrue(erro.getMessage().contains("Tipo de café não encontrado"));
    }

    @Test
    @DisplayName("funcionário inativo não registra balaio")
    void recusaFuncionarioInativo() {
        maria.setAtivo(false);
        funcionarios.salvar(maria);
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> servico.registrar(maria.getId(), tipo.getId(), 5, HOJE));
        assertTrue(erro.getMessage().contains("inativo"));
        assertEquals(0, servico.listar().size());
    }

    @Test
    @DisplayName("o total soma só os balaios do próprio funcionário")
    void totalPorFuncionario() {
        Funcionario joao = funcionarios.salvar(new Funcionario("João", "Colhedor", "88888-0000"));
        servico.registrar(maria.getId(), tipo.getId(), 20, HOJE);
        servico.registrar(maria.getId(), tipo.getId(), 15, HOJE.minusDays(1));
        servico.registrar(joao.getId(), tipo.getId(), 7, HOJE);

        assertEquals(35, servico.totalDoFuncionario(maria.getId()));
        assertEquals(7, servico.totalDoFuncionario(joao.getId()));
    }

    @Test
    @DisplayName("funcionário sem registros tem total zero")
    void totalSemRegistros() {
        assertEquals(0, servico.totalDoFuncionario(maria.getId()));
    }
}
