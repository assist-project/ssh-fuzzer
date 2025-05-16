FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
RUN git clone https://github.com/protocol-fuzzing/protocol-state-fuzzer.git psf \
    && \
    cd psf && bash install.sh && cd /app
COPY pom.xml .
COPY src ./src
RUN mvn -X clean package -DskipTests




FROM bitnami/java:17
WORKDIR /app
COPY --from=build /app/target/ssh-learner.jar ./ssh-learner.jar
COPY --from=build /app/target/lib/ ./lib/


ENTRYPOINT ["java", "-cp", "./ssh-learner.jar:./lib/*"]
