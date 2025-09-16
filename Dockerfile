# --- Stage 1: The Build Stage ---
# Use an official Maven image with Java 17 (Temurin is a great open-source distribution)
# We give this stage a name, "build", so we can refer to it later.
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to leverage Docker's layer caching.
# This way, dependencies are only re-downloaded if the pom.xml changes.
COPY pom.xml .

# Download all the dependencies into a separate layer
RUN mvn dependency:go-offline

# Copy the rest of the application's source code
COPY src ./src

# Package the application into an executable .jar file.
# We skip the tests because they aren't needed for the final production build.
RUN mvn clean package -DskipTests


# --- Stage 2: The Final Production Image ---
# Use a lightweight Java Runtime Environment (JRE) image. This is much smaller
# than a full JDK, making our final image more efficient and secure.
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the compiled .jar file from the 'build' stage into this new stage
COPY --from=build /app/target/*.jar app.jar

# Tell Docker that the container will listen on port 8080 at runtime
EXPOSE 8080

# The command that will run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]