package tech.madalingiurca.paymentprocessor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.madalingiurca.paymentprocessor.model.ApprovalRequest;
import tech.madalingiurca.paymentprocessor.model.PaymentDetails;
import tech.madalingiurca.paymentprocessor.model.PaymentDocument;
import tech.madalingiurca.paymentprocessor.model.http.PaymentRequest;
import tech.madalingiurca.paymentprocessor.service.PaymentService;

import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public PaymentDocument initiatePayment(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.initiate(paymentRequest);
    }

    @GetMapping("/{id}")
    public PaymentDetails PaymentDetails(@PathVariable UUID id) {
        return paymentService.getPayment(id);
    }

    @PostMapping("/approve")
    public PaymentDocument approvePayment(@RequestBody ApprovalRequest approvalRequest) {
        return paymentService.approve(approvalRequest);
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception exception) throws ResponseStatusException {
        log.error(exception.getMessage(), exception);
        throw new ResponseStatusException(INTERNAL_SERVER_ERROR, exception.getMessage(), exception);
    }
}
