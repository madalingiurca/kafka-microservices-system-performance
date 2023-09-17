import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class OrderSimulation extends Simulation {
    final TestProperties testProperties = new TestProperties();
    final HttpProtocolBuilder httpProtocol = buildHttpProtocol(testProperties.env());

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
                        rampUsersPerSec(1).to(testProperties.rampUsers()).during(testProperties.rampDurationSeconds()),
                        constantUsersPerSec(testProperties.constantUsers()).during(testProperties.constantDurationSeconds())
                )
        ).protocols(httpProtocol);
    }

    private static HttpProtocolBuilder buildHttpProtocol(String env) {
        var host = env.equals("azure") ? "104.40.149.227" : "localhost:8000";

        //noinspection HttpUrlsUsage
        return http
                .baseUrl("http://" + host)
                .header("content-type", "application/json")
                .acceptHeader("application/json")
                .shareConnections();
    }


}