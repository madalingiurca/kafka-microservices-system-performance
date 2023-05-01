package upb.madalingiurca.ordermanager.models.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import upb.madalingiurca.ordermanager.models.OrderStatus;

import java.util.UUID;

public record OrderStatusUpdate(UUID orderId, OrderStatus orderStatus) {
    @JsonCreator
    public OrderStatusUpdate(
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("newStatus") OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }
}
