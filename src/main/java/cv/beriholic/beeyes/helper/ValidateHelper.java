package cv.beriholic.beeyes.helper;

import cv.beriholic.beeyes.models.entity.dto.AuthLoginRequest;
import org.apache.commons.lang3.StringUtils;

public class ValidateHelper {
    private static final int QUERY_MACHINE_MAX_PAGE_SIZE = 20;

    public static void validateQueryMachinePageParam(int pageIndex, int pageSize) {
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
}
