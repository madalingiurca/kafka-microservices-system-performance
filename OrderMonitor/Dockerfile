FROM eclipse-temurin:latest
LABEL authors="madalingiurca"

WORKDIR /app

ADD ./target/ordermonitor*.jar ./OrderMonitor.jar

ENTRYPOINT ["java", "-jar", "OrderMonitor.jar"]