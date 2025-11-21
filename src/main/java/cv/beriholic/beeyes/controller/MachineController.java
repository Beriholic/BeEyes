package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.dto.MachineView;
import cv.beriholic.beeyes.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/machine")
@RequiredArgsConstructor
public class MachineController {
    private final MachineService machineService;

    @GetMapping("/list")
    public RestBean<PageDTO<List<MachineView>>> getMachineList(int pageIndex, int pageSize) {
        if (pageIndex < 0 || pageSize < 0) {
            return RestBean.failed(ErrorCode.PARAM_INVALID);
        }
        if (pageSize > 20) {
            return RestBean.failed(ErrorCode.PARAM_INVALID.getCode(), "pageSize不能大于10");
        }
        long userId = StpUtil.getLoginIdAsLong();

        PageDTO<List<MachineView>> machineList = machineService.getMachineListByUserId(
                PageDTO.of(userId, pageIndex, pageSize)
        );

        return RestBean.success(machineList);
    }
}
