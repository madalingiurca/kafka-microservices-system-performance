package upb.madalingiurca.ordermanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import upb.madalingiurca.ordermanager.models.OrderStatus;
import upb.madalingiurca.ordermanager.models.document.OrderDocument;
import upb.madalingiurca.ordermanager.models.http.NewOrderRequest;
import upb.madalingiurca.ordermanager.models.http.OrderDetails;
import upb.madalingiurca.ordermanager.models.http.OrderStatusUpdate;
import upb.madalingiurca.ordermanager.service.OrderService;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public OrderDetails getOrderDetails(@PathVariable UUID id) {
        return orderService.getOrderDetails(id);
    }

    @PostMapping("/new")
    public OrderDocument createNewOrder(@RequestBody NewOrderRequest newOrderRequest) {
        return orderService.newOrder(newOrderRequest);
    }

    @PostMapping("/update")
    public OrderStatus updateOrder(@RequestBody OrderStatusUpdate orderStatusUpdate) {
        return orderService.updateOrder(orderStatusUpdate);
    }


    @ExceptionHandler(Exception.class)
    public void handleException(Exception exception) throws ResponseStatusException {
        log.error(exception.getMessage(), exception);
        throw new ResponseStatusException(INTERNAL_SERVER_ERROR, exception.getMessage(), exception);
    }
}
