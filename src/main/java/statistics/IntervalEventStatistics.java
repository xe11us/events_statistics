package statistics;

import clock.Clock;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class IntervalEventStatistics implements EventsStatistics<Double> { // статистика - rpm - Double
    private final Clock clock;
    private final long interval;

    private final Map<String, LinkedList<Instant>> statsMap;

    public IntervalEventStatistics(Clock clock, long intervalMinutes) {
        this.clock = clock;
        this.statsMap = new HashMap<>();
        this.interval = intervalMinutes;
    }

    @Override
    public void incEvent(String name) {
        getStat(name).addLast(clock.now());
    }

    @Override
    public Double getEventStatisticsByName(String name) {
        removeOldStatisticsForEvent(name, clock.now());
        return getStat(name).size() * 1. / interval;
    }

    @Override
    public Map<String, Double> getAllEventsStatistics() {
        return statsMap
                .keySet()
                .stream()
                .collect(Collectors.toMap(name -> name, this::getEventStatisticsByName));
    }

    @Override
    public void printStatistics() {
        getAllEventsStatistics().forEach(
                (name, rpm) -> System.out.printf("Event %s: rpm = %.3f\n", name, rpm)
        );
    }

    private LinkedList<Instant> getStat(String name) {
        return statsMap.computeIfAbsent(name, e -> new LinkedList<>());
    }

    private void removeOldStatisticsForEvent(String name, Instant now) {
        LinkedList<Instant> stat = getStat(name);
        while (!stat.isEmpty() && stat.peekFirst().isBefore(now.minus(Duration.ofMinutes(interval)))) {
            stat.removeFirst();
        }
    }
}
