package util;

import exception.RegraNegocioException;

/** Validações reutilizáveis (antes repetidas em cada tela com isEmpty()). */
public final class Validacoes {
    private Validacoes() { }

    public static String textoObrigatorio(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new RegraNegocioException("O campo " + campo + " é obrigatório.");
        }
        return limitar(valor.trim(), campo, tamanhoMaximo);
    }

    public static String textoOpcional(String valor, String campo, int tamanhoMaximo) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return limitar(valor.trim(), campo, tamanhoMaximo);
    }

    public static int inteiroPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new RegraNegocioException("O campo " + campo + " deve ser maior que zero.");
        }
        return valor;
    }

    private static String limitar(String texto, String campo, int tamanhoMaximo) {
        if (texto.length() > tamanhoMaximo) {
            throw new RegraNegocioException("O campo " + campo + " deve ter no máximo " + tamanhoMaximo + " caracteres.");
        }
        return texto;
    }
}
