package cv.beriholic.beeyes.repository;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import cv.beriholic.beeyes.consts.HistoryTimeUnit;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;


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

    public List<MachineRuntimeInfoDTO> queryHistoricalRuntimeInfo(Long serverId, Integer time, HistoryTimeUnit timeUnit) {
        String query = buildQuery(serverId, time, timeUnit);

        List<FluxTable> tables = client.getQueryApi().query(query, org);
        if (CollectionUtils.isEmpty(tables)) {
            return Collections.emptyList();
        }

        List<FluxRecord> records = tables.getFirst().getRecords();
        List<MachineRuntimeInfoDTO> result = Lists.newArrayListWithCapacity(records.size());

        for (int i = 0; i < records.size(); i++) {
            JSONObject object = new JSONObject();
            for (FluxTable table : tables) {
                FluxRecord record = table.getRecords().get(i);
                object.put(record.getField(), record.getValue());
            }
            MachineRuntimeInfoDTO dto = object.to(MachineRuntimeInfoDTO.class);
            dto.setServerId(serverId);
            dto.setTimestamp(records.get(i).getTime());
            result.add(dto);
        }
        return result;
    }

    @NotNull
    private String buildQuery(Long serverId, Integer time, HistoryTimeUnit timeUnit) {
        String windowInterval = getWindowInterval(timeUnit);

        String query = """
                from(bucket: "%s")
                |> range(start: -%s%s)
                |> filter(fn: (r) => r["_measurement"] == "machine_runtime_info")
                |> filter(fn: (r) => r["serverId"] == "%s")
                |> aggregateWindow(every: %s, fn: mean, createEmpty: false)
                """;

        query = String.format(
                query,
                bucket,
                time,
                timeUnit.getUnit(),
                serverId,
                windowInterval
        );
        return query;
    }

    private String getWindowInterval(HistoryTimeUnit timeUnit) {
        return switch (timeUnit) {
            case MINUTE -> "1m";
            case HOUR -> "5m";
            case DAY -> "1h";
            case WEEK -> "6h";
            case MONTH -> "1d";
        };
    }
}