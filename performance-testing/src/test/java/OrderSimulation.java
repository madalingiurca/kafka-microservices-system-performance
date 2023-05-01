import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class OrderSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8000")
            .header("content-type", "application/json")
            .acceptHeader("application/json");

    ScenarioBuilder scn = scenario("Place order and checks until delivery ends")
            .pause(10)
            .exec(Config.createOrderRequest)
            .exec(Config.monitorOrderRequest)
            .pause(ofSeconds(10))
            .exec(Config.approvePaymentRequest)
            .pause(30)
            .asLongAs(Config.orderNotFinalized).on(
                    exec(Config.monitorOrderRequest
                            .check(jsonPath("$.orderDetails.orderStatus").saveAs("orderStatus"))
                    ).pause(10)
            );

    {
        setUp(scn.injectOpen(
                        rampUsersPerSec(1).to(30).during(30),
                        constantUsersPerSec(30).during(4)
                )
        ).protocols(httpProtocol);
    }
}