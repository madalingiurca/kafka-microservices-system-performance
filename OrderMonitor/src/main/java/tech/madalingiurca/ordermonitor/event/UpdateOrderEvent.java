package tech.madalingiurca.ordermonitor.event;


import tech.madalingiurca.ordermonitor.model.OrderStatus;

import java.io.Serializable;
import java.util.UUID;

public record UpdateOrderEvent(UUID orderId, OrderStatus newStatus) implements Serializable {
}
