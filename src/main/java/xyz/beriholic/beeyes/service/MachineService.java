package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;

public interface MachineService extends IService<Client> {
    void rename(@Valid RenameClientVO vo);
}
