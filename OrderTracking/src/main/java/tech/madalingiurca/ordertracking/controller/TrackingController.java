package tech.madalingiurca.ordertracking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.ordertracking.model.OrderStatus;
import tech.madalingiurca.ordertracking.model.document.TrackedOrderDocument;
import tech.madalingiurca.ordertracking.model.http.NewStatus;
import tech.madalingiurca.ordertracking.model.http.OrderTrackingRequest;
import tech.madalingiurca.ordertracking.repository.TrackingRepository;
import tech.madalingiurca.ordertracking.service.http.OrderManagerService;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController("tracking")
@RequiredArgsConstructor
@ConditionalOnBean(OrderManagerService.class)
public class TrackingController {

    private final TrackingRepository repository;
    private final OrderManagerService orderManagerService;

    @PostMapping("/initialize")
    public void initializeOrder(@RequestBody OrderTrackingRequest orderTrackingRequest) {
        repository.save(new TrackedOrderDocument(orderTrackingRequest.orderId(), orderTrackingRequest.orderStatus(), orderTrackingRequest.paymentReference()));
    }

    @PostMapping("/update/{id}")
    public OrderStatus updateOrder(@PathVariable UUID id, @RequestBody NewStatus newStatus) {
        var updatedTrackedOrder = repository.findById(id)
                .map(current -> new TrackedOrderDocument(current.orderId(), newStatus.orderStatus(), current.paymentReference()))
                .map(repository::save)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tracking update failed"));

        orderManagerService.updateOrderDetails(updatedTrackedOrder.orderId(), updatedTrackedOrder.orderStatus());

        return updatedTrackedOrder.orderStatus();
    }
}
