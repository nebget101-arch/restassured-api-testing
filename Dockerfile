FROM maven:3.8.1-openjdk-11

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:resolve dependency:copy-dependencies

# Copy the entire project
COPY . .

# Build project (without running tests)
RUN mvn clean compile -DskipTests

# Run tests and generate reports
CMD ["mvn", "test", "-X"]
