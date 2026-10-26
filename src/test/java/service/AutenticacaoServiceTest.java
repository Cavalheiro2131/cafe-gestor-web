package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.RegraNegocioException;
import model.PerfilUsuario;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.memory.MemoriaUsuarioRepository;
import security.CodificadorSenha;

/**
 * Usa um codificador falso (dublê de teste): como o serviço depende da interface CodificadorSenha,
 * o teste não precisa do PBKDF2 real (princípio da inversão de dependência).
 */
class AutenticacaoServiceTest {
    private static final CodificadorSenha CODIFICADOR_FALSO = new CodificadorSenha() {
        @Override
        public String codificar(String senha) {
            return "codificada:" + senha;
        }

        @Override
        public boolean confere(String senha, String senhaCodificada) {
            return ("codificada:" + senha).equals(senhaCodificada);
        }
    };

    private AutenticacaoService servico;
    private Usuario ana;

    @BeforeEach
    void preparar() {
        servico = new AutenticacaoService(new MemoriaUsuarioRepository(), CODIFICADOR_FALSO);
        ana = servico.cadastrar("Ana Souza", "ana", "senha123", PerfilUsuario.GERENTE);
    }

    @Test
    @DisplayName("login e senha corretos autenticam")
    void autenticaComCredenciaisCorretas() {
        Usuario autenticado = servico.autenticar("ana", "senha123");
        assertEquals(ana.getId(), autenticado.getId());
        assertEquals(PerfilUsuario.GERENTE, autenticado.getPerfil());
    }

    @Test
    @DisplayName("senha errada é recusada")
    void recusaSenhaErrada() {
        assertThrows(RegraNegocioException.class, () -> servico.autenticar("ana", "outra"));
    }

    @Test
    @DisplayName("usuário inexistente e senha errada geram a mesma mensagem")
    void mensagensIguaisNaoRevelamLogins() {
        RegraNegocioException senhaErrada = assertThrows(RegraNegocioException.class,
                () -> servico.autenticar("ana", "outra"));
        RegraNegocioException semUsuario = assertThrows(RegraNegocioException.class,
                () -> servico.autenticar("ninguem", "senha123"));
        assertEquals(senhaErrada.getMessage(), semUsuario.getMessage());
    }

    @Test
    @DisplayName("login ou senha vazios são recusados antes de consultar")
    void recusaCamposVazios() {
        assertThrows(RegraNegocioException.class, () -> servico.autenticar("", "senha123"));
        assertThrows(RegraNegocioException.class, () -> servico.autenticar("ana", ""));
        assertThrows(RegraNegocioException.class, () -> servico.autenticar(null, null));
    }

    @Test
    @DisplayName("a senha é guardada codificada, não em texto puro")
    void senhaNaoFicaEmTextoPuro() {
        assertNotEquals("senha123", ana.getSenhaCodificada());
        assertTrue(ana.getSenhaCodificada().startsWith("codificada:"));
    }

    @Test
    @DisplayName("login repetido é recusado, mesmo com letras maiúsculas")
    void recusaLoginRepetido() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> servico.cadastrar("Outra Ana", "ANA", "senha123", PerfilUsuario.OPERADOR));
        assertTrue(erro.getMessage().contains("Já existe"));
    }

    @Test
    @DisplayName("senha com menos de 6 caracteres é recusada; com exatamente 6 é aceita")
    void tamanhoMinimoDaSenha() {
        assertThrows(RegraNegocioException.class,
                () -> servico.cadastrar("Beto", "beto", "12345", PerfilUsuario.OPERADOR));
        assertTrue(servico.cadastrar("Beto", "beto", "123456", PerfilUsuario.OPERADOR).getId() > 0);
    }

    @Test
    @DisplayName("perfil ausente e nome vazio são recusados")
    void recusaPerfilENomeInvalidos() {
        assertThrows(RegraNegocioException.class, () -> servico.cadastrar("Beto", "beto", "123456", null));
        assertThrows(RegraNegocioException.class,
                () -> servico.cadastrar(" ", "beto", "123456", PerfilUsuario.OPERADOR));
    }

    @Test
    @DisplayName("alterar a senha: a nova passa a valer e a antiga deixa de valer")
    void alteraSenha() {
        servico.alterarSenha(ana.getId(), "senha123", "nova-senha");
        assertEquals(ana.getId(), servico.autenticar("ana", "nova-senha").getId());
        assertThrows(RegraNegocioException.class, () -> servico.autenticar("ana", "senha123"));
    }

    @Test
    @DisplayName("alterar a senha exige a senha atual correta e uma nova senha válida")
    void alteracaoExigeSenhaAtual() {
        assertThrows(RegraNegocioException.class, () -> servico.alterarSenha(ana.getId(), "errada", "nova-senha"));
        assertThrows(RegraNegocioException.class, () -> servico.alterarSenha(ana.getId(), "senha123", "123"));
        assertFalse(ana.getSenhaCodificada().contains("nova-senha"));
    }
}
