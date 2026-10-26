/* api.js - comunicação com o back-end (API REST do Spring). Substitui o antigo mock-dados.js.
 *
 * Todas as funções devolvem Promises. Em caso de erro, a Promise é rejeitada com um Error cuja
 * mensagem já é amigável (vem do campo "mensagem" da resposta) e que tem a propriedade status (HTTP). */
(function (g) {
  'use strict';
  var CG = (g.CafeGestor = g.CafeGestor || {});

  /**
   * @param opcoes.semRedirecionar  não leva ao login quando a API responde 401 (usado no próprio login)
   */
  function requisitar(metodo, url, corpo, opcoes) {
    var config = { method: metodo, headers: { 'Accept': 'application/json' }, credentials: 'same-origin' };
    if (corpo !== undefined) {
      config.headers['Content-Type'] = 'application/json';
      config.body = JSON.stringify(corpo);
    }
    return fetch(url, config).then(
      function (resposta) {
        if (resposta.status === 204) { return null; }
        return resposta.text().then(function (texto) {
          var dados = null;
          try { dados = texto ? JSON.parse(texto) : null; } catch (e) { dados = null; }
          if (resposta.ok) { return dados; }

          var erro = new Error((dados && dados.mensagem) || 'Erro inesperado. Tente novamente.');
          erro.status = resposta.status;
          if (resposta.status === 401 && !(opcoes && opcoes.semRedirecionar)) {
            g.location.replace('index.html?motivo=login');
          }
          throw erro;
        });
      },
      function () {
        var erro = new Error('Não foi possível conectar ao servidor.');
        erro.status = 0;
        throw erro;
      }
    );
  }

  function get(url) { return requisitar('GET', url); }
  function post(url, corpo) { return requisitar('POST', url, corpo); }

  CG.Api = {
    login: function (login, senha) {
      return requisitar('POST', '/api/sessao/login', { login: login, senha: senha }, { semRedirecionar: true });
    },
    sessaoAtual: function (opcoes) { return requisitar('GET', '/api/sessao/atual', undefined, opcoes); },
    sair: function () { return post('/api/sessao/logout'); },

    resumo: function () { return get('/api/resumo'); },
    simularProducao: function (balaios, balaiosPorSaca) {
      return get('/api/producao/simulacao?balaios=' + encodeURIComponent(balaios) +
        '&balaiosPorSaca=' + encodeURIComponent(balaiosPorSaca));
    },

    funcionarios: {
      listar: function () { return get('/api/funcionarios'); },
      buscar: function (id) { return get('/api/funcionarios/' + encodeURIComponent(id)); },
      criar: function (dados) { return post('/api/funcionarios', dados); },
      atualizar: function (id, dados) { return requisitar('PUT', '/api/funcionarios/' + encodeURIComponent(id), dados); },
      alterarStatus: function (id, ativo) {
        return requisitar('PATCH', '/api/funcionarios/' + encodeURIComponent(id) + '/status', { ativo: ativo });
      }
    },

    tiposCafe: {
      listar: function () { return get('/api/tipos-cafe'); },
      criar: function (dados) { return post('/api/tipos-cafe', dados); }
    },

    balaios: {
      listar: function () { return get('/api/balaios'); },
      registrar: function (dados) { return post('/api/balaios', dados); }
    },

    estoque: {
      saldos: function () { return get('/api/estoque/saldos'); },
      historico: function () { return get('/api/estoque/historico'); },
      /** tipo: 'ENTRADA' ou 'SAIDA'. Devolve { novoSaldo }. */
      movimentar: function (tipo, tipoCafeId, quantidade, descricao) {
        return post('/api/estoque/movimentacoes', {
          tipo: tipo, tipoCafeId: tipoCafeId, quantidade: quantidade, descricao: descricao
        });
      }
    }
  };
})(window);
