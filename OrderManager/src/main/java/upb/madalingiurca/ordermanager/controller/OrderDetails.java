package upb.madalingiurca.ordermanager.controller;

import upb.madalingiurca.ordermanager.models.OrderStatus;

import java.util.List;
import java.util.UUID;

public record OrderDetails(UUID id, OrderStatus orderStatus, UUID paymentReference, String address, int amount,
                           List<String> products) {
}
