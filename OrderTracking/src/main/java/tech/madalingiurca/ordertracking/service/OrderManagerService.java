package tech.madalingiurca.ordertracking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordertracking.model.OrderStatus;

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
public class OrderManagerService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${order.manager.url}")
    private String orderManagerURL;

    public void updateOrderDetails(UUID orderId, OrderStatus orderStatus) {
        var requestBody = objectMapper.createObjectNode()
                .put("orderId", orderId.toString())
                .put("newStatus", orderStatus.toString());

        var orderRequest = HttpRequest.newBuilder(URI.create(orderManagerURL))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<String> responseBody = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());
            if (responseBody.statusCode() == 200) {
                log.info("Status of order successfully updated: {}", responseBody.body());
                return;
            }

            log.error("Error while retrieving order {}. Response status code {} with body {}", orderId, responseBody.statusCode(), responseBody.body());
        } catch (IOException | InterruptedException e) {
            log.error("Exception occurred", e);
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to call order manager service", e);
        }
    }
}
