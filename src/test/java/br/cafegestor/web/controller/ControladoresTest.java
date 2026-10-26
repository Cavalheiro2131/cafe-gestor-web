package br.cafegestor.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.cafegestor.web.dto.BalaioRequest;
import br.cafegestor.web.dto.BalaioResposta;
import br.cafegestor.web.dto.FuncionarioRequest;
import br.cafegestor.web.dto.FuncionarioResposta;
import br.cafegestor.web.dto.MovimentacaoRequest;
import br.cafegestor.web.dto.MovimentacaoResposta;
import br.cafegestor.web.dto.NovoSaldoResposta;
import br.cafegestor.web.dto.ResumoResposta;
import br.cafegestor.web.dto.SaldoResposta;
import br.cafegestor.web.dto.SimulacaoResposta;
import br.cafegestor.web.dto.StatusRequest;
import br.cafegestor.web.dto.TipoCafeRequest;
import br.cafegestor.web.dto.TipoCafeResposta;
import exception.RegraNegocioException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import repository.memory.MemoriaBalaioRepository;
import repository.memory.MemoriaEstoqueRepository;
import repository.memory.MemoriaFuncionarioRepository;
import repository.memory.MemoriaMovimentacaoRepository;
import repository.memory.MemoriaTipoCafeRepository;
import service.BalaioService;
import service.EstoqueService;
import service.FuncionarioService;
import service.TipoCafeService;

/**
 * Testa os controladores chamando os métodos diretamente (sem subir o Spring), com repositórios em memória:
 * verifica status HTTP, formato das respostas (DTOs) e as regras do núcleo expostas pela API.
 */
class ControladoresTest {
    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-10-21T12:00:00Z"), ZoneOffset.UTC);
    private static final LocalDate HOJE = LocalDate.of(2026, 10, 21);

    private FuncionarioController funcionarios;
    private TipoCafeController tipos;
    private BalaioController balaios;
    private EstoqueController estoque;
    private ProducaoController producao;
    private ResumoController resumo;

    @BeforeEach
    void preparar() {
        MemoriaFuncionarioRepository repoFuncionarios = new MemoriaFuncionarioRepository();
        MemoriaTipoCafeRepository repoTipos = new MemoriaTipoCafeRepository();
        FuncionarioService servicoFuncionarios = new FuncionarioService(repoFuncionarios);
        TipoCafeService servicoTipos = new TipoCafeService(repoTipos);
        BalaioService servicoBalaios = new BalaioService(new MemoriaBalaioRepository(), repoFuncionarios, repoTipos, RELOGIO);
        EstoqueService servicoEstoque = new EstoqueService(new MemoriaEstoqueRepository(),
                new MemoriaMovimentacaoRepository(), repoTipos, RELOGIO);

        funcionarios = new FuncionarioController(servicoFuncionarios);
        tipos = new TipoCafeController(servicoTipos);
        balaios = new BalaioController(servicoBalaios);
        estoque = new EstoqueController(servicoEstoque, servicoTipos);
        producao = new ProducaoController();
        resumo = new ResumoController(servicoFuncionarios, servicoBalaios, servicoEstoque, servicoTipos);
    }

    private FuncionarioResposta novoFuncionario(String nome) {
        return funcionarios.criar(new FuncionarioRequest(nome, "Colhedor", "99999-0000")).getBody();
    }

    private TipoCafeResposta novoTipo(String nome) {
        return tipos.criar(new TipoCafeRequest(nome, "Especial", null)).getBody();
    }

    // ---------------------------------------------------------------- funcionários
    @Test
    @DisplayName("POST funcionário: 201 e o funcionário criado, ativo")
    void criaFuncionario() {
        ResponseEntity<FuncionarioResposta> r = funcionarios.criar(new FuncionarioRequest("  Maria  ", "Colhedora", "98888-7777"));
        assertEquals(201, r.getStatusCode().value());
        assertTrue(r.getBody().id() > 0);
        assertEquals("Maria", r.getBody().nome());
        assertTrue(r.getBody().ativo());
    }

