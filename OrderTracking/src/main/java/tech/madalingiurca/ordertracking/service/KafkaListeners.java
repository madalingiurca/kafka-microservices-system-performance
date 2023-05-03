package tech.madalingiurca.ordertracking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordertracking.model.document.TrackedOrderDocument;
import tech.madalingiurca.ordertracking.model.event.NewOrderEvent;
import tech.madalingiurca.ordertracking.model.event.PaymentApprovalEvent;
import tech.madalingiurca.ordertracking.repository.TrackingRepository;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static tech.madalingiurca.ordertracking.model.OrderStatus.PAYMENT_CONFIRMED;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {
    private final TrackingRepository trackingRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "orders", groupId = "new-orders-tracking-consumer")
    public void listenNewOrder(String newOrderEvent) throws JsonProcessingException {
        log.debug("New order event received {}", newOrderEvent);

        var newOrder = objectMapper.readValue(newOrderEvent, NewOrderEvent.class);
        var trackedOrderDocument = trackingRepository.save(new TrackedOrderDocument(newOrder.id(), newOrder.orderStatus(), newOrder.paymentReference()));

        log.debug("New order is being tracked with id {} having status {}", trackedOrderDocument.orderId(), trackedOrderDocument.orderStatus());
    }

    @KafkaListener(topics = "payments", groupId = "payment-approval-tracking-consumer")
    public void listenPaymentApproval(String paymentApprovalEvent) throws JsonProcessingException {
        log.debug("Payment approval event received {}", paymentApprovalEvent);

        var paymentApproval = objectMapper.readValue(paymentApprovalEvent, PaymentApprovalEvent.class);
        TrackedOrderDocument updatedTrackingDocument = trackingRepository.findByPaymentReference(paymentApproval.paymentReference())
                .map(current -> new TrackedOrderDocument(current.orderId(), PAYMENT_CONFIRMED, current.paymentReference()))
                .map(trackingRepository::save)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tracking update failed"));

        log.debug("Order {} status has been updated to {}", updatedTrackingDocument.orderId(), PAYMENT_CONFIRMED);
    }
}
