package tech.madalingiurca.ordermonitor.service.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordermonitor.model.PaymentDetails;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@RequiredArgsConstructor
@Service
@Slf4j
@Profile("!kafka")
public class PaymentsQueryService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${payments.processor.url}")
    private String paymentsProcessorURL;

    public Optional<PaymentDetails> getPaymentDetails(UUID paymentId) {
        var uri = URI.create(paymentsProcessorURL.replace("{id}", paymentId.toString()));

        var orderRequest = HttpRequest.newBuilder(uri).build();
        try {
            HttpResponse<String> responseBody = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());

            if (responseBody.statusCode() == 200) {
                log.info("Payment details retrieved: {}", responseBody);
                return Optional.of(objectMapper.readValue(responseBody.body(), PaymentDetails.class));
            }

            log.error("Error while retrieving payment {}. Response status code {} with body {}", paymentId, responseBody.statusCode(), responseBody.body());
            return Optional.empty();
        } catch (IOException | InterruptedException e) {
            log.error("Exception occurred", e);
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to call order manager service", e);
        }
    }
}
