package br.cafegestor.web.seguranca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import model.PerfilUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AutorizacaoInterceptorTest {
    private AutorizacaoInterceptor interceptor;
    private Falsos.Resposta resposta;

    private static UsuarioSessao usuario(PerfilUsuario perfil) {
        return new UsuarioSessao(1, "Fulano", "fulano", perfil);
    }

    private boolean passa(String metodo, String uri, UsuarioSessao usuario) throws IOException {
        Falsos.Requisicao req = new Falsos.Requisicao(metodo, uri);
        if (usuario != null) {
            req.comUsuario(usuario);
        }
        return interceptor.preHandle(req.comoHttpServletRequest(), resposta.comoHttpServletResponse(), new Object());
    }

    @BeforeEach
    void preparar() {
        interceptor = new AutorizacaoInterceptor();
        resposta = new Falsos.Resposta();
    }

    @Test
    @DisplayName("login e logout são liberados sem sessão")
    void loginELogoutLiberados() throws IOException {
        assertTrue(passa("POST", "/api/sessao/login", null));
        assertTrue(passa("POST", "/api/sessao/logout", null));
    }

    @Test
    @DisplayName("sem login, a API responde 401 com mensagem em JSON")
    void semLogin401() throws IOException {
        assertFalse(passa("GET", "/api/funcionarios", null));
        assertEquals(401, resposta.status);
        assertTrue(resposta.corpo.toString().contains("Faça login para continuar."));
        assertTrue(resposta.tipoConteudo.startsWith("application/json"));
    }

    @Test
    @DisplayName("OPERADOR pode consultar (GET)")
    void operadorConsulta() throws IOException {
        assertTrue(passa("GET", "/api/estoque/saldos", usuario(PerfilUsuario.OPERADOR)));
    }

    @Test
    @DisplayName("OPERADOR é barrado com 403 em POST, PUT, PATCH e DELETE")
    void operadorNaoGrava() throws IOException {
        for (String metodo : new String[]{"POST", "PUT", "PATCH", "DELETE"}) {
            resposta = new Falsos.Resposta();
            assertFalse(passa(metodo, "/api/funcionarios", usuario(PerfilUsuario.OPERADOR)), metodo);
            assertEquals(403, resposta.status, metodo);
            assertTrue(resposta.corpo.toString().contains("só permite consultas"), metodo);
        }
    }

    @Test
    @DisplayName("ADMIN e GERENTE podem gravar")
    void adminEGerenteGravam() throws IOException {
        assertTrue(passa("POST", "/api/balaios", usuario(PerfilUsuario.ADMIN)));
        assertTrue(passa("PUT", "/api/funcionarios/1", usuario(PerfilUsuario.GERENTE)));
        assertTrue(passa("PATCH", "/api/funcionarios/1/status", usuario(PerfilUsuario.GERENTE)));
    }

    @Test
    @DisplayName("usuarioDaSessao devolve null sem sessão e o usuário quando há login")
    void usuarioDaSessao() {
        assertEquals(null, AutorizacaoInterceptor.usuarioDaSessao(new Falsos.Requisicao("GET", "/x").comoHttpServletRequest()));
        UsuarioSessao u = usuario(PerfilUsuario.ADMIN);
        assertEquals(u, AutorizacaoInterceptor.usuarioDaSessao(new Falsos.Requisicao("GET", "/x").comUsuario(u).comoHttpServletRequest()));
    }
}
