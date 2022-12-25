package clock;

import java.time.Instant;

public class ChangeableClock implements Clock {
    private Instant instant;

    public ChangeableClock(Instant instant) {
        this.instant = instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public Instant now() {
        return instant;
    }
}
