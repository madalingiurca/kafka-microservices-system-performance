package upb.madalingiurca.ordermanager.service.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(HttpClient.class)
public class PaymentService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${payments.processor.url}")
    private String paymentsProcessorURL;

    public UUID initiatePaymentRequest(UUID orderId, Integer amount) {
        var uri = URI.create(paymentsProcessorURL.replace("{id}", orderId.toString()));
        ObjectNode requestBody = objectMapper.createObjectNode()
                .put("amount", amount)
                .put("orderId", orderId.toString());

        var orderRequest = HttpRequest.newBuilder(uri)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
        try {
            log.debug("Sending request towards {} having as body {}", uri, orderRequest.toString());
            var responseBody = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());

            if (responseBody.statusCode() == 200) {
                log.debug("Payment initiation successful: {}", responseBody);
                return UUID.fromString(objectMapper.readTree(responseBody.body()).at("/id").asText());
            }

            log.error("Error while initiating payment for order {}. Response status code {} with body {}", orderId, responseBody.statusCode(), responseBody.body());
            throw new ResponseStatusException(BAD_GATEWAY, "Couldn't initiate payment");
        } catch (IOException | InterruptedException e) {
            log.error("Exception occurred", e);
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to call order manager service", e);
        }
    }
}
