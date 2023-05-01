package tech.madalingiurca.ordermonitor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordermonitor.model.OrderDetails;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderManagerService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${order.manager.url}")
    private String orderManagerURL;

    public Optional<OrderDetails> getOrderDetails(UUID orderId) {
        var uri = URI.create(orderManagerURL.replace("{id}", orderId.toString()));

        var orderRequest = HttpRequest.newBuilder(uri).build();
        try {
            HttpResponse<String> responseBody = httpClient.send(orderRequest, HttpResponse.BodyHandlers.ofString());

            if (responseBody.statusCode() == 200) {
                log.info("Order retrieved: {}", responseBody);
                return Optional.of(objectMapper.readValue(responseBody.body(), OrderDetails.class));
            }

            log.error("Error while retrieving order {}. Response status code {} with body {}", orderId, responseBody.statusCode(), responseBody.body());
            return Optional.empty();
        } catch (IOException | InterruptedException e) {
            log.error("Exception occurred", e);
            throw new ResponseStatusException(BAD_GATEWAY, "Failed to call order manager service", e);
        }
    }
}
