FROM eclipse-temurin:latest
LABEL authors="madalingiurca"

WORKDIR /app

ADD ./target/OrderTracking*.jar ./OrderTracking.jar

ENTRYPOINT ["java", "-jar", "OrderTracking.jar"]