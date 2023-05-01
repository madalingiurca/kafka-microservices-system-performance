package upb.madalingiurca.ordermanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import upb.madalingiurca.ordermanager.models.OrderStatus;

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
@RequiredArgsConstructor
@Slf4j
public class TrackingService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${order.tracking.url}")
    private String orderTrackerURL;

    public void initializeOrderTracking(UUID orderId, OrderStatus awaitingPayment) {
        ObjectNode requestBody = objectMapper.createObjectNode()
                .put("orderId", orderId.toString())
                .put("orderStatus", awaitingPayment.toString());

        var orderRequest = HttpRequest.newBuilder(URI.create(orderTrackerURL))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
        try {
            log.info("Sending request towards {} having as body {}", orderTrackerURL, orderRequest.toString());
            var responseBody = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());

            if (responseBody.statusCode() == 200) {
                log.info("Tracking initiation successful.");
                return;
            }

            log.error("Error while initializing tracking for order {}. Response status code {} with body {}", orderId, responseBody.statusCode(), responseBody.body());
            throw new ResponseStatusException(BAD_GATEWAY, "Couldn't initialize tracking");
        } catch (IOException | InterruptedException e) {
            log.error("Exception occurred", e);
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to call order tracking service", e);
        }
    }
}
