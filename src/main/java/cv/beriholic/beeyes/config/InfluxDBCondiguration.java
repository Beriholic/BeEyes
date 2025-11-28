package cv.beriholic.beeyes.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBCondiguration {
    @Value("${influx-db.url}")
    private String url;
    @Value("${influx-db.token}")
    private String token;
    @Value("${influx-db.org}")
    private String org;
    @Value("${influx-db.bucket}")
    private String bucket;

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }
}
