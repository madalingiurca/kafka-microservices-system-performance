package tech.madalingiurca.ordertracking.model.event;

import tech.madalingiurca.ordertracking.model.OrderStatus;

import java.io.Serializable;
import java.util.UUID;

public record UpdateOrderEvent(UUID orderId, OrderStatus newStatus) implements Serializable {
}
