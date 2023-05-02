package tech.madalingiurca.ordermonitor.event;

import java.io.Serializable;
import java.util.UUID;

public record PaymentApprovalEvent(UUID paymentReference) implements Serializable {
}
