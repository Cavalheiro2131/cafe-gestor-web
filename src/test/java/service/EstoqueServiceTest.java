package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.RegraNegocioException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import model.MovimentacaoEstoque;
import model.TipoCafe;
import model.TipoMovimentacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.memory.MemoriaEstoqueRepository;
import repository.memory.MemoriaMovimentacaoRepository;
import repository.memory.MemoriaTipoCafeRepository;

/** Testa o serviço com repositórios em memória: nenhum acesso a banco de dados. */
class EstoqueServiceTest {
    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-10-09T12:00:00Z"), ZoneOffset.UTC);

    private EstoqueService servico;
    private int tipoId;

    @BeforeEach
    void preparar() {
        MemoriaTipoCafeRepository tipos = new MemoriaTipoCafeRepository();
        servico = new EstoqueService(new MemoriaEstoqueRepository(), new MemoriaMovimentacaoRepository(), tipos, RELOGIO);
        tipoId = tipos.salvar(new TipoCafe("Arábica", "Bourbon", null)).getId();
    }

    @Test
    @DisplayName("sem nenhuma entrada o saldo é zero")
    void saldoInicialZero() {
        assertEquals(0, servico.consultarSaldo(tipoId));
    }

    @Test
    @DisplayName("entrada cria o estoque e soma ao saldo")
    void entradaSomaAoSaldo() {
        servico.registrarEntrada(tipoId, 100, "Safra");
        assertEquals(100, servico.consultarSaldo(tipoId));
    }

    @Test
    @DisplayName("saída subtrai do saldo")
    void saidaSubtraiDoSaldo() {
        servico.registrarEntrada(tipoId, 100, null);
        servico.registrarSaida(tipoId, 30, "Venda");
        assertEquals(70, servico.consultarSaldo(tipoId));
    }

    @Test
    @DisplayName("saída maior que o saldo é recusada e não deixa rastro no histórico")
    void saidaMaiorQueSaldoNaoGrava() {
        servico.registrarEntrada(tipoId, 10, null);
        assertThrows(RegraNegocioException.class, () -> servico.registrarSaida(tipoId, 11, "Venda"));
        assertEquals(10, servico.consultarSaldo(tipoId));
        assertEquals(1, servico.historico(tipoId).size());
    }

    @Test
    @DisplayName("saída sem nenhuma entrada anterior é recusada")
    void saidaSemEntrada() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> servico.registrarSaida(tipoId, 1, "Venda"));
        assertTrue(erro.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("a saída exige destino")
    void saidaExigeDestino() {
        servico.registrarEntrada(tipoId, 10, null);
        assertThrows(RegraNegocioException.class, () -> servico.registrarSaida(tipoId, 1, "  "));
        assertThrows(RegraNegocioException.class, () -> servico.registrarSaida(tipoId, 1, null));
    }

    @Test
    @DisplayName("a nota da entrada é opcional")
    void notaDaEntradaOpcional() {
        servico.registrarEntrada(tipoId, 5, "   ");
        assertNull(servico.historico(tipoId).get(0).getDestino());
    }

    @Test
    @DisplayName("quantidade zero ou negativa é recusada")
    void quantidadeInvalida() {
        assertThrows(RegraNegocioException.class, () -> servico.registrarEntrada(tipoId, 0, null));
        assertThrows(RegraNegocioException.class, () -> servico.registrarSaida(tipoId, -2, "Venda"));
    }

    @Test
    @DisplayName("tipo de café inexistente é recusado")
    void tipoInexistente() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> servico.registrarEntrada(999, 1, null));
        assertTrue(erro.getMessage().contains("não encontrado"));
    }

    @Test
    @DisplayName("o histórico guarda tipo, quantidade, destino e a data do relógio injetado")
    void historicoCompleto() {
        servico.registrarEntrada(tipoId, 100, "Safra");
        servico.registrarSaida(tipoId, 30, "Venda");

        List<MovimentacaoEstoque> historico = servico.historico(tipoId);
        assertEquals(2, historico.size());
        assertEquals(TipoMovimentacao.ENTRADA, historico.get(0).getTipo());
        assertEquals(TipoMovimentacao.SAIDA, historico.get(1).getTipo());
        assertEquals(30, historico.get(1).getQuantidade());
        assertEquals("Venda", historico.get(1).getDestino());
        assertEquals(LocalDate.of(2026, 10, 9), historico.get(0).getData());
    }

    @Test
    @DisplayName("o saldo aparece na listagem geral")
    void saldoNaListagem() {
        servico.registrarEntrada(tipoId, 42, null);
        assertEquals(1, servico.listarSaldos().size());
        assertEquals(42, servico.listarSaldos().get(0).getQuantidadeSacas());
    }
}
