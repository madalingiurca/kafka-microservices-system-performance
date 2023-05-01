package upb.madalingiurca.ordermanager.service.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import upb.madalingiurca.ordermanager.models.OrderStatus;
import upb.madalingiurca.ordermanager.models.document.OrderDocument;
import upb.madalingiurca.ordermanager.models.http.NewOrderRequest;
import upb.madalingiurca.ordermanager.models.http.OrderDetails;
import upb.madalingiurca.ordermanager.models.http.OrderStatusUpdate;
import upb.madalingiurca.ordermanager.repository.OrderRepository;
import upb.madalingiurca.ordermanager.service.OrderService;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static upb.madalingiurca.ordermanager.models.OrderStatus.AWAITING_PAYMENT;

@Service
@Profile("!kafka")
@Slf4j
@RequiredArgsConstructor
public class RestOrderService implements OrderService {

    private final PaymentService paymentService;
    private final TrackingService trackingService;
    private final OrderRepository orderRepository;

    @Override
    public OrderDocument newOrder(NewOrderRequest newOrderRequest) {
        UUID newOrderId = UUID.randomUUID();
        trackingService.initializeOrderTracking(newOrderId, AWAITING_PAYMENT);
        UUID paymentReference = paymentService.initiatePaymentRequest(newOrderId, newOrderRequest.amount());

        OrderDocument orderDocument = new OrderDocument(
                newOrderId,
                AWAITING_PAYMENT,
                paymentReference,
                newOrderRequest.deliveryAddress(),
                newOrderRequest.products(),
                newOrderRequest.amount());

        return orderRepository.insert(orderDocument);
    }

    public OrderStatus updateOrder(OrderStatusUpdate orderStatusUpdate) {
        log.info("Updating order having id {} to status: {}", orderStatusUpdate.orderId(), orderStatusUpdate.orderStatus());
        OrderDocument orderDocument = orderRepository.findById(orderStatusUpdate.orderId())
                .map(currentOrder -> overrideOrderStatus.apply(currentOrder, orderStatusUpdate.orderStatus()))
                .map(orderRepository::save)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));

        return orderDocument.orderStatus();
    }

    @Override
    public OrderDetails getOrderDetails(UUID id) {
        return orderRepository.findById(id)
                .map(convertDocumentToOrderDetails)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));
    }

    private static final Function<OrderDocument, OrderDetails> convertDocumentToOrderDetails = document -> new OrderDetails(
            document.id(),
            document.orderStatus(),
            document.paymentReference(),
            document.address(),
            document.amount(),
            document.products()
    );

    private static final BiFunction<OrderDocument, OrderStatus, OrderDocument> overrideOrderStatus =
            (currentOrder, newStatus) -> new OrderDocument(
                    currentOrder.id(),
                    newStatus,
                    currentOrder.paymentReference(),
                    currentOrder.address(),
                    currentOrder.products(),
                    currentOrder.amount()
            );

}
