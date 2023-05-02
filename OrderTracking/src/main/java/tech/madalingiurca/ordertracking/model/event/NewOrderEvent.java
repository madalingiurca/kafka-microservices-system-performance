package tech.madalingiurca.ordertracking.model.event;


import tech.madalingiurca.ordertracking.model.OrderStatus;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record NewOrderEvent(UUID id,
                            OrderStatus orderStatus,
                            UUID paymentReference,
                            String address,
                            List<String> products,
                            int amount)
        implements Serializable {
}
