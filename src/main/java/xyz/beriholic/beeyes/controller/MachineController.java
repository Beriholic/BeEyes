package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.vo.request.*;
import xyz.beriholic.beeyes.entity.vo.response.MachineInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.SSHInfoSaveVO;
import xyz.beriholic.beeyes.service.MachineService;

import java.util.List;

@RestController
@RequestMapping("/api/machine")
public class MachineController {
    @Resource
    MachineService service;

    @GetMapping("/list")
    public RestBean<List<Machine>> listMachine() {
        List<Machine> list = service.list();
        return RestBean.success(list);
    }

    @PostMapping("/new")
    public RestBean<String> newMachine(
            @RequestBody @Valid MachineNewVO vo
    ) {
        String token = service.newMachine(vo);
        return RestBean.success(token);
    }


    @PostMapping("/rename")
    public RestBean<Void> renameMachine(
            @RequestBody @Valid RenameClientVO vo
    ) {
        service.renameMachine(vo);
        return RestBean.success();
    }

    @PostMapping("/delete")
    public RestBean<Void> deleteMachine(
            @RequestBody @Valid MachineDeleteVO vo
    ) {
        service.deleteMachine(vo.getId());
        return RestBean.success();
    }

    @PostMapping("/update")
    public RestBean<Void> updateMachine(
            @RequestBody @Valid MachineUpdateVO vo
    ) {
        service.updateMachine(vo);
        return RestBean.success();
    }

    @GetMapping("/info")
    public RestBean<MachineInfoVO> machineInfo(
            @RequestParam long id
    ) {
        MachineInfoVO vo = service.machineInfo(id);
        return RestBean.success(vo);
    }

    @GetMapping("/ssh/info/{id}")
    public RestBean<SSHInfoVO> sshInfo(
            @PathVariable long id) {
        SSHInfoVO vo = service.sshInfo(id);
        return RestBean.success(vo);
    }

    @PostMapping("/ssh/save")
    public RestBean<Void> saveSSHInfo(
            @RequestBody @Valid SSHInfoSaveVO vo
    ) {
        service.saveSSHInfo(vo);
        return RestBean.success();
    }
}
