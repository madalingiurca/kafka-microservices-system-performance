package tech.madalingiurca.paymentprocessor.service.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.paymentprocessor.PaymentsRepository;
import tech.madalingiurca.paymentprocessor.model.ApprovalRequest;
import tech.madalingiurca.paymentprocessor.model.PaymentDetails;
import tech.madalingiurca.paymentprocessor.model.PaymentDocument;
import tech.madalingiurca.paymentprocessor.model.http.PaymentRequest;
import tech.madalingiurca.paymentprocessor.service.PaymentService;

import java.util.UUID;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static tech.madalingiurca.paymentprocessor.model.PaymentStatus.APPROVED;
import static tech.madalingiurca.paymentprocessor.model.PaymentStatus.WAITING;

@Service
@RequiredArgsConstructor
@Profile("!kafka")
public class RestPaymentService implements PaymentService {

    private final PaymentsRepository paymentsRepository;
    private final TrackingService trackingService;

    @Override
    public PaymentDocument initiate(PaymentRequest paymentRequest) {
        var newPayment = new PaymentDocument(UUID.randomUUID(), paymentRequest.amount(), WAITING, paymentRequest.orderId());
        return paymentsRepository.save(newPayment);
    }

    @Override
    public PaymentDetails getPayment(UUID id) {
        return paymentsRepository.findById(id)
                .map(convertDocumentToPaymentDetails)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment document not found"));
    }

    @Override
    public PaymentDocument approve(ApprovalRequest approvalRequest) {
        PaymentDocument updatedPaymentDocument = paymentsRepository.findById(approvalRequest.paymentId())
                .map(paymentDocument -> new PaymentDocument(paymentDocument.id(), paymentDocument.amount(), APPROVED, paymentDocument.orderId()))
                .map(paymentsRepository::save)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        trackingService.updateTracking(updatedPaymentDocument.orderId(), updatedPaymentDocument.paymentStatus());
        return paymentsRepository.save(updatedPaymentDocument);
    }

    private final static Function<PaymentDocument, PaymentDetails> convertDocumentToPaymentDetails = document
            -> new PaymentDetails(
            document.amount(),
            document.paymentStatus()
    );
}
