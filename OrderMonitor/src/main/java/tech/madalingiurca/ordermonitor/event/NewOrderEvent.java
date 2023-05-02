package tech.madalingiurca.ordermonitor.event;


import tech.madalingiurca.ordermonitor.model.OrderStatus;

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
