package br.cafegestor.web.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import repository.BalaioRepository;
import repository.FuncionarioRepository;
import repository.TipoCafeRepository;
import repository.UsuarioRepository;
import security.CodificadorPbkdf2;
import security.CodificadorSenha;
import service.AutenticacaoService;
import service.BalaioService;
import service.FuncionarioService;
import service.TipoCafeService;

/**
 * Liga os serviços do núcleo (Etapa 6) às implementações de repositório escolhidas pelo perfil
 * ativo (ConfiguracaoJdbc ou ConfiguracaoMemoria). É o papel que a FabricaDeServicos tinha no projeto do núcleo,
 * agora com injeção de dependências do Spring.
 */
@Configuration
public class ConfiguracaoServicos {
    @Bean
    public Clock relogio() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public CodificadorSenha codificadorSenha() {
        return new CodificadorPbkdf2();
    }

    @Bean
    public AutenticacaoService autenticacaoService(UsuarioRepository usuarios, CodificadorSenha codificador) {
        return new AutenticacaoService(usuarios, codificador);
    }

    @Bean
    public FuncionarioService funcionarioService(FuncionarioRepository funcionarios) {
        return new FuncionarioService(funcionarios);
    }

    @Bean
    public TipoCafeService tipoCafeService(TipoCafeRepository tipos) {
        return new TipoCafeService(tipos);
    }

    @Bean
    public BalaioService balaioService(BalaioRepository balaios, FuncionarioRepository funcionarios,
                                       TipoCafeRepository tipos, Clock relogio) {
        return new BalaioService(balaios, funcionarios, tipos, relogio);
    }
}
