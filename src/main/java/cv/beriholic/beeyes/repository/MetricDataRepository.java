package cv.beriholic.beeyes.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MetricDataRepository {
    private final InfluxDBClient client;

    @Value("${influx-db.bucket}")
    private String bucket;
    @Value("${influx-db.org}")
    private String org;

    public void recordRuntimeInfo(MachineRuntimeInfoDTO machineRuntimeInfoDTO) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, machineRuntimeInfoDTO);
    }
}
