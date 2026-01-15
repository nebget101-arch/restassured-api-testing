FROM maven:3.8.1-openjdk-11

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:resolve

# Copy the entire project
COPY . .

# Run tests
RUN mvn clean test

# Create a stage for test results
FROM scratch
COPY --from=0 /app/target/surefire-reports /reports
