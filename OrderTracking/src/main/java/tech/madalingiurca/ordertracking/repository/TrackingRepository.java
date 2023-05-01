package tech.madalingiurca.ordertracking.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import tech.madalingiurca.ordertracking.model.OrderStatus;
import tech.madalingiurca.ordertracking.model.document.TrackedOrderDocument;

import java.util.List;
import java.util.UUID;

public interface TrackingRepository extends MongoRepository<TrackedOrderDocument, UUID> {
    List<TrackedOrderDocument> findAllByOrderStatus(OrderStatus orderStatus);
}
