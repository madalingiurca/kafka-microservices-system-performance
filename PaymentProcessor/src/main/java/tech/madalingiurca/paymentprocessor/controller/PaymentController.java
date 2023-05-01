package tech.madalingiurca.paymentprocessor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.paymentprocessor.PaymentsRepository;
import tech.madalingiurca.paymentprocessor.model.ApprovalRequest;
import tech.madalingiurca.paymentprocessor.model.PaymentDetails;
import tech.madalingiurca.paymentprocessor.model.PaymentDocument;
import tech.madalingiurca.paymentprocessor.model.http.PaymentRequest;
import tech.madalingiurca.paymentprocessor.service.TrackingService;

import java.util.UUID;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static tech.madalingiurca.paymentprocessor.model.PaymentStatus.APPROVED;
import static tech.madalingiurca.paymentprocessor.model.PaymentStatus.WAITING;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentsRepository paymentsRepository;
    private final TrackingService trackingService;

    @PostMapping("/initiate")
    public PaymentDocument initiatePayment(@RequestBody PaymentRequest paymentRequest) {
        var newPayment = new PaymentDocument(UUID.randomUUID(), paymentRequest.amount(), WAITING, paymentRequest.orderId());
        return paymentsRepository.save(newPayment);
    }

    @GetMapping("/{id}")
    public PaymentDetails PaymentDetails(@PathVariable UUID id) {
        return paymentsRepository.findById(id)
                .map(convertDocumentToPaymentDetails)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment document not found"));
    }

    @PostMapping("/approve")
    public PaymentDocument approvePayment(@RequestBody ApprovalRequest approvalRequest) {
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

    @ExceptionHandler(Exception.class)
    public void handleException(Exception exception) throws ResponseStatusException {
        log.error(exception.getMessage(), exception);
        throw new ResponseStatusException(INTERNAL_SERVER_ERROR, exception.getMessage(), exception);
    }
}
