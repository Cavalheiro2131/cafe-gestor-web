/* pagina-tipo-cafe-form.js - cadastro de tipo de café */
(function () {
  'use strict';
  var CG = window.CafeGestor;
  var $ = function (id) { return document.getElementById(id); };

  CG.Pagina.iniciar().then(function (usuario) {
    if (!usuario) { return; }

    var LIMITE_OBS = 1000;
    var REGRAS = {
      nome: { rotulo: 'nome', obrigatorio: true, max: 50 },
      variedade: { rotulo: 'variedade', max: 50 },
      observacoes: { rotulo: 'observações', max: LIMITE_OBS }
    };
    var form = $('form-tipo-cafe');

    function atualizarContador() {
      var restantes = LIMITE_OBS - form.elements.observacoes.value.length;
      $('contador-observacoes').textContent = restantes + ' caractere(s) restante(s)';
    }
    form.elements.observacoes.addEventListener('input', atualizarContador);
    atualizarContador();

    CG.Validacao.ligarValidacaoAoSair(form, REGRAS);

    form.addEventListener('submit', function (evento) {
      evento.preventDefault();
      $('erro-geral').textContent = '';
      var resultado = CG.Validacao.validarFormulario(form, REGRAS);
      if (!resultado.ok) { return; }

      var botao = form.querySelector('button[type=submit]');
      botao.disabled = true;
      CG.Api.tiposCafe.criar(resultado.valores).then(function () {
        CG.UI.avisoNaProximaPagina('Tipo de café cadastrado com sucesso.', 'sucesso');
        window.location.href = 'tipos-cafe.html';
      }, function (erro) {
        botao.disabled = false;
        $('erro-geral').textContent = erro.message;
      });
    });
  });
})();
