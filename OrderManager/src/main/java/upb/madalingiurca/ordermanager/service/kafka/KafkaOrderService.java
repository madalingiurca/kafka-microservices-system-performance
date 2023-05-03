package upb.madalingiurca.ordermanager.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import upb.madalingiurca.ordermanager.models.OrderStatus;
import upb.madalingiurca.ordermanager.models.document.OrderDocument;
import upb.madalingiurca.ordermanager.models.event.NewOrderEvent;
import upb.madalingiurca.ordermanager.models.http.NewOrderRequest;
import upb.madalingiurca.ordermanager.models.http.OrderDetails;
import upb.madalingiurca.ordermanager.models.http.OrderStatusUpdate;
import upb.madalingiurca.ordermanager.service.OrderService;

import java.io.Serializable;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static upb.madalingiurca.ordermanager.models.OrderStatus.AWAITING_PAYMENT;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("kafka")
public class KafkaOrderService implements OrderService {

    private final KafkaTemplate<String, Serializable> kafkaTemplate;

    @Override
    public OrderDocument newOrder(NewOrderRequest newOrderRequest) {
        var orderDocument = new OrderDocument(
                UUID.randomUUID(),
                AWAITING_PAYMENT,
                UUID.randomUUID(),
                newOrderRequest.deliveryAddress(),
                newOrderRequest.products(),
                newOrderRequest.amount());

        kafkaTemplate.send("orders", new NewOrderEvent(orderDocument))
                .whenComplete(((res, throwable) -> {
                    if (res != null)
                        log.debug("New order event successfully posted {}", res.getProducerRecord().value());
                    else
                        log.error("Error while posting on kafka bus:", throwable);
                }));
        return orderDocument;
    }

    @Override
    public OrderStatus updateOrder(OrderStatusUpdate orderStatusUpdate) {
        throw new ResponseStatusException(NOT_IMPLEMENTED, "Shouldn't be called in Kafka implementation");
    }

    @Override
    public OrderDetails getOrderDetails(UUID id) {
        throw new ResponseStatusException(NOT_IMPLEMENTED, "Shouldn't be called in Kafka implementation");
    }
}
