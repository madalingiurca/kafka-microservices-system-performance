package tech.madalingiurca.ordermonitor.model.http;

import tech.madalingiurca.ordermonitor.model.OrderDetails;
import tech.madalingiurca.ordermonitor.model.PaymentDetails;

public record OrderInfoResponse(PaymentDetails paymentDetails, OrderDetails orderDetails) {
}
