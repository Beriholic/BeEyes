package cv.beriholic.beeyes.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerStatusUpdatedDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8071363579101298322L;

    private Long id;
    private Integer status;
}
