-- Dados iniciais (opcional). Rode depois de schema.sql.
-- Contas: admin / admin123 (ADMIN), gerente / gerente123 (GERENTE), operador / operador123 (OPERADOR).
-- As senhas estão gravadas com PBKDF2 (nunca em texto puro). Troque-as em uso real.
USE cafe_gestor;

INSERT INTO usuario (nome, login, senha, tipo) VALUES
  ('Administrador',      'admin',    'pbkdf2$65536$SUxuirTb0q9dBOmPyEdNwQ==$VpfXKCB66aGFIh0CrKugPcxbkKIesU8f8aEitWgqO5U=', 'ADMIN'),
  ('Gerente da Fazenda', 'gerente',  'pbkdf2$65536$/pf4JE9JOnkc7yz4Cc+bBg==$aK/TzdrZ05GSbVyIBHllCa4F/rZkroXWHJBotGOOnQE=', 'GERENTE'),
  ('Operador',           'operador', 'pbkdf2$65536$kQbHqAkuMusB3cJiwxXHEw==$kdcXr3pTFiy1bpx6012t4C0vp8imeD82VrloHh27ouA=', 'OPERADOR');

INSERT INTO tipo_cafe (nome, variedade, observacoes) VALUES
  ('Arábica', 'Especial', 'Lote principal da safra'),
  ('Conilon', 'Robusta',  NULL),
  ('Catuaí',  'Vermelho', 'Talhão novo');

INSERT INTO funcionario (nome, funcao, contato, status) VALUES
  ('João Silva',  'Colhedor',    '99999-9999', TRUE),
  ('Maria Souza', 'Colhedora',   '98888-7777', TRUE),
  ('Pedro Alves', 'Encarregado', '97777-6666', FALSE);
