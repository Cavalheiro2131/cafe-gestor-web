/* pagina-estoque-movimentacao.js - controle de estoque: entrada e saída de sacas */
(function () {
  'use strict';
  var CG = window.CafeGestor;
  var UI = CG.UI;
  var $ = function (id) { return document.getElementById(id); };

  CG.Pagina.iniciar().then(function (usuario) {
    if (!usuario) { return; }

    var BASE = {
      tipoCafeId: { rotulo: 'tipo de café', obrigatorio: true },
      quantidade: { rotulo: 'quantidade', obrigatorio: true, tipo: 'inteiro', minimo: 1, maximo: 1000000 },
      descricao: { rotulo: 'destino', max: 100 }
    };
    /* Na saída o destino é obrigatório (mesma regra do EstoqueService.registrarSaida). */
    var SAIDA = {
      tipoCafeId: BASE.tipoCafeId,
      quantidade: BASE.quantidade,
      descricao: { rotulo: 'destino', obrigatorio: true, max: 100 }
    };
    var form = $('form-estoque');
    var saldos = {}; // tipoCafeId -> sacas

    function mostrarSaldo() {
      var id = form.elements.tipoCafeId.value;
      $('saldo-atual').textContent = id ? (saldos[id] || 0) + ' saca(s)' : 'Selecione um tipo de café';
    }

    function carregarSaldos() {
      return CG.Api.estoque.saldos().then(function (lista) {
        saldos = {};
        lista.forEach(function (s) { saldos[String(s.tipoCafeId)] = s.sacas; });
        mostrarSaldo();
      }, function (erro) { $('erro-geral').textContent = erro.message; });
    }

    CG.Api.tiposCafe.listar().then(function (lista) {
      UI.preencherSelect(form.elements.tipoCafeId, lista, 'Selecione...', function (t) { return t.id; }, function (t) { return t.nome; });
      return carregarSaldos();
    }, function (erro) { $('erro-geral').textContent = erro.message; });

    form.elements.tipoCafeId.addEventListener('change', function () { carregarSaldos(); });
    mostrarSaldo();
    CG.Validacao.ligarValidacaoAoSair(form, BASE);

    var emAndamento = false;

    function registrar(tipo) {
      if (emAndamento) { return; }
      $('erro-geral').textContent = '';
      var resultado = CG.Validacao.validarFormulario(form, tipo === 'SAIDA' ? SAIDA : BASE);
      if (!resultado.ok) { return; }

      emAndamento = true;
      CG.Api.estoque.movimentar(tipo, Number(resultado.valores.tipoCafeId), resultado.valores.quantidade,
        resultado.valores.descricao || null).then(function (r) {
        saldos[String(resultado.valores.tipoCafeId)] = r.novoSaldo;
        mostrarSaldo();
        UI.aviso((tipo === 'SAIDA' ? 'Saída' : 'Entrada') + ' registrada. Novo saldo: ' + r.novoSaldo + ' saca(s).', 'sucesso');
        form.elements.quantidade.value = '';
        form.elements.descricao.value = '';
        form.elements.quantidade.focus();
      }, function (erro) {
        $('erro-geral').textContent = erro.message;
      }).then(function () { emAndamento = false; });
    }

    $('btn-entrada').addEventListener('click', function () { registrar('ENTRADA'); });
    $('btn-saida').addEventListener('click', function () { registrar('SAIDA'); });
    form.addEventListener('submit', function (evento) { evento.preventDefault(); });
  });
})();
