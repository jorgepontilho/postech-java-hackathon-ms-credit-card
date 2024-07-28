# postech-java-ms-credit-card
Fiap - Pós Tech - Tech Challenge 5 - ms-credit-card

Este projeto é uma API desenvolvida com Java e Spring Boot, designada para Gerenciamento de Usuários

## Tecnologias Utilizadas

Este projeto foi desenvolvido com as seguintes tecnologias e bibliotecas:

- **Java 17**: Versão do Java utilizada no projeto.
- **Spring Boot**: Framework principal para a criação de aplicações Spring.
- **Spring Boot DevTools**: Para desenvolvimento rápido com reinício automático.
- **Spring Boot Starter Security**: Para autenticação e segurança da aplicação.
- **Spring Boot Starter Web**: Para construção de aplicações web, usando o Spring MVC.
- **Spring Boot Starter Data Jpa**: Para auxílio na persistência no Banco de Dados.
- **Spring Boot Validation**: Para validação de campos.
- **Springdoc OpenAPI**: Para documentação da API REST com Swagger.
- **Spring Maven**: Para gestão de dependências.
- **JWT - JSON Web Token - auth0**: Para gestão do token de segurança. 
- **Lombok**: Para reduzir o código boilerplate, como getters e setters.
- **MySQL**: banco de dados relacional.
- **Docker**: utilização de container para criação de ambiente de execução local

### Execução das aplicações
Este repositório contém uma aplicação e um banco de dados.
Porém, sua execução pode depender de outras aplicações, então sugerimos os passos abaixo para garantir que os containers estejam rodando antes de iniciar os testes.<br>
Isso inclui a criação de uma rede no docker para que os containers possam se comunicar.<br>
O comando abaixo para a criação de rede é necessário somente para o primeiro container, os demais vão assumir essa mesma rede.
````shell
docker network create my_network -d bridge
````
Após a criação da rede, precisamos executar o comando abaixo para fazer o build da aplicação e também do banco de dados. Este comando é responsável por executar o arquivo compose, e este por sua vez fará o build da aplicação usando o Dockerfile, e também irá baixar, configurar e executar o banco de dados.
````shell
docker-compose up --build
````

## Documentação

A documentação detalhada da API está acessível na URL:
http://localhost:8080/swagger-ui/index.html


## Portas configuradas

App: http://localhost:8080

MySQL: jdbc:mysql://localhost:3306/creditcarddb
