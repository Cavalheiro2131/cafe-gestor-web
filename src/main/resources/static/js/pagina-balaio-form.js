/* pagina-balaio-form.js - registro de balaios */
(function () {
  'use strict';
  var CG = window.CafeGestor;
  var UI = CG.UI;
  var $ = function (id) { return document.getElementById(id); };

  CG.Pagina.iniciar().then(function (usuario) {
    if (!usuario) { return; }

    var REGRAS = {
      funcionarioId: { rotulo: 'funcionário', obrigatorio: true },
      tipoCafeId: { rotulo: 'tipo de café', obrigatorio: true },
      quantidade: { rotulo: 'quantidade', obrigatorio: true, tipo: 'inteiro', minimo: 1, maximo: 100000 },
      data: { rotulo: 'data', obrigatorio: true, tipo: 'data', naoFutura: true }
    };
    var form = $('form-balaio');

    form.elements.data.value = UI.hojeIso();
    form.elements.data.max = UI.hojeIso();
    CG.Validacao.ligarValidacaoAoSair(form, REGRAS);

    Promise.all([CG.Api.funcionarios.listar(), CG.Api.tiposCafe.listar()]).then(function (r) {
      UI.preencherSelect(form.elements.funcionarioId, r[0].filter(function (f) { return f.ativo; }), 'Selecione...',
        function (f) { return f.id; }, function (f) { return f.nome + ' - ' + f.funcao; });
      UI.preencherSelect(form.elements.tipoCafeId, r[1], 'Selecione...',
        function (t) { return t.id; }, function (t) { return t.nome; });
    }, function (erro) { $('erro-geral').textContent = erro.message; });

    form.addEventListener('submit', function (evento) {
      evento.preventDefault();
      $('erro-geral').textContent = '';
      var resultado = CG.Validacao.validarFormulario(form, REGRAS);
      if (!resultado.ok) { return; }

      var botao = form.querySelector('button[type=submit]');
      botao.disabled = true;
      CG.Api.balaios.registrar({
        funcionarioId: Number(resultado.valores.funcionarioId),
        tipoCafeId: Number(resultado.valores.tipoCafeId),
        quantidade: resultado.valores.quantidade,
        data: resultado.valores.data
      }).then(function () {
        UI.avisoNaProximaPagina('Balaio registrado com sucesso.', 'sucesso');
        window.location.href = 'balaios.html';
      }, function (erro) {
        botao.disabled = false;
        $('erro-geral').textContent = erro.message;
      });
    });
  });
})();
