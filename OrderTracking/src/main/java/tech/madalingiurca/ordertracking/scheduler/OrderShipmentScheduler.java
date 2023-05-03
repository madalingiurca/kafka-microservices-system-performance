package tech.madalingiurca.ordertracking.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.madalingiurca.ordertracking.model.document.TrackedOrderDocument;
import tech.madalingiurca.ordertracking.repository.TrackingRepository;
import tech.madalingiurca.ordertracking.service.http.OrderManagerService;

import java.util.function.UnaryOperator;

import static java.util.concurrent.TimeUnit.SECONDS;
import static tech.madalingiurca.ordertracking.model.OrderStatus.*;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(OrderManagerService.class)
public class OrderShipmentScheduler {

    private final TrackingRepository repository;
    private final OrderManagerService orderManagerService;

    @Scheduled(initialDelay = 30, fixedRate = 30, timeUnit = SECONDS)
    public void startOrderShipment() {
        final UnaryOperator<TrackedOrderDocument> updateOrderManager = (order) -> {
            orderManagerService.updateOrderDetails(order.orderId(), order.orderStatus());
            return order;
        };

        log.info("Starting process of order shipment");
        repository.findAllByOrderStatus(PAYMENT_CONFIRMED).stream()
                .peek(order -> log.info("Starting shipment for order {}", order.orderId()))
                .map(changeStatusToShipped.andThen(updateOrderManager))
                .forEach(repository::save);
    }

    @Scheduled(initialDelay = 60, fixedRate = 30, timeUnit = SECONDS)
    public void markOrderAsDelivered() {
        final UnaryOperator<TrackedOrderDocument> updateOrderManager = (order) -> {
            orderManagerService.updateOrderDetails(order.orderId(), order.orderStatus());
            return order;
        };
        log.info("Starting process of order delivery");
        repository.findAllByOrderStatus(SHIPPED).stream()
                .peek(order -> log.info("Delivery completed for order {}", order.orderId()))
                .map(changeStatusToDelivered.andThen(updateOrderManager))
                .forEach(repository::delete);
    }

    private final UnaryOperator<TrackedOrderDocument> changeStatusToShipped =
            (order) -> new TrackedOrderDocument(order.orderId(), SHIPPED, order.paymentReference());

    private final UnaryOperator<TrackedOrderDocument> changeStatusToDelivered =
            (order) -> new TrackedOrderDocument(order.orderId(), DELIVERED, order.paymentReference());


}
