package tech.madalingiurca.paymentprocessor.model.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record PaymentRequest(UUID orderId, Integer amount) {

    @JsonCreator
    public PaymentRequest(
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("amount") Integer amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
}
