/* pagina-login.js - tela de login */
(function () {
  'use strict';
  var CG = window.CafeGestor;
  var $ = function (id) { return document.getElementById(id); };

  var REGRAS = {
    login: { rotulo: 'login', obrigatorio: true, max: 50 },
    senha: { rotulo: 'senha', obrigatorio: true, semTrim: true }
  };
  var form = $('form-login');

  // Quem já está logado vai direto para o início.
  CG.Api.sessaoAtual({ semRedirecionar: true }).then(function () {
    window.location.replace('inicio.html');
  }, function () { /* sem sessão: continua no login */ });

  var motivo = CG.UI.parametro('motivo');
  var mensagens = { login: 'Faça login para continuar.', sair: 'Sessão encerrada com sucesso.' };
  if (mensagens[motivo]) {
    $('aviso-login').textContent = mensagens[motivo];
    $('aviso-login').hidden = false;
  }

  CG.Validacao.ligarValidacaoAoSair(form, REGRAS);

  $('mostrar-senha').addEventListener('change', function (evento) {
    $('senha').type = evento.target.checked ? 'text' : 'password';
  });

  form.addEventListener('submit', function (evento) {
    evento.preventDefault();
    $('erro-geral').textContent = '';
    var resultado = CG.Validacao.validarFormulario(form, REGRAS);
    if (!resultado.ok) { return; }

    var botao = form.querySelector('button[type=submit]');
    botao.disabled = true;
    CG.Api.login(resultado.valores.login, resultado.valores.senha).then(
      function () { window.location.href = 'inicio.html'; },
      function (erro) {
        botao.disabled = false;
        $('erro-geral').textContent = erro.message;
        $('senha').value = '';
        $('senha').focus();
      }
    );
  });
})();
