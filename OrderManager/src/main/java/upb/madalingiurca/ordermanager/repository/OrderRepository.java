package upb.madalingiurca.ordermanager.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import upb.madalingiurca.ordermanager.models.document.OrderDocument;

import java.util.UUID;

public interface OrderRepository extends MongoRepository<OrderDocument, UUID> {
}
