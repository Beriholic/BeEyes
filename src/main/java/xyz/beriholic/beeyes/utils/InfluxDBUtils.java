package xyz.beriholic.beeyes.utils;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.entity.dto.RuntimeInfoDB;

@Component
public class InfluxDBUtils {
    @Value("${spring.influx-db.url}")
    private String url;
    @Value("${spring.influx-db.token}")
    private String token;
    @Value("${spring.influx-db.bucket}")
    private String bucket;
    @Value("${spring.influx-db.org}")
    private String org;
    private InfluxDBClient client;

    @PostConstruct
    public void initClient() {
        client = InfluxDBClientFactory.create(url, token.toCharArray());
    }

    public void writeRuntimeInfo(RuntimeInfoDB info) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, info);
    }
}
