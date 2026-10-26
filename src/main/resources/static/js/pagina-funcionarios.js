/* pagina-funcionarios.js - lista de funcionários com busca, filtro de situação e inativação */
(function () {
  'use strict';
  var CG = window.CafeGestor;
  var UI = CG.UI;
  var $ = function (id) { return document.getElementById(id); };

  CG.Pagina.iniciar().then(function (usuario) {
    if (!usuario) { return; }
    var podeAlterar = CG.Sessao.podeAlterar(usuario);
    var todos = [];
    $('btn-novo').hidden = !podeAlterar;
    $('col-acoes').hidden = !podeAlterar;

    function filtrar() {
      var texto = $('filtro-texto').value.trim().toLowerCase();
      var situacao = $('filtro-status').value;
      return todos.filter(function (f) {
        var combina = !texto || f.nome.toLowerCase().indexOf(texto) >= 0 || f.funcao.toLowerCase().indexOf(texto) >= 0;
        var situacaoOk = situacao === 'todos' || (situacao === 'ativos' ? f.ativo : !f.ativo);
        return combina && situacaoOk;
      });
    }

    function celula(texto) { return UI.criar('td', { texto: texto }); }

    function alternarStatus(f) {
      var acao = f.ativo ? 'Inativar' : 'Reativar';
      UI.confirmar(acao + ' o funcionário ' + f.nome + '?', acao).then(function (ok) {
        if (!ok) { return; }
        CG.Api.funcionarios.alterarStatus(f.id, !f.ativo).then(function () {
          UI.aviso('Funcionário ' + (f.ativo ? 'inativado' : 'reativado') + '.', 'sucesso');
          carregar();
        }, function (erro) { UI.aviso(erro.message, 'erro'); });
      });
    }

    function linha(f) {
      var tr = UI.criar('tr');
      tr.appendChild(celula(f.nome));
      tr.appendChild(celula(f.funcao));
      tr.appendChild(celula(f.contato));
      tr.appendChild(UI.criar('td', {}, [
        UI.criar('span', { classe: 'selo ' + (f.ativo ? 'selo-ativo' : 'selo-inativo'), texto: f.ativo ? 'Ativo' : 'Inativo' })
      ]));
      if (podeAlterar) {
        var botao = UI.criar('button', {
          type: 'button', classe: 'btn btn-pequeno ' + (f.ativo ? 'btn-secundario' : 'btn-primario'),
          texto: f.ativo ? 'Inativar' : 'Reativar', 'aria-label': (f.ativo ? 'Inativar ' : 'Reativar ') + f.nome
        });
        botao.addEventListener('click', function () { alternarStatus(f); });
        tr.appendChild(UI.criar('td', { classe: 'acoes' }, [
          UI.criar('a', {
            classe: 'btn btn-pequeno btn-secundario', href: 'funcionario-form.html?id=' + f.id,
            texto: 'Editar', 'aria-label': 'Editar ' + f.nome
          }),
          botao
        ]));
      }
      return tr;
    }

    function desenhar() {
      var corpo = $('tabela-corpo');
      var lista = filtrar();
      UI.limpar(corpo);
      if (lista.length === 0) {
        corpo.appendChild(UI.criar('tr', {}, [UI.criar('td', { classe: 'vazio', colspan: podeAlterar ? 5 : 4, texto: 'Nenhum funcionário encontrado.' })]));
      } else {
        lista.forEach(function (f) { corpo.appendChild(linha(f)); });
      }
      $('contagem').textContent = lista.length + ' funcionário(s) listado(s).';
    }

    function carregar() {
      CG.Api.funcionarios.listar().then(function (lista) {
        todos = lista;
        desenhar();
      }, function (erro) { UI.aviso(erro.message, 'erro'); });
    }

    $('filtro-texto').addEventListener('input', desenhar);
    $('filtro-status').addEventListener('change', desenhar);
    carregar();
  });
})();
