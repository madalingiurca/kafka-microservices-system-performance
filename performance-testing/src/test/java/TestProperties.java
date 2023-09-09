import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;

@Getter
@Accessors(fluent = true)
final class TestProperties {
    private final String env;
    private final Integer rampUsers;
    private final Integer constantUsers;
    private final Integer rampDurationSeconds;
    private final Integer constantDurationSeconds;

    TestProperties() {
        this.env = System.getProperty("environment", "local");
        this.rampUsers = Integer.getInteger("rampUsers", 1);
        this.constantUsers = Integer.getInteger("constantUsers", 1);
        this.rampDurationSeconds = Integer.getInteger("rampDuration", 1);
        this.constantDurationSeconds = Integer.getInteger("constantDuration", 1);
    }

    @Override
    public String toString() {
        return "%s/ru%d/cu%d/rd%d/cd%d".formatted(
                env,
                rampUsers,
                constantUsers,
                rampDurationSeconds,
                constantDurationSeconds
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TestProperties) obj;
        return Objects.equals(this.env, that.env) &&
                Objects.equals(this.rampUsers, that.rampUsers) &&
                Objects.equals(this.constantUsers, that.constantUsers) &&
                Objects.equals(this.rampDurationSeconds, that.rampDurationSeconds) &&
                Objects.equals(this.constantDurationSeconds, that.constantDurationSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(env, rampUsers, constantUsers, rampDurationSeconds, constantDurationSeconds);
    }

}
