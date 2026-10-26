package br.cafegestor.web.config;

import java.time.Clock;
import java.time.LocalDate;
import model.Funcionario;
import model.PerfilUsuario;
import model.TipoCafe;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import service.AutenticacaoService;
import service.BalaioService;
import service.EstoqueService;
import service.FuncionarioService;
import service.TipoCafeService;

/**
 * Dados iniciais do perfil "memoria", criados pelos próprios serviços do núcleo (portanto passam
 * pelas mesmas regras). No MySQL os dados iniciais vêm do script sql/dados_iniciais.sql.
 */
@Component
@Profile("memoria")
public class SementeDemonstracao implements ApplicationRunner {
    private final AutenticacaoService autenticacao;
    private final FuncionarioService funcionarios;
    private final TipoCafeService tipos;
    private final BalaioService balaios;
    private final EstoqueService estoque;
    private final Clock relogio;

    public SementeDemonstracao(AutenticacaoService autenticacao, FuncionarioService funcionarios,
                               TipoCafeService tipos, BalaioService balaios, EstoqueService estoque, Clock relogio) {
        this.autenticacao = autenticacao;
        this.funcionarios = funcionarios;
        this.tipos = tipos;
        this.balaios = balaios;
        this.estoque = estoque;
        this.relogio = relogio;
    }

    @Override
    public void run(ApplicationArguments argumentos) {
        autenticacao.cadastrar("Administrador", "admin", "admin123", PerfilUsuario.ADMIN);
        autenticacao.cadastrar("Gerente da Fazenda", "gerente", "gerente123", PerfilUsuario.GERENTE);
        autenticacao.cadastrar("Operador", "operador", "operador123", PerfilUsuario.OPERADOR);

        Funcionario joao = funcionarios.cadastrar("João Silva", "Colhedor", "99999-9999");
        Funcionario maria = funcionarios.cadastrar("Maria Souza", "Colhedora", "98888-7777");
        Funcionario pedro = funcionarios.cadastrar("Pedro Alves", "Encarregado", "97777-6666");
        funcionarios.inativar(pedro.getId());

        TipoCafe arabica = tipos.cadastrar("Arábica", "Especial", "Lote principal da safra");
        TipoCafe conilon = tipos.cadastrar("Conilon", "Robusta", null);
        TipoCafe catuai = tipos.cadastrar("Catuaí", "Vermelho", "Talhão novo");

        LocalDate hoje = LocalDate.now(relogio);
        balaios.registrar(joao.getId(), arabica.getId(), 20, hoje.minusDays(6));
        balaios.registrar(maria.getId(), arabica.getId(), 18, hoje.minusDays(5));
        balaios.registrar(joao.getId(), conilon.getId(), 12, hoje.minusDays(4));
        balaios.registrar(maria.getId(), conilon.getId(), 15, hoje.minusDays(2));
        balaios.registrar(joao.getId(), arabica.getId(), 22, hoje.minusDays(1));
        balaios.registrar(maria.getId(), catuai.getId(), 9, hoje);

        estoque.registrarEntrada(arabica.getId(), 100, "Colheita da safra");
        estoque.registrarSaida(arabica.getId(), 10, "Venda");
        estoque.registrarEntrada(conilon.getId(), 40, "Colheita da safra");
    }
}
