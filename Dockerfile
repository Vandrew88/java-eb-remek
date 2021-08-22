FROM openjdk:11-slim
WORKDIR /app
COPY ./target/java-eb-remek-0.0.1-SNAPSHOT.jar /app/remek.jar
EXPOSE 8080
CMD "java" "-jar" "/app/remek.jar"