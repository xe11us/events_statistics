package clock;

import java.time.Instant;

public class SimpleClock implements Clock {
    private final Instant instant;

    public SimpleClock(Instant instant) {
        this.instant = instant;
    }

    @Override
    public Instant now() {
        return instant;
    }
}
