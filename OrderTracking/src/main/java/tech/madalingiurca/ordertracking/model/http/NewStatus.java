package tech.madalingiurca.ordertracking.model.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tech.madalingiurca.ordertracking.model.OrderStatus;

public record NewStatus(OrderStatus orderStatus) {
    @JsonCreator
    public NewStatus(@JsonProperty("orderStatus") OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
