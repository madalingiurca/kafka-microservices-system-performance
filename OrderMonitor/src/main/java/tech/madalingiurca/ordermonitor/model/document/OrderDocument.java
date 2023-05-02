package tech.madalingiurca.ordermonitor.model.document;


import org.springframework.data.annotation.Id;
import tech.madalingiurca.ordermonitor.model.OrderStatus;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;


public record OrderDocument(
        @Id UUID id,
        OrderStatus orderStatus,
        UUID paymentReference,
        String address,
        List<String> products,
        int amount) implements Serializable {
}
