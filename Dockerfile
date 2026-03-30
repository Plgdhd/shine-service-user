FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY . .


RUN --mount=type=cache,target=/root/.m2 \
    mvn package -pl user-service -am -DskipTests -q

RUN java -Djarmode=layertools -jar user-service/target/user-service-*.jar extract --destination user-service/target/extracted

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -g 1001 -S appgroup && adduser -u 1001 -S appuser -G appgroup
WORKDIR /app

COPY --from=builder --chown=appuser:appgroup /app/user-service/target/extracted/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/user-service/target/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /app/user-service/target/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/user-service/target/extracted/application/ ./

USER appuser
EXPOSE 8082
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}", "org.springframework.boot.loader.launch.JarLauncher"]