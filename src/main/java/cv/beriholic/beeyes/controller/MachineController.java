package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.helper.ValidateHelper;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.dto.*;
import cv.beriholic.beeyes.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/machine")
@RequiredArgsConstructor
public class MachineController {
    private final MachineService machineService;

    @GetMapping("/list")
    public RestBean<PageDTO<List<MachineView>>> getMachineList(QueryMachineListRequest request) {
        ValidateHelper.validateQueryMachinePageParam(request.getPageIndex(), request.getPageSize());
        long userId = StpUtil.getLoginIdAsLong();

        PageDTO<List<MachineView>> machineList = machineService.queryMachineListOrderByStatus(userId, request);
        return RestBean.success(machineList);
    }

    @PostMapping("/manage/create")
    public RestBean<Void> createMachine(@RequestBody CreateMachineRequest request) {
        if (StringUtils.isEmpty(request.getDescription()) || StringUtils.isEmpty(request.getRegion())) {
            return RestBean.failed(ErrorCode.PARAM_INVALID);
        }
        Long userId = StpUtil.getLoginIdAsLong();
        machineService.createMachine(userId, request);
        return RestBean.success();
    }

    @PostMapping("/manage/update")
    public RestBean<Void> updateMachine(@RequestBody UpdateMachineRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        machineService.updateMachine(userId, request);
        return RestBean.success();
    }

    @GetMapping("/manage/list")
    public RestBean<PageDTO<List<MachineManageView>>> getMachineManageList(QueryMachineManageListRequest request) {
        ValidateHelper.validateQueryMachinePageParam(request.getPageIndex(), request.getPageSize());
        long userId = StpUtil.getLoginIdAsLong();

        PageDTO<List<MachineManageView>> machineList = machineService.queryMachineManageList(userId, request);
        return RestBean.success(machineList);
    }

    @DeleteMapping("/machine/delete")
    public RestBean<Void> deleteMachine(@RequestBody DeleteMachineRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        machineService.deleteMachine(userId, request);
        return RestBean.success();
    }
}
