/* pagina-inicio.js - tela inicial: resumo e simulador de produção (cálculo feito pela API, com a CalculadoraProducao do núcleo) */
(function () {
  'use strict';
  var CG = window.CafeGestor;
  var $ = function (id) { return document.getElementById(id); };

  CG.Pagina.iniciar().then(function (usuario) {
    if (!usuario) { return; }

    $('boas-vindas').textContent = 'Bem-vindo, ' + usuario.nome + '!';
    $('texto-perfil').textContent = CG.Sessao.podeAlterar(usuario)
      ? 'Seu perfil (' + usuario.perfil + ') pode consultar e alterar os dados.'
      : 'Seu perfil (' + usuario.perfil + ') pode apenas consultar os dados.';

    CG.Api.resumo().then(function (resumo) {
      $('estat-funcionarios').textContent = resumo.funcionariosAtivos;
      $('estat-balaios').textContent = resumo.balaiosRegistrados;
      $('estat-estoque').textContent = resumo.sacasEmEstoque;
    }, function (erro) { CG.UI.aviso(erro.message, 'erro'); });

    /* ---------- simulador ---------- */
    var REGRAS = {
      balaiosTotal: { rotulo: 'total de balaios', obrigatorio: true, tipo: 'inteiro', minimo: 0, maximo: 1000000 },
      balaiosPorSaca: { rotulo: 'balaios por saca', obrigatorio: true, tipo: 'inteiro', minimo: 1, maximo: 1000 }
    };
    var form = $('form-simulador');
    var resultado = $('resultado-simulador');
    var pedido = 0; // descarta respostas antigas quando o usuário digita rápido

    function simular() {
      var r = CG.Validacao.validarFormulario(form, REGRAS, { semFoco: true });
      if (!r.ok) {
        pedido++;
        resultado.hidden = true;
        return;
      }
      var meu = ++pedido;
      CG.Api.simularProducao(r.valores.balaiosTotal, r.valores.balaiosPorSaca).then(function (s) {
        if (meu !== pedido) { return; }
        resultado.textContent = s.balaios + ' balaio(s) rendem ' + s.sacasCompletas +
          ' saca(s) completa(s), sobrando ' + s.balaiosRestantes + ' balaio(s).';
        resultado.hidden = false;
      }, function (erro) {
        if (meu !== pedido) { return; }
        resultado.textContent = erro.message;
        resultado.hidden = false;
      });
    }
    form.addEventListener('input', simular);
    form.addEventListener('submit', function (evento) { evento.preventDefault(); simular(); });
  });
})();
