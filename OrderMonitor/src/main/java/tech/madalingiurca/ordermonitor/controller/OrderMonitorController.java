package tech.madalingiurca.ordermonitor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordermonitor.model.http.OrderInfoResponse;
import tech.madalingiurca.ordermonitor.service.OrderRetrieveService;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderMonitorController {

    private final OrderRetrieveService orderRetrieveService;

    @GetMapping("/{id}")
    public OrderInfoResponse getOrderInformation(@PathVariable UUID id) {
        return orderRetrieveService.getOrderDetails(id);
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
