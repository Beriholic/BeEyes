package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.dto.*;

import java.util.List;

public interface ManageService {
    PageDTO<List<ManageUserView>> getManageUserList(QueryManageUserListRequest request);

    CreateUserView createUser(CreateUserRequest request);

    void updateUser(UpdateUserRequest request);

    void deleteUser(DeleteUserRequest request);

    ResetUserView resetUserPassword(ResetUserPasswordRequest request);
}
