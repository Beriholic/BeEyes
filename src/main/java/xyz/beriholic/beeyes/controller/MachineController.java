package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;
import xyz.beriholic.beeyes.service.MachineService;

@RestController
@RequestMapping("/api/machine")
public class MachineController {
    @Resource
    MachineService service;


    @PostMapping("/rename")
    public RestBean<Void> rename(
            @RequestBody @Valid RenameClientVO vo
    ) {
        service.rename(vo);
        return RestBean.success();
    }
}
