import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import java.util.Objects;
import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Config {
    public static final HttpRequestActionBuilder createOrderRequest = http("create_order")
            .post("/orders/new")
            .body(RawFileBody("new-order-request.json"))
            .check(status().is(200), jsonPath("$.id").saveAs("orderId"));

    public static final HttpRequestActionBuilder monitorOrderRequest = http("monitor_order")
            .get("/monitor/#{orderId}")
            .check(status().is(200), jsonPath("$.orderDetails.paymentReference").saveAs("paymentReference"));

    public static final HttpRequestActionBuilder approvePaymentRequest = http("approve_payment")
            .post("/payments/approve")
            .body(StringBody("""
                    {
                        "paymentId": "#{paymentReference}"
                    }
                    """))
            .check(status().is(200));

    public static final Function<Session, Boolean> orderNotFinalized = session ->
            !session.contains("orderStatus") || !Objects.equals(session.getString("orderStatus"), "DELIVERED");
}
