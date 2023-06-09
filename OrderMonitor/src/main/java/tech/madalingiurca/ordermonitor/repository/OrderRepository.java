package tech.madalingiurca.ordermonitor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.madalingiurca.ordermonitor.model.document.OrderDocument;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends MongoRepository<OrderDocument, UUID> {
    Optional<OrderDocument> findOrderDocumentByPaymentReference(UUID paymentReference);
}
