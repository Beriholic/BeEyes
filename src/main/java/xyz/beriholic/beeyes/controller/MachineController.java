package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.NewMachineVO;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;
import xyz.beriholic.beeyes.service.MachineService;

import java.util.List;

@RestController
@RequestMapping("/api/machine")
public class MachineController {
    @Resource
    MachineService service;

    @GetMapping("/list")
    public RestBean<List<Client>> listMachine() {
        List<Client> list = service.list();
        return RestBean.success(list);
    }

    @PostMapping("/new")
    public RestBean<String> newMachine(
            @RequestBody @Valid NewMachineVO vo
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
}
