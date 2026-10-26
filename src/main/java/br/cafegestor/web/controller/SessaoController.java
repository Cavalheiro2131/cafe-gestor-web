package br.cafegestor.web.controller;

import br.cafegestor.web.dto.ErroResposta;
import br.cafegestor.web.dto.LoginRequest;
import br.cafegestor.web.dto.UsuarioResposta;
import br.cafegestor.web.seguranca.AutorizacaoInterceptor;
import br.cafegestor.web.seguranca.UsuarioSessao;
import exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AutenticacaoService;

/** Login, logout e consulta do usuário logado (sessão HTTP no servidor, identificada por cookie). */
@RestController
@RequestMapping("/api/sessao")
public class SessaoController {
    public static final String ATRIBUTO = "usuarioLogado";

    private final AutenticacaoService autenticacao;

    public SessaoController(AutenticacaoService autenticacao) {
        this.autenticacao = autenticacao;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> entrar(@RequestBody LoginRequest dados, HttpServletRequest requisicao) {
        Usuario usuario;
        try {
            usuario = autenticacao.autenticar(dados.login(), dados.senha());
        } catch (RegraNegocioException erro) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroResposta(erro.getMessage()));
        }
        // Descarta a sessão anterior e cria outra: evita a fixação de sessão.
        HttpSession antiga = requisicao.getSession(false);
        if (antiga != null) {
            antiga.invalidate();
        }
        UsuarioSessao logado = new UsuarioSessao(usuario.getId(), usuario.getNome(), usuario.getLogin(), usuario.getPerfil());
        requisicao.getSession(true).setAttribute(ATRIBUTO, logado);
        return ResponseEntity.ok(UsuarioResposta.de(logado));
    }

    @GetMapping("/atual")
    public ResponseEntity<Object> atual(HttpServletRequest requisicao) {
        UsuarioSessao usuario = AutorizacaoInterceptor.usuarioDaSessao(requisicao);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroResposta("Faça login para continuar."));
        }
        return ResponseEntity.ok(UsuarioResposta.de(usuario));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> sair(HttpServletRequest requisicao) {
        HttpSession sessao = requisicao.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return ResponseEntity.noContent().build();
    }
}
