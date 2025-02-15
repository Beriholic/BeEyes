package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.vo.request.MachineUpdateVO;
import xyz.beriholic.beeyes.entity.vo.request.NewMachineVO;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;

public interface MachineService extends IService<Machine> {
    void renameMachine(@Valid RenameClientVO vo);

    String newMachine(@Valid NewMachineVO vo);

    void deleteMachine(Long id);

    void updateMachine(@Valid MachineUpdateVO vo);
}
