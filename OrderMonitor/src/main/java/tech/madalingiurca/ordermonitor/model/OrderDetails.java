package tech.madalingiurca.ordermonitor.model;

import java.util.List;
import java.util.UUID;

public record OrderDetails(UUID id, String orderStatus, UUID paymentReference, String address, int amount,
                           List<String> products) {
}
