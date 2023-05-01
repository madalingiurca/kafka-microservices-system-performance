package upb.madalingiurca.ordermanager.service;

import upb.madalingiurca.ordermanager.models.OrderStatus;
import upb.madalingiurca.ordermanager.models.document.OrderDocument;
import upb.madalingiurca.ordermanager.models.http.NewOrderRequest;
import upb.madalingiurca.ordermanager.models.http.OrderDetails;
import upb.madalingiurca.ordermanager.models.http.OrderStatusUpdate;

import java.util.UUID;

public interface OrderService {
    OrderDocument newOrder(NewOrderRequest newOrderRequest);

    OrderStatus updateOrder(OrderStatusUpdate orderStatusUpdate);

    OrderDetails getOrderDetails(UUID id);
}
