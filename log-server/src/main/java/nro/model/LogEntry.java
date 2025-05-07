package nro.model;

import java.time.Instant;
import java.util.Map;

public record LogEntry(String logId, Instant timestamp, String level,
                       String event, String message,
                       String stackTrace, Map<String, String> meta) {
}
