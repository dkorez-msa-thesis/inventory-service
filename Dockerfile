FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /build
COPY catalog-common-lib /build/catalog-common-lib
RUN cd /build/catalog-common-lib && mvn clean install -DskipTests

COPY inventory-service/src /build/src
COPY inventory-service/pom.xml /build
RUN mvn clean package -DskipTests

FROM registry.access.redhat.com/ubi9/openjdk-17:1.20
WORKDIR /app

COPY --from=builder --chown=185 /build/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder --chown=185 /build/target/quarkus-app/*.jar /deployments/app.jar
COPY --from=builder --chown=185 /build/target/quarkus-app/app/ /deployments/app/
COPY --from=builder --chown=185 /build/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

CMD ["java", "-jar", "/deployments/app.jar"]