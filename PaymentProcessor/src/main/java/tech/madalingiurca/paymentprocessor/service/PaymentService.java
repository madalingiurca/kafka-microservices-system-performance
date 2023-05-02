package tech.madalingiurca.paymentprocessor.service;

import tech.madalingiurca.paymentprocessor.model.ApprovalRequest;
import tech.madalingiurca.paymentprocessor.model.PaymentDetails;
import tech.madalingiurca.paymentprocessor.model.PaymentDocument;
import tech.madalingiurca.paymentprocessor.model.http.PaymentRequest;

import java.util.UUID;

public interface PaymentService {
    PaymentDocument initiate(PaymentRequest paymentRequest);

    PaymentDetails getPayment(UUID id);

    PaymentDocument approve(ApprovalRequest approvalRequest);
}
