package tech.madalingiurca.ordertracking.model.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tech.madalingiurca.ordertracking.model.OrderStatus;

import java.util.UUID;

public record OrderTrackingRequest(UUID orderId, OrderStatus orderStatus) {
    @JsonCreator
    public OrderTrackingRequest(
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("orderStatus") OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }
}
