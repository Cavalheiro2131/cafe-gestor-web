package br.cafegestor.web.seguranca;

import br.cafegestor.web.controller.SessaoController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Protege a API: sem login responde 401; usuário sem permissão de alteração (perfil OPERADOR)
 * que tente gravar (POST, PUT, PATCH ou DELETE) recebe 403. A regra de quem altera dados
 * vem de PerfilUsuario.podeAlterarDados(), do núcleo, a mesma usada na versão desktop.
 */
public class AutorizacaoInterceptor implements HandlerInterceptor {
    static final String MENSAGEM_SEM_LOGIN = "Faça login para continuar.";
    static final String MENSAGEM_SEM_PERMISSAO = "Seu perfil só permite consultas.";

    @Override
    public boolean preHandle(HttpServletRequest requisicao, HttpServletResponse resposta, Object destino)
            throws IOException {
        String caminho = requisicao.getRequestURI().substring(requisicao.getContextPath().length());
        if (caminho.equals("/api/sessao/login") || caminho.equals("/api/sessao/logout")) {
            return true;
        }

        UsuarioSessao usuario = usuarioDaSessao(requisicao);
        if (usuario == null) {
            responder(resposta, 401, MENSAGEM_SEM_LOGIN);
            return false;
        }
        if (!apenasLeitura(requisicao.getMethod()) && !usuario.perfil().podeAlterarDados()) {
            responder(resposta, 403, MENSAGEM_SEM_PERMISSAO);
            return false;
        }
        return true;
    }

    /** Usuário logado na sessão da requisição, ou null se não houver sessão. */
    public static UsuarioSessao usuarioDaSessao(HttpServletRequest requisicao) {
        HttpSession sessao = requisicao.getSession(false);
        if (sessao == null) {
            return null;
        }
        Object atributo = sessao.getAttribute(SessaoController.ATRIBUTO);
        return atributo instanceof UsuarioSessao usuario ? usuario : null;
    }

    private static boolean apenasLeitura(String metodo) {
        return "GET".equalsIgnoreCase(metodo) || "HEAD".equalsIgnoreCase(metodo) || "OPTIONS".equalsIgnoreCase(metodo);
    }

    private static void responder(HttpServletResponse resposta, int status, String mensagem) throws IOException {
        resposta.setStatus(status);
        resposta.setCharacterEncoding("UTF-8");
        resposta.setContentType("application/json;charset=UTF-8");
        resposta.getWriter().write("{\"mensagem\":\"" + mensagem.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
    }
}
