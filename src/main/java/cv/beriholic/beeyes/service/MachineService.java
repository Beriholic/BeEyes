package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.dto.MachineView;

import java.util.List;

public interface MachineService {
    PageDTO<List<MachineView>> getMachineListByUserId(PageDTO<Long> userIdPage);
}
