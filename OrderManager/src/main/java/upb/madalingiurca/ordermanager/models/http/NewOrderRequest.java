package upb.madalingiurca.ordermanager.models.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NewOrderRequest(Integer amount, List<String> products, String deliveryAddress) {
    @JsonCreator
    public NewOrderRequest(
            @JsonProperty("amount") Integer amount,
            @JsonProperty("products") List<String> products,
            @JsonProperty("deliveryAddress") String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        this.products = products;
        this.amount = amount;
    }
}
