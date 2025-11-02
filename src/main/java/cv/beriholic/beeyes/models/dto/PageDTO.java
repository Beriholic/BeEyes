package cv.beriholic.beeyes.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    private T data;
    private Integer pageIndex;
    private Integer pageSize;

    public static <T> PageDTO<T> of(T data) {
        return new PageDTO<>(data, 0, 10);
    }

    public PageDTO<T> of(Integer pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        return this;
    }
}
