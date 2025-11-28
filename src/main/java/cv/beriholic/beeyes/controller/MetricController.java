package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.helper.ValidateHelper;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.dto.RuntimeInfoDTO;
import cv.beriholic.beeyes.models.entity.dto.QueryMachineRuntimeHistoryRequest;
import cv.beriholic.beeyes.models.entity.dto.QueryMachineRuntimeInfoRequest;
import cv.beriholic.beeyes.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/metric")
@RequiredArgsConstructor
public class MetricController {

    private final MetricService metricService;

    @PostMapping("/runtime/current")
    public RestBean<List<RuntimeInfoDTO>> queryMachineRuntimeInfo(
            @RequestBody QueryMachineRuntimeInfoRequest request
    ) {
        ValidateHelper.validateQueryMachinePageParam(request.getPageIndex(), request.getPageSize());
        Long userId = StpUtil.getLoginIdAsLong();
        List<RuntimeInfoDTO> runtimeInfoList = metricService.queryMachineRuntimeInfo(userId, request);
        return RestBean.success(runtimeInfoList);
    }

    @GetMapping("/runtime/history")
    public RestBean<List<MachineRuntimeInfoDTO>> queryMachineHistoryRuntimeInfo(
            QueryMachineRuntimeHistoryRequest request
    ) {
        ValidateHelper.validateQueryMachineHistoryRuntimeInfoRequest(request);
        Long userId = StpUtil.getLoginIdAsLong();
        List<MachineRuntimeInfoDTO> runtimeInfoList = metricService.queryMachineRuntimeHistory(userId, request);
        return RestBean.success(runtimeInfoList);
    }
}
