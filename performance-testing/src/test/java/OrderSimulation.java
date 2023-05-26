import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class OrderSimulation extends Simulation {

    final int NUMBER_OF_USERS = 200;

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8000")
            .header("content-type", "application/json")
            .acceptHeader("application/json");

    ScenarioBuilder scn = scenario("Place order and checks until delivery ends")
            .pause(1, 5)
            .exec(Config.createOrderRequest)
            .exitHereIfFailed()
            .pause(1)
            .exec(Config.monitorOrderRequest)
            .exitHereIfFailed()
            .pause(5, 10)
            .exec(Config.approvePaymentRequest)
            .exitHereIfFailed()
            .pause(2)
            .asLongAsDuring(Config.orderNotFinalized, ofSeconds(80)).on(
                    exec(Config.monitorOrderRequest
                            .check(jsonPath("$.orderDetails.orderStatus").saveAs("orderStatus"))
                    ).pause(2, 15)
            );

    {
        setUp(scn.injectOpen(
                        rampUsersPerSec(1).to(NUMBER_OF_USERS).during(30),
                        constantUsersPerSec(NUMBER_OF_USERS).during(20)
                )
        ).protocols(httpProtocol);
    }
}