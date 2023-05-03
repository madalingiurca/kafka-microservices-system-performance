import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class OrderSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8000")
            .header("content-type", "application/json")
            .acceptHeader("application/json");

    ScenarioBuilder scn = scenario("Place order and checks until delivery ends")
            .pause(1, 5)
            .exec(Config.createOrderRequest)
            .pause(1)
            .exec(Config.monitorOrderRequest)
            .pause(5, 10)
            .exec(Config.approvePaymentRequest)
            .pause(2)
            .asLongAs(Config.orderNotFinalized).on(
                    exec(Config.monitorOrderRequest
                            .check(jsonPath("$.orderDetails.orderStatus").saveAs("orderStatus"))
                    ).pause(2, 15)
            );

    {
        setUp(scn.injectOpen(
                        rampUsersPerSec(1).to(50).during(30),
                        constantUsersPerSec(50).during(20)
                )
        ).protocols(httpProtocol);
    }
}