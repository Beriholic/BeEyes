package cv.beriholic.beeyes.helper;

import cv.beriholic.beeyes.consts.HistoryTimeUnit;
import cv.beriholic.beeyes.models.entity.dto.*;
import org.apache.commons.lang3.StringUtils;

public class ValidateHelper {
    private static final int QUERY_MACHINE_MAX_PAGE_SIZE = 20;

    public static void validateQueryPageParam(int pageIndex, int pageSize) {
        if (pageIndex < 0 || pageSize < 0) {
            throw new IllegalArgumentException("Page index or page size cannot be negative");
        }
        if (pageSize > QUERY_MACHINE_MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size cannot be greater than " + QUERY_MACHINE_MAX_PAGE_SIZE);
        }
    }

    public static void validateAuthLoginRequest(AuthLoginRequest request) {
        if (StringUtils.isEmpty(request.getPassword())) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (StringUtils.isEmpty(request.getUsername()) && StringUtils.isEmpty(request.getPhone())
                && StringUtils.isEmpty(request.getEmail())
        ) {
            throw new IllegalArgumentException("Login condition cannot be empty");
        }
    }

    public static void validateUpdateSSHConfigRequest(UpdateSSHConfigRequest request) {
        if (StringUtils.isEmpty(request.getName())
                || StringUtils.isEmpty(request.getPassword())
        ) {
            throw new IllegalArgumentException("Name or Password cannot be empty");
        }
    }

    public static void validateQueryMachineHistoryRuntimeInfoRequest(QueryMachineRuntimeHistoryRequest request) {
        if (StringUtils.isEmpty(request.getMachineId())) {
            throw new IllegalArgumentException("Machine Id cannot be empty");
        }
        if (request.getTime() < 1) {
            throw new IllegalArgumentException("Time cannot be less than 1");
        }

        HistoryTimeUnit historyTimeUnit = HistoryTimeUnit.of(request.getTimeUnit());
        boolean validTimeUnit = false;

        switch (historyTimeUnit) {
            case MINUTE ->
                    validTimeUnit = (request.getTime() == 5 || request.getTime() == 15 || request.getTime() == 30);
            case HOUR ->
                    validTimeUnit = (request.getTime() == 1 || request.getTime() == 2 || request.getTime() == 3 || request.getTime() == 6 || request.getTime() == 12);
            case DAY -> validTimeUnit = (request.getTime() == 1 || request.getTime() == 3);
            case WEEK -> validTimeUnit = (request.getTime() == 1 || request.getTime() == 2 || request.getTime() == 3);
            case MONTH ->
                    validTimeUnit = (request.getTime() == 1 || request.getTime() == 3 || request.getTime() == 6 || request.getTime() == 12);
        }

        if (!validTimeUnit) {
            throw new IllegalArgumentException("Invalid time unit or time value");
        }
    }

    public static void validateCreateUserRequest(CreateUserRequest request) {
        if (StringUtils.isEmpty(request.getUsername())) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (StringUtils.isEmpty(request.getFullName())) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (StringUtils.isEmpty(request.getPhone())) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
    }

    public static void validateUpdateUserRequest(UpdateUserRequest request) {
        if (StringUtils.isEmpty(request.getUserId())) {
            throw new IllegalArgumentException("User Id cannot be empty");
        }
        if (StringUtils.isEmpty(request.getUsername())) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (StringUtils.isEmpty(request.getFullName())) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (StringUtils.isEmpty(request.getPhone())) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
    }

    public static void validateDeleteUserRequest(DeleteUserRequest request) {
        if (StringUtils.isEmpty(request.getUserId())) {
            throw new IllegalArgumentException("User Id cannot be empty");
        }
    }

    public static void validateResetUserPasswordRequest(ResetUserPasswordRequest request) {
        if (StringUtils.isEmpty(request.getUserId())) {
            throw new IllegalArgumentException("User Id cannot be empty");
        }
    }
}
