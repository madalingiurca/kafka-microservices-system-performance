package tech.madalingiurca.paymentprocessor.service.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.paymentprocessor.model.PaymentStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static tech.madalingiurca.paymentprocessor.model.PaymentStatus.APPROVED;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!kafka")
public class TrackingService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${order.tracking.url}")
    private String orderTrackerURL;

    public void updateTracking(UUID orderId, PaymentStatus paymentStatus) {
        var requestURI = URI.create(orderTrackerURL.replace("{id}", orderId.toString()));
        var requestBody = objectMapper.createObjectNode()
                .put("orderStatus", paymentStatus == APPROVED ? "PAYMENT_CONFIRMED" : "AWAITING_PAYMENT");

        var orderRequest = HttpRequest.newBuilder(requestURI)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
        try {
            log.debug("Sending request towards {} having as body {}", orderTrackerURL, requestBody);
            var responseBody = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());

            if (responseBody.statusCode() == 200) {
                log.debug("Tracking initiation successful.");
                return;
            }

            log.error("Error while updating tracking for order {}. Response status code {} with body {}", orderId, responseBody.statusCode(), responseBody.body());
            throw new ResponseStatusException(BAD_GATEWAY, "Couldn't update tracking");
        } catch (IOException | InterruptedException e) {
            log.error("Exception occurred", e);
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to call order tracking service", e);
        }
    }
}
