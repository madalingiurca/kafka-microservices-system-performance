package tech.madalingiurca.ordermonitor.service.http;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordermonitor.model.http.OrderInfoResponse;
import tech.madalingiurca.ordermonitor.service.OrderRetrieveService;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Profile("!kafka")
@RequiredArgsConstructor
public class RestOrderRetrieveService implements OrderRetrieveService {

    private final OrderManagerService orderManagerService;
    private final PaymentsQueryService paymentsQueryService;

    @Override
    public OrderInfoResponse getOrderDetails(UUID id) {
        var orderDetails = orderManagerService.getOrderDetails(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order details not found"));
        var paymentDetails = paymentsQueryService.getPaymentDetails(orderDetails.paymentReference())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment details not found"));

        return new OrderInfoResponse(paymentDetails, orderDetails);
    }
}
