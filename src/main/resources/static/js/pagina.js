/* pagina.js - comportamento comum das páginas internas: confirma o login no servidor, monta o menu por perfil e liga o botão Sair */
(function (g) {
  'use strict';
  var CG = (g.CafeGestor = g.CafeGestor || {});
  var UI = CG.UI;

  var CONSULTAS = [
    { id: 'inicio', texto: 'Início', href: 'inicio.html' },
    { id: 'funcionarios', texto: 'Funcionários', href: 'funcionarios.html' },
    { id: 'tipos-cafe', texto: 'Tipos de Café', href: 'tipos-cafe.html' },
    { id: 'balaios', texto: 'Balaios', href: 'balaios.html' },
    { id: 'estoque', texto: 'Estoque', href: 'estoque.html' }
  ];
  var OPERACOES = [
    { id: 'funcionario-form', texto: 'Cadastro de Funcionário', href: 'funcionario-form.html' },
    { id: 'tipo-cafe-form', texto: 'Cadastro de Tipo de Café', href: 'tipo-cafe-form.html' },
    { id: 'balaio-form', texto: 'Registro de Balaios', href: 'balaio-form.html' },
    { id: 'estoque-movimentacao', texto: 'Controle de Estoque', href: 'estoque-movimentacao.html' }
  ];

  function grupo(titulo, itens, paginaAtual) {
    var lista = UI.criar('ul');
    itens.forEach(function (item) {
      var link = UI.criar('a', { href: item.href, texto: item.texto });
      if (item.id === paginaAtual) { link.setAttribute('aria-current', 'page'); }
      lista.appendChild(UI.criar('li', {}, [link]));
    });
    return [UI.criar('p', { classe: 'menu-titulo', texto: titulo }), lista];
  }

  function montarMenu(usuario) {
    var nav = document.getElementById('menu-nav');
    var atual = document.body.getAttribute('data-pagina');
    UI.limpar(nav);
    grupo('Consultas', CONSULTAS, atual).forEach(function (no) { nav.appendChild(no); });
    if (CG.Sessao.podeAlterar(usuario)) {
      grupo('Operações', OPERACOES, atual).forEach(function (no) { nav.appendChild(no); });
    }
    document.getElementById('menu-perfil').textContent = usuario.perfil;
    document.getElementById('menu-usuario').textContent = usuario.nome;
  }

  function ligarEventos() {
    var menu = document.getElementById('menu');
    var botao = document.getElementById('menu-toggle');
    botao.addEventListener('click', function () {
      var aberto = menu.classList.toggle('aberto');
      botao.setAttribute('aria-expanded', aberto ? 'true' : 'false');
    });
    document.getElementById('btn-sair').addEventListener('click', CG.Sessao.sair);
  }

  /**
   * Pergunta ao servidor quem está logado. Devolve uma Promise com o usuário, ou com null quando a página
   * foi redirecionada (sem login, ou perfil sem permissão): nesse caso o script da página deve parar.
   */
  function iniciar() {
    return CG.Api.sessaoAtual().then(
      function (usuario) {
        if (document.body.hasAttribute('data-exige-edicao') && !CG.Sessao.podeAlterar(usuario)) {
          UI.avisoNaProximaPagina('Seu perfil só permite consultas.', 'erro');
          g.location.replace('inicio.html');
          return null;
        }
        montarMenu(usuario);
        ligarEventos();
        UI.mostrarAvisoPendente();
        return usuario;
      },
      function (erro) {
        // 401: a API já levou ao login. Outros erros (servidor fora do ar): avisa e para.
        if (erro.status !== 401) { UI.aviso(erro.message, 'erro'); }
        return null;
      }
    );
  }

  CG.Pagina = { iniciar: iniciar };
})(window);