    @Test
    @DisplayName("POST funcionário sem nome: regra de negócio violada (vira 422 no handler)")
    void funcionarioSemNome() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> funcionarios.criar(new FuncionarioRequest("", "Colhedor", "1")));
        assertTrue(erro.getMessage().contains("nome"));
    }

    @Test
    @DisplayName("PUT funcionário altera os dados; PUT em id inexistente é recusado")
    void atualizaFuncionario() {
        int id = novoFuncionario("Maria").id();
        FuncionarioResposta atualizado = funcionarios.atualizar(id, new FuncionarioRequest("Maria Silva", "Encarregada", "1"));
        assertEquals("Encarregada", atualizado.funcao());
        assertEquals("Maria Silva", funcionarios.buscar(id).nome());
        assertThrows(RegraNegocioException.class, () -> funcionarios.atualizar(999, new FuncionarioRequest("A", "B", "C")));
    }

    @Test
    @DisplayName("PATCH status inativa e reativa; a lista traz os dois")
    void alteraStatus() {
        int id = novoFuncionario("Maria").id();
        assertFalse(funcionarios.alterarStatus(id, new StatusRequest(false)).ativo());
        assertEquals(1, funcionarios.listar().size());
        assertFalse(funcionarios.listar().get(0).ativo());
        assertTrue(funcionarios.alterarStatus(id, new StatusRequest(true)).ativo());
    }

    @Test
    @DisplayName("GET funcionário inexistente é recusado")
    void buscaInexistente() {
        assertThrows(RegraNegocioException.class, () -> funcionarios.buscar(999));
    }

    // ---------------------------------------------------------------- tipos de café
    @Test
    @DisplayName("POST tipo de café: 201; variedade e observações vazias viram null")
    void criaTipo() {
        ResponseEntity<TipoCafeResposta> r = tipos.criar(new TipoCafeRequest("Arábica", "", "  "));
        assertEquals(201, r.getStatusCode().value());
        assertEquals("Arábica", r.getBody().nome());
        assertNull(r.getBody().variedade());
        assertNull(r.getBody().observacoes());
        assertEquals(1, tipos.listar().size());
    }

    @Test
    @DisplayName("POST tipo de café sem nome é recusado")
    void tipoSemNome() {
        assertThrows(RegraNegocioException.class, () -> tipos.criar(new TipoCafeRequest(" ", null, null)));
    }

    // ---------------------------------------------------------------- balaios
    @Test
    @DisplayName("POST balaio: 201, data em formato ISO e nomes resolvidos")
    void registraBalaio() {
        int f = novoFuncionario("Maria").id();
        int t = novoTipo("Arábica").id();
        ResponseEntity<BalaioResposta> r = balaios.registrar(new BalaioRequest(f, t, 20, "2026-10-20"));
        assertEquals(201, r.getStatusCode().value());
        assertEquals("2026-10-20", r.getBody().data());
        assertEquals("Maria", r.getBody().funcionarioNome());
        assertEquals("Arábica", r.getBody().tipoCafeNome());
        assertEquals(1, balaios.listar().size());
    }

    @Test
    @DisplayName("POST balaio: data inválida, ausente ou futura é recusada")
    void balaioDataInvalida() {
        int f = novoFuncionario("Maria").id();
        int t = novoTipo("Arábica").id();
        assertTrue(assertThrows(RegraNegocioException.class,
                () -> balaios.registrar(new BalaioRequest(f, t, 5, "31/02/2026"))).getMessage().contains("data válida"));
        assertTrue(assertThrows(RegraNegocioException.class,
                () -> balaios.registrar(new BalaioRequest(f, t, 5, null))).getMessage().contains("Informe a data"));
        assertTrue(assertThrows(RegraNegocioException.class,
                () -> balaios.registrar(new BalaioRequest(f, t, 5, HOJE.plusDays(1).toString()))).getMessage().contains("futuro"));
    }

    @Test
    @DisplayName("POST balaio: quantidade zero e funcionário inativo são recusados")
    void balaioRegras() {
        int f = novoFuncionario("Maria").id();
        int t = novoTipo("Arábica").id();
        assertThrows(RegraNegocioException.class, () -> balaios.registrar(new BalaioRequest(f, t, 0, HOJE.toString())));
        funcionarios.alterarStatus(f, new StatusRequest(false));
        assertTrue(assertThrows(RegraNegocioException.class,
                () -> balaios.registrar(new BalaioRequest(f, t, 5, HOJE.toString()))).getMessage().contains("inativo"));
    }

    // ---------------------------------------------------------------- estoque
    @Test
    @DisplayName("POST movimentação: entrada soma, saída subtrai e devolve o novo saldo")
    void movimentaEstoque() {
        int t = novoTipo("Arábica").id();
        ResponseEntity<NovoSaldoResposta> entrada = estoque.movimentar(new MovimentacaoRequest("ENTRADA", t, 100, "Safra"));
        assertEquals(201, entrada.getStatusCode().value());
        assertEquals(100, entrada.getBody().novoSaldo());
        assertEquals(70, estoque.movimentar(new MovimentacaoRequest("saida", t, 30, "Venda")).getBody().novoSaldo());
    }

    @Test
    @DisplayName("POST movimentação: saída acima do saldo, sem destino e tipo inválido são recusados")
    void estoqueRegras() {
        int t = novoTipo("Arábica").id();
        estoque.movimentar(new MovimentacaoRequest("ENTRADA", t, 10, null));
        assertTrue(assertThrows(RegraNegocioException.class,
                () -> estoque.movimentar(new MovimentacaoRequest("SAIDA", t, 11, "Venda"))).getMessage().contains("Estoque insuficiente"));
        assertTrue(assertThrows(RegraNegocioException.class,
                () -> estoque.movimentar(new MovimentacaoRequest("SAIDA", t, 1, " "))).getMessage().contains("destino"));
        assertTrue(assertThrows(RegraNegocioException.class,
                () -> estoque.movimentar(new MovimentacaoRequest("TRANSFERENCIA", t, 1, "x"))).getMessage().contains("Tipo de movimentação inválido"));
        assertThrows(RegraNegocioException.class, () -> estoque.movimentar(new MovimentacaoRequest(null, t, 1, "x")));
        assertEquals(10, estoque.saldos().get(0).sacas());
    }

    @Test
    @DisplayName("GET saldos lista todos os tipos, inclusive os sem estoque (saldo 0)")
    void saldosComZero() {
        int a = novoTipo("Arábica").id();
        novoTipo("Conilon");
        estoque.movimentar(new MovimentacaoRequest("ENTRADA", a, 40, null));
        List<SaldoResposta> saldos = estoque.saldos();
        assertEquals(2, saldos.size());
        assertEquals(40, saldos.get(0).sacas());
        assertEquals(0, saldos.get(1).sacas());
    }

    @Test
    @DisplayName("GET histórico: mais recente primeiro, com destino e data")
    void historico() {
        int t = novoTipo("Arábica").id();
        estoque.movimentar(new MovimentacaoRequest("ENTRADA", t, 100, "Safra"));
        estoque.movimentar(new MovimentacaoRequest("SAIDA", t, 30, "Venda"));
        List<MovimentacaoResposta> historico = estoque.historico();
        assertEquals(2, historico.size());
        assertEquals("SAIDA", historico.get(0).tipo());
        assertEquals("Venda", historico.get(0).descricao());
        assertEquals("ENTRADA", historico.get(1).tipo());
        assertEquals("2026-10-21", historico.get(0).data());
    }

    // ---------------------------------------------------------------- produção e resumo
    @Test
    @DisplayName("GET simulação usa a CalculadoraProducao: 20 balaios, 8 por saca = 2 sacas e sobra 4")
    void simulacao() {
        SimulacaoResposta r = producao.simular(20, 8);
        assertEquals(2, r.sacasCompletas());
        assertEquals(4, r.balaiosRestantes());
        assertEquals(20, r.balaios());
    }

    @Test
    @DisplayName("GET simulação: balaios por saca zero e balaios negativos são recusados")
    void simulacaoInvalida() {
        assertThrows(RegraNegocioException.class, () -> producao.simular(10, 0));
        assertThrows(RegraNegocioException.class, () -> producao.simular(-1, 8));
    }

    @Test
    @DisplayName("GET resumo soma funcionários ativos, balaios e sacas")
    void resumoGeral() {
        int f1 = novoFuncionario("Maria").id();
        int f2 = novoFuncionario("João").id();
        funcionarios.alterarStatus(f2, new StatusRequest(false));
        int t = novoTipo("Arábica").id();
        balaios.registrar(new BalaioRequest(f1, t, 20, HOJE.toString()));
        balaios.registrar(new BalaioRequest(f1, t, 15, HOJE.toString()));
        estoque.movimentar(new MovimentacaoRequest("ENTRADA", t, 50, null));

        ResumoResposta r = resumo.resumo();
        assertEquals(1, r.funcionariosAtivos());
        assertEquals(35, r.balaiosRegistrados());
        assertEquals(50, r.sacasEmEstoque());
    }
}
