FROM eclipse-temurin:latest
LABEL authors="madalingiurca"

WORKDIR /app

ADD ./target/ordermonitor*.jar ./OrderMonitor.jar

ENTRYPOINT ["java", "-Djdk.httpclient.HttpClient.log=requests", "-jar", "OrderMonitor.jar"]