package service;

import exception.RegraNegocioException;
import util.Validacoes;

/**
 * Cálculos puros de produção, sem acesso a banco ou interface (por isso fáceis de testar).
 * A relação balaios por saca é configurável, porque depende da fazenda.
 */
public class CalculadoraProducao {
    private final int balaiosPorSaca;

    public CalculadoraProducao(int balaiosPorSaca) {
        this.balaiosPorSaca = Validacoes.inteiroPositivo(balaiosPorSaca, "balaios por saca");
    }

    /** Quantas sacas completas a produção em balaios rende. */
    public int sacasCompletas(int balaios) {
        exigirNaoNegativo(balaios);
        return balaios / balaiosPorSaca;
    }

    /** Balaios que sobram depois de formar as sacas completas. */
    public int balaiosRestantes(int balaios) {
        exigirNaoNegativo(balaios);
        return balaios % balaiosPorSaca;
    }

    /** Média de balaios colhidos por dia no período. */
    public double mediaPorDia(int totalBalaios, int dias) {
        exigirNaoNegativo(totalBalaios);
        Validacoes.inteiroPositivo(dias, "dias");
        return (double) totalBalaios / dias;
    }

    private static void exigirNaoNegativo(int balaios) {
        if (balaios < 0) {
            throw new RegraNegocioException("A quantidade de balaios não pode ser negativa.");
        }
    }
}
