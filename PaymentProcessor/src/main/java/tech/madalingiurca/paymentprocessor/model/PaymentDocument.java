package tech.madalingiurca.paymentprocessor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record PaymentDocument(UUID id, int amount, PaymentStatus paymentStatus, UUID orderId) {

    @JsonCreator
    public PaymentDocument(
            @JsonProperty("id") UUID id,
            @JsonProperty("amount") int amount,
            @JsonProperty("paymentStatus") PaymentStatus paymentStatus,
            @JsonProperty("orderId") UUID orderId) {
        this.id = id;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.orderId = orderId;
    }
}
