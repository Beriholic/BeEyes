package cv.beriholic.beeyes.controller;

import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import cv.beriholic.beeyes.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/runtime")
@RequiredArgsConstructor
public class MachineController {
    private final MetricService metricService;


    @GetMapping("/current/{id}")
    public RestBean<RuntimeInfo> getMachineCurrentRuntimeInfo(@PathVariable String id) {
        RuntimeInfo currentRuntimeInfo = metricService.getMachineRuntimeInfoById(Long.valueOf(id));
        if (Objects.isNull(currentRuntimeInfo)) {
            return RestBean.failed(ErrorCode.RECORD_NOT_FOUND);
        }
        return RestBean.success(currentRuntimeInfo);
    }
}
