/* validacao.js - validação de formulários no navegador.
 * Cada campo é descrito por uma regra: { rotulo, obrigatorio, tipo, max, min, minimo, maximo, naoFutura, semTrim }.
 * As mensagens seguem as do núcleo Java. A validação final continua sendo do servidor. */
(function (g) {
  'use strict';
  var CG = (g.CafeGestor = g.CafeGestor || {});

  function ehDataValida(iso) {
    if (!/^\d{4}-\d{2}-\d{2}$/.test(iso)) { return false; }
    var p = iso.split('-').map(Number);
    var d = new Date(p[0], p[1] - 1, p[2]);
    return d.getFullYear() === p[0] && d.getMonth() === p[1] - 1 && d.getDate() === p[2];
  }

  /** Devolve a mensagem de erro, ou null se o valor é válido. É uma função pura (fácil de testar). */
  function validarValor(bruto, regra, hojeIso, valores) {
    var rotulo = regra.rotulo;
    var texto = (bruto === null || bruto === undefined) ? '' : String(bruto);
    var valor = regra.semTrim ? texto : texto.trim();
    var obrigatorio = typeof regra.obrigatorio === 'function' ? regra.obrigatorio(valores || {}) : regra.obrigatorio;

    if (valor === '') {
      return obrigatorio ? 'O campo ' + rotulo + ' é obrigatório.' : null;
    }

    switch (regra.tipo || 'texto') {
      case 'inteiro': {
        if (!/^-?\d+$/.test(valor)) { return 'O campo ' + rotulo + ' deve ser um número inteiro.'; }
        var n = Number(valor);
        var minimo = regra.minimo === undefined ? 1 : regra.minimo;
        if (n < minimo) {
          return minimo === 1 ? 'O campo ' + rotulo + ' deve ser maior que zero.'
                              : 'O campo ' + rotulo + ' deve ser no mínimo ' + minimo + '.';
        }
        if (regra.maximo !== undefined && n > regra.maximo) {
          return 'O campo ' + rotulo + ' deve ser no máximo ' + regra.maximo + '.';
        }
        return null;
      }
      case 'data': {
        if (!ehDataValida(valor)) { return 'O campo ' + rotulo + ' deve ser uma data válida.'; }
        if (regra.naoFutura && valor > hojeIso) { return 'A data não pode estar no futuro.'; }
        return null;
      }
      default: {
        if (regra.max && valor.length > regra.max) {
          return 'O campo ' + rotulo + ' deve ter no máximo ' + regra.max + ' caracteres.';
        }
        if (regra.min && valor.length < regra.min) {
          return 'O campo ' + rotulo + ' deve ter pelo menos ' + regra.min + ' caracteres.';
        }
        return null;
      }
    }
  }

  function mostrarErro(form, nome, mensagem) {
    var campo = form.elements[nome];
    var span = document.getElementById('erro-' + nome);
    if (span) { span.textContent = mensagem || ''; }
    if (campo) {
      campo.setAttribute('aria-invalid', mensagem ? 'true' : 'false');
      if (span) { campo.setAttribute('aria-describedby', span.id); }
      campo.classList.toggle('invalido', !!mensagem);
    }
  }

  function lerValores(form, regras) {
    var valores = {};
    Object.keys(regras).forEach(function (nome) {
      var campo = form.elements[nome];
      var bruto = campo ? campo.value : '';
      valores[nome] = regras[nome].semTrim ? bruto : bruto.trim();
    });
    return valores;
  }

  /** Valida todos os campos de uma vez. opcoes.semFoco = true evita mover o foco para o 1º campo inválido. */
  function validarFormulario(form, regras, opcoes) {
    var hoje = CG.UI.hojeIso();
    var valores = lerValores(form, regras);
    var primeiroInvalido = null;
    var convertidos = {};

    Object.keys(regras).forEach(function (nome) {
      var mensagem = validarValor(valores[nome], regras[nome], hoje, valores);
      mostrarErro(form, nome, mensagem);
      if (mensagem && !primeiroInvalido) { primeiroInvalido = nome; }
      convertidos[nome] = (regras[nome].tipo === 'inteiro' && valores[nome] !== '' && !mensagem)
        ? Number(valores[nome]) : valores[nome];
    });

    if (primeiroInvalido && !(opcoes && opcoes.semFoco)) { form.elements[primeiroInvalido].focus(); }
    return { ok: primeiroInvalido === null, valores: convertidos };
  }

  /** Valida cada campo assim que o usuário sai dele. */
  function ligarValidacaoAoSair(form, regras) {
    Object.keys(regras).forEach(function (nome) {
      var campo = form.elements[nome];
      if (!campo) { return; }
      campo.addEventListener('blur', function () {
        var valores = lerValores(form, regras);
        mostrarErro(form, nome, validarValor(valores[nome], regras[nome], CG.UI.hojeIso(), valores));
      });
    });
  }

  CG.Validacao = {
    validarValor: validarValor, validarFormulario: validarFormulario,
    ligarValidacaoAoSair: ligarValidacaoAoSair, mostrarErro: mostrarErro, ehDataValida: ehDataValida
  };
})(window);
