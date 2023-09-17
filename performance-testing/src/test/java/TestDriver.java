import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;

public class TestDriver {

    public static void main(String[] args) {

        TestParameters testParameters = new TestParameters();

        var props = new GatlingPropertiesBuilder()
                .simulationClass(OrderSimulation.class.getName())
                .resourcesDirectory("performance-testing/src/test/resources")
                .resultsDirectory("performance-testing/results/" + testParameters);

        Gatling.fromMap(props.build());
    }
}
