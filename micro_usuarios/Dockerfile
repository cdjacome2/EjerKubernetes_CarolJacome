# Etapa 1: Construcción del JAR
FROM openjdk:17.0.2 AS builder
WORKDIR /app

# Copiamos Maven Wrapper y dependencias para que queden en caché
COPY ./pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Descarga dependencias para que la compilación sea más rápida en el futuro
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copiamos el código fuente y construimos el JAR
COPY ./src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen final con solo el JAR
FROM openjdk:17.0.2
WORKDIR /app

# Copiamos el JAR desde la etapa de construcción
COPY --from=builder /app/target/micro_usuarios-0.0.1-SNAPSHOT.jar app.jar

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
