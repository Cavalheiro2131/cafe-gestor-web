/* util.js - funções de apoio à interface: criar elementos, datas, avisos e diálogo de confirmação */
(function (g) {
  'use strict';
  var CG = (g.CafeGestor = g.CafeGestor || {});
  var CHAVE_AVISO = 'cafegestor.aviso';

  /** Cria um elemento sem usar innerHTML (o texto nunca é interpretado como HTML). */
  function criar(tag, atributos, filhos) {
    var no = document.createElement(tag);
    Object.keys(atributos || {}).forEach(function (chave) {
      var valor = atributos[chave];
      if (valor === null || valor === undefined || valor === false) { return; }
      if (chave === 'classe') { no.className = valor; }
      else if (chave === 'texto') { no.textContent = valor; }
      else { no.setAttribute(chave, valor === true ? '' : valor); }
    });
    (filhos || []).forEach(function (filho) {
      no.appendChild(typeof filho === 'string' ? document.createTextNode(filho) : filho);
    });
    return no;
  }

  function limpar(no) {
    while (no.firstChild) { no.removeChild(no.firstChild); }
  }

  function dois(n) { return String(n).padStart(2, '0'); }

  /** Data no formato aaaa-mm-dd, deslocada em N dias a partir de hoje (0 = hoje). */
  function dataIso(deslocamentoDias) {
    var d = new Date();
    d.setDate(d.getDate() + (deslocamentoDias || 0));
    return d.getFullYear() + '-' + dois(d.getMonth() + 1) + '-' + dois(d.getDate());
  }

  function hojeIso() { return dataIso(0); }

  /** aaaa-mm-dd -> dd/mm/aaaa */
  function dataBr(iso) {
    if (!iso) { return ''; }
    var p = iso.split('-');
    return p[2] + '/' + p[1] + '/' + p[0];
  }

  function parametro(nome) {
    return new URLSearchParams(g.location.search).get(nome);
  }

  function containerAvisos() {
    var c = document.getElementById('avisos');
    if (!c) {
      c = criar('div', { id: 'avisos', classe: 'avisos', 'aria-live': 'polite' });
      document.body.appendChild(c);
    }
    return c;
  }

  /** Mostra uma mensagem temporária. tipo: 'info' (padrão), 'sucesso' ou 'erro'. */
  function aviso(mensagem, tipo) {
    var item = criar('p', { classe: 'aviso aviso-' + (tipo || 'info'), role: 'status', texto: mensagem });
    containerAvisos().appendChild(item);
    setTimeout(function () { if (item.parentNode) { item.parentNode.removeChild(item); } }, 6000);
  }

  /** Guarda uma mensagem para ser exibida depois do redirecionamento. */
  function avisoNaProximaPagina(mensagem, tipo) {
    try { sessionStorage.setItem(CHAVE_AVISO, JSON.stringify({ mensagem: mensagem, tipo: tipo })); } catch (e) { /* sem armazenamento */ }
  }

  function mostrarAvisoPendente() {
    try {
      var texto = sessionStorage.getItem(CHAVE_AVISO);
      if (!texto) { return; }
      sessionStorage.removeItem(CHAVE_AVISO);
      var a = JSON.parse(texto);
      aviso(a.mensagem, a.tipo);
    } catch (e) { /* ignora */ }
  }

  function preencherSelect(select, itens, textoVazio, valorDe, textoDe) {
    limpar(select);
    if (textoVazio !== null) { select.appendChild(criar('option', { value: '', texto: textoVazio })); }
    itens.forEach(function (item) {
      select.appendChild(criar('option', { value: String(valorDe(item)), texto: textoDe(item) }));
    });
  }

  /** Diálogo de confirmação acessível (usa <dialog>; cai para window.confirm em navegadores antigos). */
  function confirmar(mensagem, textoConfirmar) {
    return new Promise(function (resolver) {
      if (typeof HTMLDialogElement === 'undefined' || !document.createElement('dialog').showModal) {
        resolver(g.confirm(mensagem));
        return;
      }
      var resolvido = false;
      var dialogo = criar('dialog', { classe: 'dialogo' });
      var cancelar = criar('button', { type: 'button', classe: 'btn btn-secundario', texto: 'Cancelar' });
      var ok = criar('button', { type: 'button', classe: 'btn btn-perigo', texto: textoConfirmar || 'Confirmar' });
      dialogo.appendChild(criar('p', { texto: mensagem }));
      dialogo.appendChild(criar('div', { classe: 'acoes-form' }, [cancelar, ok]));

      function fechar(resposta) {
        if (resolvido) { return; }
        resolvido = true;
        if (dialogo.open) { dialogo.close(); }
        if (dialogo.parentNode) { dialogo.parentNode.removeChild(dialogo); }
        resolver(resposta);
      }
      cancelar.addEventListener('click', function () { fechar(false); });
      ok.addEventListener('click', function () { fechar(true); });
      dialogo.addEventListener('cancel', function () { fechar(false); }); // tecla Esc
      document.body.appendChild(dialogo);
      dialogo.showModal();
      cancelar.focus();
    });
  }

  CG.UI = {
    criar: criar, limpar: limpar, dataIso: dataIso, hojeIso: hojeIso, dataBr: dataBr,
    parametro: parametro, aviso: aviso, avisoNaProximaPagina: avisoNaProximaPagina,
    mostrarAvisoPendente: mostrarAvisoPendente, preencherSelect: preencherSelect, confirmar: confirmar
  };
})(window);
