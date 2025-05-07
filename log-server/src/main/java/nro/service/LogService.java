package nro.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import nro.model.LogEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    private final CqlSession session;

    public LogService(CqlSession session) {
        this.session = session;
    }

    public List<LogEntry> getAllLogs() {
        List<LogEntry> logs = new ArrayList<>();
        ResultSet rs = session.execute("SELECT * FROM user_logs LIMIT 50");

        for (Row row : rs) {
            logs.add(new LogEntry(
                    row.getString("log_id"),
                    row.getInstant("timestamp"),
                    row.getString("event"),
                    row.getString("level"),
                    row.getString("message"),
                    row.getString("stack_trace"),
                    row.getMap("meta", String.class, String.class)
            ));
        }
        return logs;
    }

}
