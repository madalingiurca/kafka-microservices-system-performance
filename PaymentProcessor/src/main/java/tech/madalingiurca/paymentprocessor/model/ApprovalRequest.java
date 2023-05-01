package tech.madalingiurca.paymentprocessor.model;

import java.util.UUID;

public record ApprovalRequest(UUID paymentId) {
}
