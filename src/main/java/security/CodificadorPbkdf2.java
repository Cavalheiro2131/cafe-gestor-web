package security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2 com HMAC-SHA256 e sal aleatório (já incluído no JDK). Formato: pbkdf2$iterações$sal$hash. */
public class CodificadorPbkdf2 implements CodificadorSenha {
    private static final String PREFIXO = "pbkdf2";
    private static final int ITERACOES = 65536;
    private static final int BITS_HASH = 256;
    private static final int BYTES_SAL = 16;

    private final SecureRandom aleatorio = new SecureRandom();

    @Override
    public String codificar(String senha) {
        byte[] sal = new byte[BYTES_SAL];
        aleatorio.nextBytes(sal);
        byte[] hash = derivar(senha, sal, ITERACOES);
        Base64.Encoder b64 = Base64.getEncoder();
        return PREFIXO + "$" + ITERACOES + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash);
    }

    @Override
    public boolean confere(String senha, String senhaCodificada) {
        if (senha == null || senhaCodificada == null) {
            return false;
        }
        String[] partes = senhaCodificada.split("\\$");
        if (partes.length != 4 || !PREFIXO.equals(partes[0])) {
            return false;
        }
        try {
            int iteracoes = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            return MessageDigest.isEqual(esperado, derivar(senha, sal, iteracoes));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String senha, byte[] sal, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, iteracoes, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 indisponível nesta JVM", e);
        } finally {
            spec.clearPassword();
        }
    }
}
