FROM eclipse-temurin:latest
LABEL authors="madalingiurca"

WORKDIR /app

ADD ./target/OrderTracking*.jar ./OrderTracking.jar

ENTRYPOINT ["java", "-Djdk.httpclient.HttpClient.log=requests", "-jar", "OrderTracking.jar"]