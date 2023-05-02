package tech.madalingiurca.ordermonitor.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordermonitor.model.OrderDetails;
import tech.madalingiurca.ordermonitor.model.PaymentDetails;
import tech.madalingiurca.ordermonitor.model.document.OrderDocument;
import tech.madalingiurca.ordermonitor.model.http.OrderInfoResponse;
import tech.madalingiurca.ordermonitor.repository.OrderRepository;
import tech.madalingiurca.ordermonitor.service.OrderRetrieveService;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static tech.madalingiurca.ordermonitor.model.PaymentStatus.APPROVED;
import static tech.madalingiurca.ordermonitor.model.PaymentStatus.WAITING;

@Service
@Profile("kafka")
@Slf4j
@RequiredArgsConstructor
public class KafkaOrderRetrieveService implements OrderRetrieveService {

    private final OrderRepository orderRepository;

    @Override
    public OrderInfoResponse getOrderDetails(UUID id) {
        OrderDocument order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));

        log.info("Order status ordinal = {}", order.orderStatus().ordinal());
        var paymentStatus = order.orderStatus().ordinal() < 2 ? WAITING : APPROVED;
        var payment = new PaymentDetails(order.amount(), paymentStatus);
        var orderDetails = new OrderDetails(
                order.id(),
                order.orderStatus().name(),
                order.paymentReference(),
                order.address(),
                order.amount(),
                order.products()
        );

        return new OrderInfoResponse(payment, orderDetails);
    }
}
