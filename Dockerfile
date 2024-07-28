#Usando a imagem oficial do Maven para compilar o projeto Java
FROM maven:3.8.4-openjdk-17-slim AS build

# Definindo o diretório de trabalho dentro do container
WORKDIR /app

# Copiando o arquivo POM e os arquivos fonte do projeto
COPY pom.xml .
COPY src ./src

# Compilando o projeto com Maven
RUN mvn clean install -DskipTests


#Usando uma imagem base JDK para executar a aplicação Java
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar /app/mscreditcard.jar

CMD ["java", "-jar", "/app/mscreditcard.jar"]