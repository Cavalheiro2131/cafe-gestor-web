/* pagina-balaios.js - lista de balaios com filtros por funcionário, tipo de café e data */
(function () {
  'use strict';
  var CG = window.CafeGestor;
  var UI = CG.UI;
  var $ = function (id) { return document.getElementById(id); };

  CG.Pagina.iniciar().then(function (usuario) {
    if (!usuario) { return; }
    $('btn-novo').hidden = !CG.Sessao.podeAlterar(usuario);
    var todos = [];

    function desenhar() {
      var funcionario = $('filtro-funcionario').value;
      var tipo = $('filtro-tipo').value;
      var data = $('filtro-data').value;
      var lista = todos.filter(function (b) {
        return (!funcionario || String(b.funcionarioId) === funcionario)
          && (!tipo || String(b.tipoCafeId) === tipo)
          && (!data || b.data === data);
      });
      var corpo = $('tabela-corpo');
      UI.limpar(corpo);
      if (lista.length === 0) {
        corpo.appendChild(UI.criar('tr', {}, [UI.criar('td', { classe: 'vazio', colspan: 4, texto: 'Nenhum balaio encontrado.' })]));
      }
      var total = 0;
      lista.forEach(function (b) {
        total += b.quantidade;
        corpo.appendChild(UI.criar('tr', {}, [
          UI.criar('td', { texto: UI.dataBr(b.data) }),
          UI.criar('td', { texto: b.funcionarioNome }),
          UI.criar('td', { texto: b.tipoCafeNome }),
          UI.criar('td', { classe: 'numero', texto: String(b.quantidade) })
        ]));
      });
      $('total-balaios').textContent = String(total);
      $('contagem').textContent = lista.length + ' registro(s) listado(s).';
    }

    ['filtro-funcionario', 'filtro-tipo', 'filtro-data'].forEach(function (id) {
      $(id).addEventListener('change', desenhar);
    });
    $('btn-limpar-filtros').addEventListener('click', function () {
      $('filtro-funcionario').value = '';
      $('filtro-tipo').value = '';
      $('filtro-data').value = '';
      desenhar();
    });

    Promise.all([CG.Api.funcionarios.listar(), CG.Api.tiposCafe.listar(), CG.Api.balaios.listar()]).then(function (r) {
      UI.preencherSelect($('filtro-funcionario'), r[0], 'Todos', function (f) { return f.id; }, function (f) { return f.nome; });
      UI.preencherSelect($('filtro-tipo'), r[1], 'Todos', function (t) { return t.id; }, function (t) { return t.nome; });
      todos = r[2];
      desenhar();
    }, function (erro) { UI.aviso(erro.message, 'erro'); });
  });
})();
