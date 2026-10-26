package br.cafegestor.web;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Teste de integração: sobe o contexto completo do Spring (perfil "memoria", sem banco) e chama a API
 * pelo MockMvc, passando por rotas, interceptor de login/permissão, JSON e tratamento de erros.
 * Os testes não dependem da ordem de execução (o contexto é compartilhado).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("memoria")
class ApiIntegracaoTest {
    @Autowired
    private MockMvc mvc;

    private MockHttpSession entrar(String login, String senha) throws Exception {
        MvcResult resultado = mvc.perform(post("/api/sessao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"" + login + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) resultado.getRequest().getSession(false);
    }

    @Test
    @DisplayName("a página de login é servida como arquivo estático")
    void servePaginaEstatica() throws Exception {
        mvc.perform(get("/index.html")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("sem login, a API responde 401")
    void semLogin() throws Exception {
        mvc.perform(get("/api/funcionarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("login com senha errada responde 401")
    void senhaErrada() throws Exception {
        mvc.perform(post("/api/sessao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("ADMIN entra, vê o próprio perfil e lista os funcionários")
    void adminConsulta() throws Exception {
        MockHttpSession sessao = entrar("admin", "admin123");

        mvc.perform(get("/api/sessao/atual").session(sessao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("admin"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
        mvc.perform(get("/api/funcionarios").session(sessao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(3)));
    }

    @Test
    @DisplayName("ADMIN cadastra funcionário e recebe 201 com o id")
    void adminCadastra() throws Exception {
        MockHttpSession sessao = entrar("admin", "admin123");

        mvc.perform(post("/api/funcionarios").session(sessao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"funcao\":\"Colhedora\",\"contato\":\"95555-4444\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("OPERADOR consulta, mas é barrado com 403 ao gravar")
    void operadorSoConsulta() throws Exception {
        MockHttpSession sessao = entrar("operador", "operador123");

        mvc.perform(get("/api/estoque/saldos").session(sessao)).andExpect(status().isOk());
        mvc.perform(post("/api/funcionarios").session(sessao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"funcao\":\"Colhedora\",\"contato\":\"1\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/api/funcionarios/1/status").session(sessao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ativo\":false}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("regra de negócio violada responde 422 (balaio com quantidade zero)")
    void regraDeNegocio() throws Exception {
        MockHttpSession sessao = entrar("admin", "admin123");

        mvc.perform(post("/api/balaios").session(sessao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"funcionarioId\":1,\"tipoCafeId\":1,\"quantidade\":0,\"data\":\"2026-10-20\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("saída maior que o saldo responde 422 (Catuaí começa sem estoque)")
    void saidaSemSaldo() throws Exception {
        MockHttpSession sessao = entrar("admin", "admin123");

        mvc.perform(post("/api/estoque/movimentacoes").session(sessao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"SAIDA\",\"tipoCafeId\":3,\"quantidade\":1,\"descricao\":\"Venda\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("entrada de estoque responde 201 com o novo saldo")
    void entradaDeEstoque() throws Exception {
        MockHttpSession sessao = entrar("gerente", "gerente123");

        mvc.perform(post("/api/estoque/movimentacoes").session(sessao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"ENTRADA\",\"tipoCafeId\":3,\"quantidade\":5,\"descricao\":\"Teste\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.novoSaldo").value(greaterThanOrEqualTo(5)));
    }

    @Test
    @DisplayName("a simulação de produção usa a CalculadoraProducao do núcleo")
    void simulacao() throws Exception {
        MockHttpSession sessao = entrar("operador", "operador123");

        mvc.perform(get("/api/producao/simulacao?balaios=20&balaiosPorSaca=8").session(sessao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sacasCompletas").value(2))
                .andExpect(jsonPath("$.balaiosRestantes").value(4));
        mvc.perform(get("/api/producao/simulacao?balaios=abc&balaiosPorSaca=8").session(sessao))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JSON malformado responde 400")
    void jsonInvalido() throws Exception {
        MockHttpSession sessao = entrar("admin", "admin123");

        mvc.perform(post("/api/funcionarios").session(sessao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("depois do logout a sessão deixa de valer")
    void logout() throws Exception {
        MockHttpSession sessao = entrar("admin", "admin123");

        mvc.perform(post("/api/sessao/logout").session(sessao)).andExpect(status().isNoContent());
        mvc.perform(get("/api/funcionarios").session(sessao)).andExpect(status().isUnauthorized());
    }
}
