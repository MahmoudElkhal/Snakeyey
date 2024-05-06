# Use a base image with JDK and Maven installed
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project's POM file
COPY pom.xml .

# Download dependencies and build the project
RUN mvn dependency:go-offline

# Copy the entire project source code
COPY src ./src

# Build the project
RUN mvn package

# Use a lightweight base image to run the application
FROM openjdk:17-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/g-1.0-SNAPSHOT.jar .

# Command to run the application
CMD ["java", "-jar", "g-1.0-SNAPSHOT.jar"]
