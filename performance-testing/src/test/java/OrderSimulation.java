import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.time.Duration.ofSeconds;

public class OrderSimulation extends Simulation {
    final TestParameters testParameters = new TestParameters();
    final HttpProtocolBuilder httpProtocol = buildHttpProtocol(testParameters.env());

    ScenarioBuilder scn = scenario("Place order and checks until delivery ends")
            .pause(1, 5)
            .exec(UserInteractions.createOrderRequest)
            .exitHereIfFailed()
            .pause(1)
            .exec(UserInteractions.monitorOrderRequest)
            .exitHereIfFailed()
            .pause(5, 10)
            .exec(UserInteractions.approvePaymentRequest)
            .exitHereIfFailed()
            .pause(2)
            .asLongAsDuring(UserInteractions.orderNotFinalized, ofSeconds(80)).on(
                    exec(UserInteractions.monitorOrderRequest
                            .check(jsonPath("$.orderDetails.orderStatus").saveAs("orderStatus"))
                    ).pause(2, 15)
            );

    {
        setUp(scn.injectOpen(
                        rampUsersPerSec(1).to(testParameters.rampUsers()).during(testParameters.rampDurationSeconds()),
                        constantUsersPerSec(testParameters.constantUsers()).during(testParameters.constantDurationSeconds())
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