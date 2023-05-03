package tech.madalingiurca.ordertracking.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.madalingiurca.ordertracking.model.document.TrackedOrderDocument;
import tech.madalingiurca.ordertracking.model.event.UpdateOrderEvent;
import tech.madalingiurca.ordertracking.repository.TrackingRepository;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import static java.util.concurrent.TimeUnit.SECONDS;
import static tech.madalingiurca.ordertracking.model.OrderStatus.*;

@Component
@Profile("kafka")
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderScheduler {

    private final KafkaTemplate<String, Serializable> kafkaTemplate;
    private final TrackingRepository repository;

    @Scheduled(initialDelay = 5, fixedRate = 5, timeUnit = SECONDS)
    public void startOrderShipment() {
        log.info("Starting process of order shipment using kafka");

        repository.findAllByOrderStatus(PAYMENT_CONFIRMED).stream()
                .peek(order -> log.info("Starting shipment for order {}", order.orderId()))
                .map(changeStatusToShipped)
                .forEach(trackedOrderDocument -> {
                    kafkaTemplate.send("updates", new UpdateOrderEvent(trackedOrderDocument.orderId(), trackedOrderDocument.orderStatus()))
                            .whenComplete(((res, throwable) -> {
                                if (res != null) {
                                    log.debug("New order update event successfully posted {}", res.getProducerRecord().value());
                                } else
                                    log.error("Error while posting on kafka bus:", throwable);
                            }));
                    repository.save(trackedOrderDocument);
                });
    }

    @Scheduled(initialDelay = 10, fixedRate = 5, timeUnit = SECONDS)
    public void markOrderAsDelivered() {
        log.info("Starting process of order delivery using kafka");
        repository.findAllByOrderStatus(SHIPPED).stream()
                .peek(order -> log.info("Delivery completed for order {}", order.orderId()))
                .map(changeStatusToDelivered)
                .forEach(trackedOrderDocument -> {
                    kafkaTemplate.send("updates", new UpdateOrderEvent(trackedOrderDocument.orderId(), trackedOrderDocument.orderStatus()))
                            .whenComplete(((res, throwable) -> {
                                if (res != null) {
                                    log.debug("New order update event successfully posted {}", res.getProducerRecord().value());
                                } else
                                    log.error("Error while posting on kafka bus:", throwable);
                            }));
                    repository.delete(trackedOrderDocument);
                });
    }

    private final UnaryOperator<TrackedOrderDocument> changeStatusToShipped =
            (order) -> new TrackedOrderDocument(order.orderId(), SHIPPED, order.paymentReference());

    private final UnaryOperator<TrackedOrderDocument> changeStatusToDelivered =
            (order) -> new TrackedOrderDocument(order.orderId(), DELIVERED, order.paymentReference());

}
