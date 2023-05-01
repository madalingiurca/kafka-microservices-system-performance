package tech.madalingiurca.ordermonitor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordermonitor.model.http.OrderInfoResponse;
import tech.madalingiurca.ordermonitor.service.OrderManagerService;
import tech.madalingiurca.ordermonitor.service.PaymentsQueryService;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderMonitorController {

    private final OrderManagerService orderManagerService;
    private final PaymentsQueryService paymentsQueryService;

    @GetMapping("/{id}")
    public OrderInfoResponse getOrderInformation(@PathVariable UUID id) {
        var orderDetails = orderManagerService.getOrderDetails(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order details not found"));
        var paymentDetails = paymentsQueryService.getPaymentDetails(orderDetails.paymentReference())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment details not found"));

        return new OrderInfoResponse(paymentDetails, orderDetails);
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception exception) throws ResponseStatusException {
        if (exception instanceof ResponseStatusException responseStatusException) {
            log.warn("Business logic got tangled, inform Mada about: {}", responseStatusException.getMessage());
            throw responseStatusException;
        }

        log.error(exception.getMessage(), exception);
        throw new ResponseStatusException(INTERNAL_SERVER_ERROR, exception.getMessage(), exception);
    }
}
