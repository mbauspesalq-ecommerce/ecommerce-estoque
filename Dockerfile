# Etapa de construção
FROM openjdk:17-jdk-slim as build

# Copiar o arquivo JAR gerado para o container
COPY target/estoque-*.jar /app/estoque.jar

# Etapa de execução
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar o arquivo JAR da etapa de construção
COPY --from=build /app/estoque.jar /app/estoque.jar

# Expor a porta da aplicação
EXPOSE 8000

# Comando para rodar a aplicação
CMD ["java", "-jar", "estoque.jar"]