FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY Backend/ganesh-mandat-backend/pom.xml .
COPY Backend/ganesh-mandat-backend/src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/ganesh-mandal-backend-1.0.0.jar app.jar
EXPOSE 8081
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8081}"]
