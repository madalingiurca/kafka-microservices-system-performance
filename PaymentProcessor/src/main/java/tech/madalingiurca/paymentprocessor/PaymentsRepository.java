package tech.madalingiurca.paymentprocessor;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.madalingiurca.paymentprocessor.model.PaymentDocument;

import java.util.UUID;

public interface PaymentsRepository extends MongoRepository<PaymentDocument, UUID> {
}
