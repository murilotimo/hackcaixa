# Define a imagem base
FROM openjdk:8-jdk

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR para o diretório de trabalho
COPY srcJava/simulador/target/simulador-0.0.1-SNAPSHOT.jar .

# Define o comando de execução da aplicação
CMD ["java", "-jar", "simulador-0.0.1.jar"]