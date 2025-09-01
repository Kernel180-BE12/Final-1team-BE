# /docker/production/Dockerfile
FROM eclipse-temurin:24-jre

WORKDIR /app

# repo 루트의 build/libs/*.jar 를 복사
COPY build/libs/*.jar app.jar

EXPOSE 8080

# 비루트 사용자로 실행 (보안 권장)
USER 10001

# JVM 기본 최적화 옵션
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=25.0 -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["java","-jar","/app/app.jar"]
