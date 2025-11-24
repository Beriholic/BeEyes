package cv.beriholic.beeyes.models.dto;

import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class RuntimeInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3106752249394283876L;

    private String id;
    private Integer status;
    private RuntimeInfo info;
}
