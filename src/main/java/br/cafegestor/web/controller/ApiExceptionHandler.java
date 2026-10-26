package br.cafegestor.web.controller;

import br.cafegestor.web.dto.ErroResposta;
import exception.RegraNegocioException;
import exception.RepositorioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Traduz as exceções do núcleo em respostas HTTP com o corpo {"mensagem": "..."}. */
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /** Regra de negócio violada (campo inválido, saldo insuficiente, registro não encontrado...): 422. */
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> regraDeNegocio(RegraNegocioException erro) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErroResposta(erro.getMessage()));
    }

    /** Falha técnica no banco: o detalhe vai para o log, e o usuário recebe uma mensagem genérica. */
    @ExceptionHandler(RepositorioException.class)
    public ResponseEntity<ErroResposta> banco(RepositorioException erro) {
        LOG.error("Falha de persistência", erro);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroResposta("Não foi possível acessar o banco de dados. Tente novamente."));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ErroResposta> requisicaoInvalida(Exception erro) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResposta("Requisição inválida."));
    }
}
