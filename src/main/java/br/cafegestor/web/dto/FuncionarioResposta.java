package br.cafegestor.web.dto;

import model.Funcionario;

public record FuncionarioResposta(int id, String nome, String funcao, String contato, boolean ativo) {
    public static FuncionarioResposta de(Funcionario f) {
        return new FuncionarioResposta(f.getId(), f.getNome(), f.getFuncao(), f.getContato(), f.isAtivo());
    }
}
