package tech.madalingiurca.ordermonitor.service;

import tech.madalingiurca.ordermonitor.model.http.OrderInfoResponse;

import java.util.UUID;

public interface OrderRetrieveService {

    OrderInfoResponse getOrderDetails(UUID id);
}
