package br.cafegestor.web.config;

import br.cafegestor.web.servico.EstoqueServiceTransacional;
import java.time.Clock;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import repository.BalaioRepository;
import repository.EstoqueRepository;
import repository.FuncionarioRepository;
import repository.MovimentacaoRepository;
import repository.TipoCafeRepository;
import repository.UsuarioRepository;
import repository.jdbc.FonteDeConexao;
import repository.jdbc.JdbcBalaioRepository;
import repository.jdbc.JdbcEstoqueRepository;
import repository.jdbc.JdbcFuncionarioRepository;
import repository.jdbc.JdbcMovimentacaoRepository;
import repository.jdbc.JdbcTipoCafeRepository;
import repository.jdbc.JdbcUsuarioRepository;
import service.EstoqueService;

/**
 * Persistência em MySQL (perfil padrão). Reaproveita os repositórios JDBC do núcleo: eles recebem uma
 * FonteDeConexao, que aqui vem do pool de conexões (HikariCP) configurado em application.properties.
 */
@Configuration
@Profile("!memoria")
public class ConfiguracaoJdbc {

    /**
     * O proxy faz as conexões pedidas pelos repositórios participarem da transação do Spring:
     * dentro de um método @Transactional todos usam a mesma conexão (e o close() delas não a fecha).
     */
    @Bean
    public FonteDeConexao fonteDeConexao(DataSource dataSource) {
        DataSource comTransacao = new TransactionAwareDataSourceProxy(dataSource);
        return comTransacao::getConnection;
    }

    @Bean
    public UsuarioRepository usuarioRepository(FonteDeConexao fonte) {
        return new JdbcUsuarioRepository(fonte);
    }

    @Bean
    public FuncionarioRepository funcionarioRepository(FonteDeConexao fonte) {
        return new JdbcFuncionarioRepository(fonte);
    }

    @Bean
    public TipoCafeRepository tipoCafeRepository(FonteDeConexao fonte) {
        return new JdbcTipoCafeRepository(fonte);
    }

    @Bean
    public BalaioRepository balaioRepository(FonteDeConexao fonte) {
        return new JdbcBalaioRepository(fonte);
    }

    @Bean
    public EstoqueRepository estoqueRepository(FonteDeConexao fonte) {
        return new JdbcEstoqueRepository(fonte);
    }

    @Bean
    public MovimentacaoRepository movimentacaoRepository(FonteDeConexao fonte) {
        return new JdbcMovimentacaoRepository(fonte);
    }

    /** Atualizar o saldo e gravar a movimentação acontecem na mesma transação. */
    @Bean
    public EstoqueService estoqueService(EstoqueRepository estoques, MovimentacaoRepository movimentacoes,
                                         TipoCafeRepository tipos, Clock relogio) {
        return new EstoqueServiceTransacional(estoques, movimentacoes, tipos, relogio);
    }
}
