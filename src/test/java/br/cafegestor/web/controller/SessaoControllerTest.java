package br.cafegestor.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.cafegestor.web.dto.ErroResposta;
import br.cafegestor.web.dto.LoginRequest;
import br.cafegestor.web.dto.UsuarioResposta;
import br.cafegestor.web.seguranca.Falsos;
import br.cafegestor.web.seguranca.UsuarioSessao;
import model.PerfilUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import repository.memory.MemoriaUsuarioRepository;
import security.CodificadorSenha;
import service.AutenticacaoService;

class SessaoControllerTest {
    private SessaoController controlador;

    @BeforeEach
    void preparar() {
        CodificadorSenha falso = new CodificadorSenha() {
            @Override public String codificar(String senha) { return "x:" + senha; }
            @Override public boolean confere(String senha, String codificada) { return ("x:" + senha).equals(codificada); }
        };
        AutenticacaoService autenticacao = new AutenticacaoService(new MemoriaUsuarioRepository(), falso);
        autenticacao.cadastrar("Ana Souza", "ana", "senha123", PerfilUsuario.GERENTE);
        controlador = new SessaoController(autenticacao);
    }

    @Test
    @DisplayName("login correto devolve 200, o usuário e guarda-o na sessão")
    void loginCorreto() {
        Falsos.Requisicao req = new Falsos.Requisicao("POST", "/api/sessao/login");
        ResponseEntity<Object> resposta = controlador.entrar(new LoginRequest("ana", "senha123"), req.comoHttpServletRequest());

        assertEquals(200, resposta.getStatusCode().value());
        UsuarioResposta corpo = (UsuarioResposta) resposta.getBody();
        assertEquals("ana", corpo.login());
        assertEquals("GERENTE", corpo.perfil());
        assertNotNull(req.sessao);
        UsuarioSessao naSessao = (UsuarioSessao) req.sessao.atributos.get(SessaoController.ATRIBUTO);
        assertEquals(PerfilUsuario.GERENTE, naSessao.perfil());
    }

    @Test
    @DisplayName("senha errada devolve 401 com a mensagem e não cria sessão")
    void senhaErrada() {
        Falsos.Requisicao req = new Falsos.Requisicao("POST", "/api/sessao/login");
        ResponseEntity<Object> resposta = controlador.entrar(new LoginRequest("ana", "errada"), req.comoHttpServletRequest());

        assertEquals(401, resposta.getStatusCode().value());
        assertEquals("Login ou senha inválidos.", ((ErroResposta) resposta.getBody()).mensagem());
        assertNull(req.sessao);
    }

    @Test
    @DisplayName("novo login descarta a sessão anterior (evita fixação de sessão)")
    void loginTrocaDeSessao() {
        Falsos.Requisicao req = new Falsos.Requisicao("POST", "/api/sessao/login")
                .comUsuario(new UsuarioSessao(9, "Antigo", "antigo", PerfilUsuario.OPERADOR));
        Falsos.Sessao anterior = req.sessao;

        controlador.entrar(new LoginRequest("ana", "senha123"), req.comoHttpServletRequest());

        assertTrue(anterior.invalidada);
        assertFalse(req.sessao == anterior);
        assertEquals("ana", ((UsuarioSessao) req.sessao.atributos.get(SessaoController.ATRIBUTO)).login());
    }

    @Test
    @DisplayName("atual: 401 sem sessão e 200 com o usuário quando logado")
    void atual() {
        ResponseEntity<Object> semLogin = controlador.atual(new Falsos.Requisicao("GET", "/api/sessao/atual").comoHttpServletRequest());
        assertEquals(401, semLogin.getStatusCode().value());

        UsuarioSessao u = new UsuarioSessao(1, "Ana", "ana", PerfilUsuario.ADMIN);
        ResponseEntity<Object> logado = controlador.atual(
                new Falsos.Requisicao("GET", "/api/sessao/atual").comUsuario(u).comoHttpServletRequest());
        assertEquals(200, logado.getStatusCode().value());
        assertEquals("ADMIN", ((UsuarioResposta) logado.getBody()).perfil());
    }

    @Test
    @DisplayName("logout invalida a sessão e responde 204")
    void logout() {
        Falsos.Requisicao req = new Falsos.Requisicao("POST", "/api/sessao/logout")
                .comUsuario(new UsuarioSessao(1, "Ana", "ana", PerfilUsuario.ADMIN));
        Falsos.Sessao sessao = req.sessao;

        ResponseEntity<Void> resposta = controlador.sair(req.comoHttpServletRequest());

        assertEquals(204, resposta.getStatusCode().value());
        assertTrue(sessao.invalidada);
    }

    @Test
    @DisplayName("logout sem sessão também responde 204")
    void logoutSemSessao() {
        ResponseEntity<Void> resposta = controlador.sair(new Falsos.Requisicao("POST", "/api/sessao/logout").comoHttpServletRequest());
        assertEquals(204, resposta.getStatusCode().value());
    }
}
