package statistics;

import java.util.Map;

public interface EventsStatistics<T> {
    void incEvent(String name);
    T getEventStatisticsByName(String name); // возвращает статистику по названию события
    Map<String, T> getAllEventsStatistics();
    void printStatistics();
}
