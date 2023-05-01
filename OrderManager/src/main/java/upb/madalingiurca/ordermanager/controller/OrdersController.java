package upb.madalingiurca.ordermanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import upb.madalingiurca.ordermanager.models.OrderStatus;
import upb.madalingiurca.ordermanager.models.document.OrderDocument;
import upb.madalingiurca.ordermanager.models.http.NewOrderRequest;
import upb.madalingiurca.ordermanager.models.http.OrderStatusUpdate;
import upb.madalingiurca.ordermanager.repository.OrderRepository;
import upb.madalingiurca.ordermanager.service.PaymentService;
import upb.madalingiurca.ordermanager.service.TrackingService;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static upb.madalingiurca.ordermanager.models.OrderStatus.AWAITING_PAYMENT;

@RestController("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final TrackingService trackingService;

    @GetMapping("/{id}")
    public OrderDetails getOrderDetails(@PathVariable UUID id) {
        return orderRepository.findById(id)
                .map(convertDocumentToOrderDetails)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));
    }

    @PostMapping("/new")
    public OrderDocument createNewOrder(@RequestBody NewOrderRequest newOrderRequest) {
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

    @PostMapping("/update")
    public OrderStatus updateOrder(@RequestBody OrderStatusUpdate orderStatusUpdate) {
        log.info("Updating order having id {} to status: {}", orderStatusUpdate.orderId(), orderStatusUpdate.orderStatus());
        OrderDocument orderDocument = orderRepository.findById(orderStatusUpdate.orderId())
                .map(currentOrder -> overrideOrderStatus.apply(currentOrder, orderStatusUpdate.orderStatus()))
                .map(orderRepository::save)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));

        return orderDocument.orderStatus();
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

    @ExceptionHandler(Exception.class)
    public void handleException(Exception exception) throws ResponseStatusException {
        log.error(exception.getMessage(), exception);
        throw new ResponseStatusException(INTERNAL_SERVER_ERROR, exception.getMessage(), exception);
    }
}
