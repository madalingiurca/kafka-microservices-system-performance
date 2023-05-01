FROM eclipse-temurin:latest
LABEL authors="madalingiurca"

WORKDIR /app

ADD ./target/PaymentProcessor*.jar ./PaymentProcessor.jar

ENTRYPOINT ["java", "-Djdk.httpclient.HttpClient.log=requests", "-jar", "PaymentProcessor.jar"]