package tech.madalingiurca.ordermonitor.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tech.madalingiurca.ordermonitor.event.NewOrderEvent;
import tech.madalingiurca.ordermonitor.model.document.OrderDocument;
import tech.madalingiurca.ordermonitor.repository.OrderRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaListeners {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "orders", groupId = "new-orders-consumer")
    public void listenGroupFoo(String newOrder) throws JsonProcessingException {
        System.out.println("Received newOrder: " + newOrder);
        var newOrderEvent = objectMapper.readValue(newOrder, NewOrderEvent.class);
        var orderDocument = new OrderDocument(
                newOrderEvent.id(),
                newOrderEvent.orderStatus(),
                newOrderEvent.paymentReference(),
                newOrderEvent.address(),
                newOrderEvent.products(),
                newOrderEvent.amount());

        orderRepository.save(orderDocument);
        log.info("Order with {} saved in the database", orderDocument.id());
    }
}
