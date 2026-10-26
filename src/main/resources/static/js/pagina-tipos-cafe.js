/* pagina-tipos-cafe.js - lista de tipos de café com busca */
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
      var texto = $('filtro-texto').value.trim().toLowerCase();
      var lista = todos.filter(function (t) {
        return !texto || t.nome.toLowerCase().indexOf(texto) >= 0 || (t.variedade || '').toLowerCase().indexOf(texto) >= 0;
      });
      var corpo = $('tabela-corpo');
      UI.limpar(corpo);
      if (lista.length === 0) {
        corpo.appendChild(UI.criar('tr', {}, [UI.criar('td', { classe: 'vazio', colspan: 3, texto: 'Nenhum tipo de café encontrado.' })]));
      }
      lista.forEach(function (t) {
        corpo.appendChild(UI.criar('tr', {}, [
          UI.criar('td', { texto: t.nome }),
          UI.criar('td', { texto: t.variedade || '–' }),
          UI.criar('td', { texto: t.observacoes || '–' })
        ]));
      });
      $('contagem').textContent = lista.length + ' tipo(s) de café listado(s).';
    }

    $('filtro-texto').addEventListener('input', desenhar);
    CG.Api.tiposCafe.listar().then(function (lista) {
      todos = lista;
      desenhar();
    }, function (erro) { UI.aviso(erro.message, 'erro'); });
  });
})();
