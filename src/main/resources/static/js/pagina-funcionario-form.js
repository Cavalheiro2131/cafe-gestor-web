/* pagina-funcionario-form.js - cadastro e edição de funcionário */
(function () {
  'use strict';
  var CG = window.CafeGestor;
  var $ = function (id) { return document.getElementById(id); };

  CG.Pagina.iniciar().then(function (usuario) {
    if (!usuario) { return; }

    var REGRAS = {
      nome: { rotulo: 'nome', obrigatorio: true, max: 100 },
      funcao: { rotulo: 'função', obrigatorio: true, max: 50 },
      contato: { rotulo: 'contato', obrigatorio: true, max: 50 }
    };
    var form = $('form-funcionario');
    var id = CG.UI.parametro('id');

    CG.Validacao.ligarValidacaoAoSair(form, REGRAS);

    if (id) {
      CG.Api.funcionarios.buscar(id).then(function (existente) {
        document.querySelector('.titulo-pagina').textContent = 'Editar Funcionário';
        $('titulo-form').textContent = 'Alterar dados de ' + existente.nome;
        form.elements.nome.value = existente.nome;
        form.elements.funcao.value = existente.funcao;
        form.elements.contato.value = existente.contato;
      }, function (erro) {
        CG.UI.avisoNaProximaPagina(erro.message, 'erro');
        window.location.replace('funcionarios.html');
      });
    }

    form.addEventListener('submit', function (evento) {
      evento.preventDefault();
      $('erro-geral').textContent = '';
      var resultado = CG.Validacao.validarFormulario(form, REGRAS);
      if (!resultado.ok) { return; }

      var dados = { nome: resultado.valores.nome, funcao: resultado.valores.funcao, contato: resultado.valores.contato };
      var botao = form.querySelector('button[type=submit]');
      botao.disabled = true;
      var envio = id ? CG.Api.funcionarios.atualizar(id, dados) : CG.Api.funcionarios.criar(dados);
      envio.then(function () {
        CG.UI.avisoNaProximaPagina(id ? 'Funcionário atualizado com sucesso.' : 'Funcionário cadastrado com sucesso.', 'sucesso');
        window.location.href = 'funcionarios.html';
      }, function (erro) {
        botao.disabled = false;
        $('erro-geral').textContent = erro.message;
      });
    });
  });
})();
