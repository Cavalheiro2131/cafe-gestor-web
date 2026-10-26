@echo off
REM Cria um historico de commits organizado por etapa do desenvolvimento.
REM Uso (dentro da pasta do projeto):  versionar.bat
REM Antes: git init  e  git branch -M main  (se ainda nao fez)

git add pom.xml .gitignore README.md src/main/resources/application.properties src/main/resources/application-memoria.properties sql
git commit -m "Estrutura Maven, configuracao do Spring Boot e scripts SQL"

git add src/main/java/model src/main/java/exception src/main/java/util src/main/java/repository src/main/java/security src/main/java/service
git commit -m "Integra o nucleo de regras de negocio (etapa 6) ao projeto web"

git add src/main/java/br/cafegestor/web/CafeGestorApplication.java src/main/java/br/cafegestor/web/config src/main/java/br/cafegestor/web/seguranca src/main/java/br/cafegestor/web/servico
git commit -m "Configuracao por perfil (JDBC e memoria), sessao, interceptor de permissoes e estoque transacional"

git add src/main/java/br/cafegestor/web/dto src/main/java/br/cafegestor/web/controller
git commit -m "API REST: DTOs, controladores e tratamento de erros"

git add src/main/resources/static
git commit -m "Integra o front-end da etapa 8 a API (api.js e scripts das paginas)"

git add src/test/java/model src/test/java/security src/test/java/service src/test/java/util
git commit -m "Testes JUnit do nucleo (etapa 7)"

git add src/test/java/br
git commit -m "Testes da API: controladores, interceptor, semente e integracao com MockMvc"

git add .github versionar.bat
git commit -m "Modelos de issue para bugtracking e script de versionamento"

echo.
echo Historico criado. Conferir com:  git log --oneline
