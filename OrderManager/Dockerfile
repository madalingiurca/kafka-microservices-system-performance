FROM eclipse-temurin:latest
LABEL authors="madalingiurca"

WORKDIR /app

ADD ./target/OrderManager*.jar ./OrderManager.jar

ENTRYPOINT ["java", "-Djdk.httpclient.HttpClient.log=requests", "-jar", "OrderManager.jar"]