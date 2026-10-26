package br.cafegestor.web.seguranca;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Requisição, resposta e sessão HTTP falsas (java.lang.reflect.Proxy), para testar o interceptor e o
 * controlador de sessão sem subir o Spring e sem depender de biblioteca de mocks.
 */
public final class Falsos {
    private Falsos() { }

    /** Sessão HTTP falsa que guarda atributos e sabe se foi invalidada. */
    public static final class Sessao {
        public final Map<String, Object> atributos = new HashMap<>();
        public boolean invalidada;
        private final Requisicao dona;

        Sessao(Requisicao dona) { this.dona = dona; }

        public HttpSession comoHttpSession() {
            return (HttpSession) Proxy.newProxyInstance(Falsos.class.getClassLoader(),
                    new Class<?>[]{HttpSession.class}, (proxy, metodo, args) -> {
                        switch (metodo.getName()) {
                            case "getAttribute": return atributos.get((String) args[0]);
                            case "setAttribute": atributos.put((String) args[0], args[1]); return null;
                            case "invalidate": invalidada = true; dona.sessao = null; return null;
                            default: return padrao(metodo.getReturnType());
                        }
                    });
        }
    }

    /** Requisição HTTP falsa. */
    public static final class Requisicao {
        final String metodo;
        final String uri;
        public Sessao sessao;

        public Requisicao(String metodo, String uri) {
            this.metodo = metodo;
            this.uri = uri;
        }

        public Requisicao comUsuario(UsuarioSessao usuario) {
            this.sessao = new Sessao(this);
            this.sessao.atributos.put("usuarioLogado", usuario);
            return this;
        }

        public HttpServletRequest comoHttpServletRequest() {
            InvocationHandler tratador = (proxy, m, args) -> {
                switch (m.getName()) {
                    case "getMethod": return metodo;
                    case "getRequestURI": return uri;
                    case "getContextPath": return "";
                    case "getSession":
                        boolean criar = args == null || args.length == 0 || (Boolean) args[0];
                        if (sessao == null && criar) { sessao = new Sessao(this); }
                        return sessao == null ? null : sessao.comoHttpSession();
                    case "toString": return metodo + " " + uri;
                    case "hashCode": return System.identityHashCode(this);
                    case "equals": return proxy == args[0];
                    default: return padrao(m.getReturnType());
                }
            };
            return (HttpServletRequest) Proxy.newProxyInstance(Falsos.class.getClassLoader(),
                    new Class<?>[]{HttpServletRequest.class}, tratador);
        }
    }

    /** Resposta HTTP falsa que registra status e corpo. */
    public static final class Resposta {
        public int status = 200;
        public String tipoConteudo;
        public final StringWriter corpo = new StringWriter();

        public HttpServletResponse comoHttpServletResponse() {
            return (HttpServletResponse) Proxy.newProxyInstance(Falsos.class.getClassLoader(),
                    new Class<?>[]{HttpServletResponse.class}, (proxy, m, args) -> {
                        switch (m.getName()) {
                            case "setStatus": status = (Integer) args[0]; return null;
                            case "setContentType": tipoConteudo = (String) args[0]; return null;
                            case "getWriter": return new PrintWriter(corpo, true);
                            case "toString": return "resposta falsa";
                            case "hashCode": return System.identityHashCode(this);
                            case "equals": return proxy == args[0];
                            default: return padrao(m.getReturnType());
                        }
                    });
        }
    }

    private static Object padrao(Class<?> tipo) {
        if (tipo == boolean.class) { return false; }
        if (tipo == int.class) { return 0; }
        if (tipo == long.class) { return 0L; }
        return null;
    }
}
