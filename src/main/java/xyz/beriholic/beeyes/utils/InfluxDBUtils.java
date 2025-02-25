package xyz.beriholic.beeyes.utils;

import com.alibaba.fastjson2.JSONObject;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.entity.dto.RuntimeInfoDB;
import xyz.beriholic.beeyes.entity.vo.response.RuntimeInfoHistoryVO;

import java.util.List;

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

    public RuntimeInfoHistoryVO readRuntimeInfo(long clientId,int timeline) {
        RuntimeInfoHistoryVO vo = new RuntimeInfoHistoryVO();
        String query = """
                from(bucket: "%s")
                |> range(start: %s)
                |> filter(fn: (r) => r["_measurement"] == "runtime_info" )
                |> filter(fn: (r) => r["clientId"] == "%s" )
                """;
        query = String.format(query, bucket, "-"+timeline+"d", clientId);

        List<FluxTable> tables = client.getQueryApi().query(query, org);

        if (tables.isEmpty()) return vo;

        List<FluxRecord> records = tables.getFirst().getRecords();


        for (int i = 0; i < records.size(); i++) {
            JSONObject object = new JSONObject();
            object.put("timestamp", records.get(i).getTime());
            for (FluxTable table : tables) {
                FluxRecord record = table.getRecords().get(i);
                object.put(record.getField(), record.getValue());
            }
            vo.getList().add(object);
        }
        return vo;
    }
}
