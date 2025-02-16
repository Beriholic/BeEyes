package xyz.beriholic.beeyes.entity.vo.response;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class RuntimeInfoHistoryVO {
    /**
     * @FIELD list: []
     * timestamp: date,
     * "cpuUsage": double,
     * "diskUsage": double,
     * "memoryUsage": double,
     * "networkDownloadSpeed": double,
     * "networkUploadSpeed": double,
     * "swapUsage": double
     */
    List<JSONObject> list = new LinkedList<>();
}
