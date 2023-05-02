package tech.madalingiurca.paymentprocessor.model.event;

import java.io.Serializable;
import java.util.UUID;

public record PaymentApprovalEvent(UUID paymentReference) implements Serializable {
}
