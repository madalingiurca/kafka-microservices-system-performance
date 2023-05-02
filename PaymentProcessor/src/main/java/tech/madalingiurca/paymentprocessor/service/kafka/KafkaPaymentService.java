package tech.madalingiurca.paymentprocessor.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.paymentprocessor.model.ApprovalRequest;
import tech.madalingiurca.paymentprocessor.model.PaymentDetails;
import tech.madalingiurca.paymentprocessor.model.PaymentDocument;
import tech.madalingiurca.paymentprocessor.model.PaymentStatus;
import tech.madalingiurca.paymentprocessor.model.event.PaymentApprovalEvent;
import tech.madalingiurca.paymentprocessor.model.http.PaymentRequest;
import tech.madalingiurca.paymentprocessor.service.PaymentService;

import java.io.Serializable;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("kafka")
public class KafkaPaymentService implements PaymentService {

    private final KafkaTemplate<String, Serializable> kafkaTemplate;

    @Override
    public PaymentDocument initiate(PaymentRequest paymentRequest) {
        throw new ResponseStatusException(NOT_IMPLEMENTED, "Shouldn't be called in Kafka implementation");
    }

    @Override
    public PaymentDetails getPayment(UUID id) {
        throw new ResponseStatusException(NOT_IMPLEMENTED, "Shouldn't be called in Kafka implementation");
    }

    @Override
    public PaymentDocument approve(ApprovalRequest approvalRequest) {
        kafkaTemplate.send("payments", new PaymentApprovalEvent(approvalRequest.paymentId()))
                .whenComplete(((res, throwable) -> {
                    if (res != null)
                        log.info("New payment approval event successfully posted {}", res.getProducerRecord().value());
                    else
                        log.error("Error while posting on kafka bus:", throwable);
                }));

        return new PaymentDocument(approvalRequest.paymentId(), 0, PaymentStatus.APPROVED, null);
    }
}
