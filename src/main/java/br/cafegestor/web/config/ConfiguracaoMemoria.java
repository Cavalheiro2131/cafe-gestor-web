package br.cafegestor.web.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import repository.BalaioRepository;
import repository.EstoqueRepository;
import repository.FuncionarioRepository;
import repository.MovimentacaoRepository;
import repository.TipoCafeRepository;
import repository.UsuarioRepository;
import repository.memory.MemoriaBalaioRepository;
import repository.memory.MemoriaEstoqueRepository;
import repository.memory.MemoriaFuncionarioRepository;
import repository.memory.MemoriaMovimentacaoRepository;
import repository.memory.MemoriaTipoCafeRepository;
import repository.memory.MemoriaUsuarioRepository;
import service.EstoqueService;

/** Perfil "memoria": mesmos serviços, sem banco de dados. Usado nos testes e para demonstrar o sistema sem MySQL. */
@Configuration
@Profile("memoria")
public class ConfiguracaoMemoria {
    @Bean
    public UsuarioRepository usuarioRepository() {
        return new MemoriaUsuarioRepository();
    }

    @Bean
    public FuncionarioRepository funcionarioRepository() {
        return new MemoriaFuncionarioRepository();
    }

    @Bean
    public TipoCafeRepository tipoCafeRepository() {
        return new MemoriaTipoCafeRepository();
    }

    @Bean
    public BalaioRepository balaioRepository() {
        return new MemoriaBalaioRepository();
    }

    @Bean
    public EstoqueRepository estoqueRepository() {
        return new MemoriaEstoqueRepository();
    }

    @Bean
    public MovimentacaoRepository movimentacaoRepository() {
        return new MemoriaMovimentacaoRepository();
    }

    @Bean
    public EstoqueService estoqueService(EstoqueRepository estoques, MovimentacaoRepository movimentacoes,
                                         TipoCafeRepository tipos, Clock relogio) {
        return new EstoqueService(estoques, movimentacoes, tipos, relogio);
    }
}
