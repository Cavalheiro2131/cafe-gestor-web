package br.cafegestor.web.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.RegraNegocioException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import model.PerfilUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.memory.MemoriaBalaioRepository;
import repository.memory.MemoriaEstoqueRepository;
import repository.memory.MemoriaFuncionarioRepository;
import repository.memory.MemoriaMovimentacaoRepository;
import repository.memory.MemoriaTipoCafeRepository;
import repository.memory.MemoriaUsuarioRepository;
import security.CodificadorPbkdf2;
import service.AutenticacaoService;
import service.BalaioService;
import service.EstoqueService;
import service.FuncionarioService;
import service.TipoCafeService;

class SementeDemonstracaoTest {
    private AutenticacaoService autenticacao;
    private FuncionarioService funcionarios;
    private TipoCafeService tipos;
    private BalaioService balaios;
    private EstoqueService estoque;

    @BeforeEach
    void semear() {
        Clock relogio = Clock.fixed(Instant.parse("2026-10-21T12:00:00Z"), ZoneOffset.UTC);
        MemoriaFuncionarioRepository repoFuncionarios = new MemoriaFuncionarioRepository();
        MemoriaTipoCafeRepository repoTipos = new MemoriaTipoCafeRepository();
        autenticacao = new AutenticacaoService(new MemoriaUsuarioRepository(), new CodificadorPbkdf2());
        funcionarios = new FuncionarioService(repoFuncionarios);
        tipos = new TipoCafeService(repoTipos);
        balaios = new BalaioService(new MemoriaBalaioRepository(), repoFuncionarios, repoTipos, relogio);
        estoque = new EstoqueService(new MemoriaEstoqueRepository(), new MemoriaMovimentacaoRepository(), repoTipos, relogio);

        new SementeDemonstracao(autenticacao, funcionarios, tipos, balaios, estoque, relogio).run(null);
    }

    @Test
    @DisplayName("a semente cria os três usuários de demonstração, que conseguem entrar")
    void usuarios() {
        assertEquals(PerfilUsuario.ADMIN, autenticacao.autenticar("admin", "admin123").getPerfil());
        assertEquals(PerfilUsuario.GERENTE, autenticacao.autenticar("gerente", "gerente123").getPerfil());
        assertEquals(PerfilUsuario.OPERADOR, autenticacao.autenticar("operador", "operador123").getPerfil());
        assertThrows(RegraNegocioException.class, () -> autenticacao.autenticar("admin", "errada"));
    }

    @Test
    @DisplayName("a semente cria funcionários (um inativo), tipos de café e balaios")
    void cadastros() {
        assertEquals(3, funcionarios.listar().size());
        assertEquals(2, funcionarios.listarAtivos().size());
        assertEquals(3, tipos.listar().size());
        assertEquals(6, balaios.listar().size());
    }

    @Test
    @DisplayName("a semente deixa saldo de 90 sacas de Arábica, 40 de Conilon e 0 de Catuaí")
    void estoqueInicial() {
        int arabica = tipos.listar().get(0).getId();
        int conilon = tipos.listar().get(1).getId();
        int catuai = tipos.listar().get(2).getId();
        assertEquals(90, estoque.consultarSaldo(arabica));
        assertEquals(40, estoque.consultarSaldo(conilon));
        assertEquals(0, estoque.consultarSaldo(catuai));
    }
}
