package tech.madalingiurca.ordermonitor.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordermonitor.event.NewOrderEvent;
import tech.madalingiurca.ordermonitor.event.PaymentApprovalEvent;
import tech.madalingiurca.ordermonitor.event.UpdateOrderEvent;
import tech.madalingiurca.ordermonitor.model.document.OrderDocument;
import tech.madalingiurca.ordermonitor.repository.OrderRepository;

import java.util.function.UnaryOperator;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static tech.madalingiurca.ordermonitor.model.OrderStatus.PAYMENT_CONFIRMED;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("kafka")
public class KafkaListeners {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "orders", groupId = "new-orders-consumer")
    public void listenNewOrderEvent(String newOrder) throws JsonProcessingException {
        var newOrderEvent = objectMapper.readValue(newOrder, NewOrderEvent.class);
        var orderDocument = new OrderDocument(
                newOrderEvent.id(),
                newOrderEvent.orderStatus(),
                newOrderEvent.paymentReference(),
                newOrderEvent.address(),
                newOrderEvent.products(),
                newOrderEvent.amount());

        orderRepository.save(orderDocument);
        log.debug("Order with {} saved in the database", orderDocument.id());
    }

    @KafkaListener(topics = "payments", groupId = "payments-approval-consumer")
    public void listenPaymentApproval(String paymentApprovalEvent) throws JsonProcessingException {
        var paymentApproval = objectMapper.readValue(paymentApprovalEvent, PaymentApprovalEvent.class);

        var orderDocument = orderRepository.findOrderDocumentByPaymentReference(paymentApproval.paymentReference())
                .map(markOrderWithApprovedPayment)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found for payment reference"));

        orderRepository.save(orderDocument);

        log.debug("Payment approved for order {}", orderDocument.id());
    }

    @KafkaListener(topics = "updates", groupId = "order-updates-consumer")
    public void listenOrderUpdates(String updateOrderEvent) throws JsonProcessingException {
        var updateOrder = objectMapper.readValue(updateOrderEvent, UpdateOrderEvent.class);

        var orderDocument = orderRepository.findById(updateOrder.orderId())
                .map(order -> new OrderDocument(
                        order.id(),
                        updateOrder.newStatus(),
                        order.paymentReference(),
                        order.address(),
                        order.products(),
                        order.amount()
                ))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found for payment reference"));

        orderRepository.save(orderDocument);

        log.debug("Order updated {}", orderDocument.id());
    }

    private final UnaryOperator<OrderDocument> markOrderWithApprovedPayment = order -> new OrderDocument(
            order.id(),
            PAYMENT_CONFIRMED,
            order.paymentReference(),
            order.address(),
            order.products(),
            order.amount()
    );
}
