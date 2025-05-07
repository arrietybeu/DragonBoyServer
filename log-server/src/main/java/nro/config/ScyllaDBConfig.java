package nro.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ScyllaDBConfig {

    @Bean
    public CqlSession scyllaSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress("178.128.107.27", 9042))
                .withLocalDatacenter("datacenter1")
                .withKeyspace("logs_keyspace")
                .build();
    }

}
