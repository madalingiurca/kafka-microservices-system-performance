package tech.madalingiurca.ordertracking.model.document;

import org.springframework.data.annotation.Id;
import tech.madalingiurca.ordertracking.model.OrderStatus;

import java.util.UUID;

public record TrackedOrderDocument(@Id UUID orderId, OrderStatus orderStatus, UUID paymentReference) {
}
