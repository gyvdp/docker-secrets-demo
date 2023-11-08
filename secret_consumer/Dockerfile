FROM openjdk:11
# Set the working directory
WORKDIR /app
# Copy the pom.xml file and download the project dependencies
COPY ./target/secret_consumer-1.0-SNAPSHOT-jar-with-dependencies.jar .

CMD ["java", "-jar", "secret_consumer-1.0-SNAPSHOT-jar-with-dependencies.jar"]