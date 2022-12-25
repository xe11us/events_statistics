import clock.ChangeableClock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import statistics.EventsStatistics;
import statistics.IntervalEventStatistics;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.IntStream;

public class IntervalEventStatisticsTest {
    private final double EPS = 1e-7;
    private final ChangeableClock clock = new ChangeableClock(Instant.now());
    private final EventsStatistics<Double> statistics = new IntervalEventStatistics(clock, 60);

    @Test
    public void nonExistingEvent() {
        Assertions.assertEquals(Math.abs(getStat("event")), 0., EPS);
    }

    @Test
    public void outdatedEvent() {
        statistics.incEvent("event");
        clock.setInstant(clock.now().plus(Duration.ofSeconds(3601)));
        Assertions.assertEquals(Math.abs(getStat("event")), 0., EPS);
    }

    @Test
    public void calculateRpm() {
        IntStream.range(0, 30).forEach(i -> statistics.incEvent("event"));
        Assertions.assertEquals(Math.abs(getStat("event")), 0.5, EPS);
    }

    @Test
    public void calculateOnlyActualEventsRpm() {
        clock.setInstant(clock.now().plus(Duration.ofHours(2)));
        IntStream.range(0, 20).forEach(i -> statistics.incEvent("event 1"));

        clock.setInstant(clock.now().plus(Duration.ofMinutes(15)));
        IntStream.range(0, 35).forEach(i -> statistics.incEvent("event 2"));
        IntStream.range(0, 100).forEach(i -> statistics.incEvent("event 3"));

        clock.setInstant(clock.now().plus(Duration.ofMinutes(45)));
        IntStream.range(0, 33).forEach(i -> statistics.incEvent("event 3"));

        Map<String, Double> expectedStats = Map.of(
                "event 1", 20. / 60,
                "event 2", 35. / 60,
                "event 3", 133. / 60
        );
        Map<String, Double> actualStats = statistics.getAllEventsStatistics();

        actualStats.forEach(
                (event, rpm) -> {
                    Assertions.assertTrue(expectedStats.containsKey(event), "Unexpected event");
                    Assertions.assertEquals(expectedStats.get(event), rpm, EPS);
                }
        );

        Assertions.assertEquals(expectedStats.size(), actualStats.size(), "Unexpected events amount");
    }

    private double getStat(String name) {
        return statistics.getEventStatisticsByName(name);
    }
}
