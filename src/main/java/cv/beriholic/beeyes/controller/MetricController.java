package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.helper.ValidateHelper;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.dto.RuntimeInfoDTO;
import cv.beriholic.beeyes.models.entity.dto.QueryMachineRuntimeInfoRequest;
import cv.beriholic.beeyes.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
