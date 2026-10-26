package util;

import exception.RegraNegocioException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Converte o texto digitado em formulários (Swing ou Web) para tipos Java, com mensagens claras. */
public final class Conversor {
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Conversor() { }

    public static int paraInteiroPositivo(String texto, String campo) {
        int valor;
        try {
            valor = Integer.parseInt(texto == null ? "" : texto.trim());
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("O campo " + campo + " deve ser um número inteiro.");
        }
        return Validacoes.inteiroPositivo(valor, campo);
    }

    public static LocalDate paraData(String texto, String campo) {
        try {
            return LocalDate.parse(texto == null ? "" : texto.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException("O campo " + campo + " deve estar no formato dd/mm/aaaa.");
        }
    }
}
