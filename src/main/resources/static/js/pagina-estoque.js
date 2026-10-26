/* pagina-estoque.js - consulta de saldos e histórico de movimentações, em abas */
(function () {
  'use strict';
  var CG = window.CafeGestor;
  var UI = CG.UI;
  var $ = function (id) { return document.getElementById(id); };

  CG.Pagina.iniciar().then(function (usuario) {
    if (!usuario) { return; }
    $('btn-novo').hidden = !CG.Sessao.podeAlterar(usuario);
    var historico = [];

    /* ---------- abas ---------- */
    var ABAS = [
      { botao: 'aba-saldos', painel: 'painel-saldos' },
      { botao: 'aba-historico', painel: 'painel-historico' }
    ];

    function ativar(indice) {
      ABAS.forEach(function (aba, i) {
        var selecionada = i === indice;
        $(aba.botao).setAttribute('aria-selected', selecionada ? 'true' : 'false');
        $(aba.botao).tabIndex = selecionada ? 0 : -1;
        $(aba.painel).hidden = !selecionada;
      });
    }

    ABAS.forEach(function (aba, i) {
      $(aba.botao).addEventListener('click', function () { ativar(i); });
      $(aba.botao).addEventListener('keydown', function (evento) {
        if (evento.key !== 'ArrowRight' && evento.key !== 'ArrowLeft') { return; }
        var proxima = (i + (evento.key === 'ArrowRight' ? 1 : ABAS.length - 1)) % ABAS.length;
        ativar(proxima);
        $(ABAS[proxima].botao).focus();
      });
    });

    /* ---------- saldos ---------- */
    function desenharSaldos(saldos) {
      var corpo = $('saldos-corpo');
      UI.limpar(corpo);
      saldos.forEach(function (s) {
        corpo.appendChild(UI.criar('tr', {}, [
          UI.criar('td', { texto: s.tipoCafeNome }),
          UI.criar('td', { classe: 'numero', texto: String(s.sacas) })
        ]));
      });
    }

    /* ---------- histórico ---------- */
    function desenharHistorico() {
      var tipo = $('filtro-tipo-historico').value;
      var lista = historico.filter(function (m) { return !tipo || String(m.tipoCafeId) === tipo; });
      var corpo = $('historico-corpo');
      UI.limpar(corpo);
      if (lista.length === 0) {
        corpo.appendChild(UI.criar('tr', {}, [UI.criar('td', { classe: 'vazio', colspan: 5, texto: 'Nenhuma movimentação encontrada.' })]));
      }
      lista.forEach(function (m) {
        var entrada = m.tipo === 'ENTRADA';
        corpo.appendChild(UI.criar('tr', {}, [
          UI.criar('td', { texto: UI.dataBr(m.data) }),
          UI.criar('td', {}, [UI.criar('span', { classe: 'selo ' + (entrada ? 'selo-entrada' : 'selo-saida'), texto: entrada ? 'Entrada' : 'Saída' })]),
          UI.criar('td', { texto: m.tipoCafeNome }),
          UI.criar('td', { classe: 'numero', texto: String(m.quantidade) }),
          UI.criar('td', { texto: m.descricao || '–' })
        ]));
      });
      $('contagem-historico').textContent = lista.length + ' movimentação(ões) listada(s).';
    }

    $('filtro-tipo-historico').addEventListener('change', desenharHistorico);
    ativar(0);

    Promise.all([CG.Api.estoque.saldos(), CG.Api.estoque.historico(), CG.Api.tiposCafe.listar()]).then(function (r) {
      desenharSaldos(r[0]);
      historico = r[1];
      UI.preencherSelect($('filtro-tipo-historico'), r[2], 'Todos', function (t) { return t.id; }, function (t) { return t.nome; });
      desenharHistorico();
    }, function (erro) { UI.aviso(erro.message, 'erro'); });
  });
})();
