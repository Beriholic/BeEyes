package cv.beriholic.beeyes.helper;

import cv.beriholic.beeyes.models.entity.dto.AuthLoginRequest;
import org.apache.commons.lang3.StringUtils;

public class ValidateHelper {
    public static void validatePageParma(int pageIndex, int pageSize, int maxPage) {
        if (pageIndex < 0 || pageSize < 0) {
            throw new IllegalArgumentException("Page index or page size cannot be negative");
        }
        if (pageSize > maxPage) {
            throw new IllegalArgumentException("Page size cannot be greater than " + maxPage);
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
