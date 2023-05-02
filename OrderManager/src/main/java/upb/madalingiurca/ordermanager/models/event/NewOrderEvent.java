package upb.madalingiurca.ordermanager.models.event;

import upb.madalingiurca.ordermanager.models.OrderStatus;
import upb.madalingiurca.ordermanager.models.document.OrderDocument;

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

    public NewOrderEvent(OrderDocument order) {
        this(
                order.id(),
                order.orderStatus(),
                order.paymentReference(),
                order.address(),
                order.products(),
                order.amount()
        );
    }
}
