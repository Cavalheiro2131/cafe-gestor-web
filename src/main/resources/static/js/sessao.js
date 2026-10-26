/* sessao.js - perfil do usuário e saída do sistema. O login e a sessão ficam no servidor (cookie de sessão);
 * aqui só há atalhos para a interface. As permissões de verdade são checadas pela API (401 e 403). */
(function (g) {
  'use strict';
  var CG = (g.CafeGestor = g.CafeGestor || {});

  /** Mesma regra de PerfilUsuario.podeAlterarDados() do núcleo Java (usada só para esconder botões e menus). */
  function podeAlterar(usuario) {
    return !!usuario && (usuario.perfil === 'ADMIN' || usuario.perfil === 'GERENTE');
  }

  function sair() {
    var voltar = function () { g.location.href = 'index.html?motivo=sair'; };
    CG.Api.sair().then(voltar, voltar);
  }

  CG.Sessao = { podeAlterar: podeAlterar, sair: sair };
})(window);
